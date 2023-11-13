package com.sipa.boot.storage.service;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.web.multipart.MultipartFile;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.core.secure.IdpUserUtil;
import com.sipa.boot.storage.*;
import com.sipa.boot.storage.aspect.DeleteAspectChain;
import com.sipa.boot.storage.aspect.ExistsAspectChain;
import com.sipa.boot.storage.aspect.FileStorageAspect;
import com.sipa.boot.storage.aspect.UploadAspectChain;
import com.sipa.boot.storage.platform.FileStorage;
import com.sipa.boot.storage.property.StorageProperty;
import com.sipa.boot.storage.recorder.FileRecorder;
import com.sipa.boot.storage.tika.TikaFactory;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 用来处理文件存储，对接多个平台
 * 
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Getter
@Setter
public class FileStorageService implements DisposableBean {
    private FileStorageService self;

    private FileRecorder fileRecorder;

    private CopyOnWriteArrayList<FileStorage> fileStorageList;

    private StorageProperty properties;

    private CopyOnWriteArrayList<FileStorageAspect> aspectList;

    private TikaFactory tikaFactory;

    /**
     * 获取默认的存储平台
     */
    public FileStorage getFileStorage() {
        return this.getFileStorage(this.properties.getDefaultPlatform(), null);
    }

    /**
     * 获取对应的存储平台
     */
    public FileStorage getFileStorage(String platform, String bucket) {
        for (FileStorage fileStorage : this.fileStorageList) {
            if (fileStorage.getPlatform().equals(platform) && StringUtils
                .equals(StringUtils.trimToEmpty(fileStorage.getBucket()), StringUtils.trimToEmpty(bucket))) {
                return fileStorage;
            }
        }
        return null;
    }

    /**
     * 获取对应的存储平台，如果存储平台不存在则抛出异常
     */
    public FileStorage getFileStorageVerify(FileInfo fileInfo) {
        FileStorage fileStorage = this.getFileStorage(fileInfo.getPlatform(), fileInfo.getBucket());
        if (fileStorage == null) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.NO_CORRESPONDING_STORAGE_PLATFORM_FOUND);
        }
        return fileStorage;
    }

    /**
     * 上传文件，成功返回文件信息，失败返回 null
     */
    public FileInfo upload(UploadPretreatment pre) {
        MultipartFile file = pre.fileWrapper();
        if (file == null) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_IS_EMPTY);
        }
        if (pre.platform() == null) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.STORAGE_PLATFORM_IS_EMPTY);
        }

        FileInfo fileInfo = new FileInfo();
        fileInfo.setUploadTime(LocalDateTime.now());
        fileInfo.setSize(file.getSize());
        fileInfo.setOriginalFileName(file.getOriginalFilename());
        fileInfo.setExt(FileNameUtil.getSuffix(file.getOriginalFilename()));
        fileInfo.setObjectId(pre.objectId());
        fileInfo.setObjectType(pre.objectType());
        fileInfo.setPath(pre.path());
        fileInfo.setPlatform(pre.platform());
        fileInfo.setBucket(pre.bucket());
        fileInfo.setAttr(pre.getAttr());
        fileInfo.setApplicationId(pre.applicationId());
        fileInfo.setCompanyId(pre.companyId());

        if (StrUtil.isNotBlank(pre.saveFileName())) {
            fileInfo.setFileName(pre.saveThFileName());
        } else {
            fileInfo.setFileName(
                IdUtil.objectId() + (StrUtil.isEmpty(fileInfo.getExt()) ? StrUtil.EMPTY : "." + fileInfo.getExt()));
        }

        if (pre.contentType() != null) {
            fileInfo.setContentType(pre.contentType());
        } else if (pre.fileWrapper().getContentType() != null) {
            fileInfo.setContentType(pre.fileWrapper().getContentType());
        } else {
            fileInfo.setContentType(this.tikaFactory.getTika().detect(fileInfo.getFileName()));
        }

        byte[] thumbnailBytes = pre.thumbnailBytes();
        if (thumbnailBytes != null) {
            fileInfo.setThSize((long)thumbnailBytes.length);
            if (StrUtil.isNotBlank(pre.saveThFileName())) {
                fileInfo.setThFileName(pre.saveThFileName() + pre.thumbnailSuffix());
            } else {
                fileInfo.setThFileName(fileInfo.getFileName() + pre.thumbnailSuffix());
            }
            fileInfo.setThContentType(this.tikaFactory.getTika().detect(thumbnailBytes, fileInfo.getThFileName()));
        }

        FileStorage fileStorage = this.getFileStorage(pre.platform(), pre.bucket());
        if (fileStorage == null) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.NO_CORRESPONDING_STORAGE_PLATFORM_FOUND);
        }

        // 处理切面
        return new UploadAspectChain(this.aspectList,
            (chainFileInfo, chainPre, chainFileStorage, chainFileRecorder) -> {
                if (chainFileStorage.save(chainFileInfo, chainPre)) {
                    if (chainFileRecorder.record(chainFileInfo)) {
                        return chainFileInfo;
                    }
                }
                return null;
            }).next(fileInfo, pre, fileStorage, this.fileRecorder);
    }

    /**
     * 根据 url 获取 FileInfo
     */
    public FileInfo getFileInfoByUrl(String url) {
        return this.fileRecorder.getByUrl(url);
    }

    /**
     * 根据 url 删除文件
     */
    public boolean delete(String url) {
        FileInfo fileInfo = this.getFileInfoByUrl(url);
        if (Objects.isNull(fileInfo)) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_NOT_EXISTS);
        }
        if (!fileInfo.getCreateBy().equals(IdpUserUtil.getId())) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.UNABLE_TO_DELETE_FILE);
        }
        return this.delete(fileInfo);
    }

    /**
     * 根据 url 删除文件
     */
    public boolean delete(String url, Predicate<FileInfo> predicate) {
        FileInfo fileInfo = this.getFileInfoByUrl(url);
        if (Objects.isNull(fileInfo)) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_NOT_EXISTS);
        }
        if (!fileInfo.getCreateBy().equals(IdpUserUtil.getId())) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.UNABLE_TO_DELETE_FILE);
        }
        return this.delete(fileInfo, predicate);
    }

    /**
     * 根据条件
     */
    public boolean delete(FileInfo fileInfo) {
        return this.delete(fileInfo, null);
    }

    /**
     * 根据条件删除文件
     */
    public boolean delete(FileInfo fileInfo, Predicate<FileInfo> predicate) {
        if (fileInfo == null) {
            return true;
        }
        if (predicate != null && !predicate.test(fileInfo)) {
            return false;
        }
        FileStorage fileStorage = this.getFileStorage(fileInfo.getPlatform(), fileInfo.getBucket());
        if (fileStorage == null) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.NO_CORRESPONDING_STORAGE_PLATFORM_FOUND);
        }

        return new DeleteAspectChain(this.aspectList, (chainFileInfo, chainFileStorage, chainFileRecorder) -> {
            if (chainFileStorage.delete(chainFileInfo)) {
                return chainFileRecorder.delete(chainFileInfo.getUrl());
            }
            return false;
        }).next(fileInfo, fileStorage, this.fileRecorder);
    }

    /**
     * 文件是否存在
     */
    public boolean exists(String url) {
        FileInfo fileInfo = this.getFileInfoByUrl(url);
        return !Objects.isNull(fileInfo) && this.exists(fileInfo);
    }

    /**
     * 文件是否存在
     */
    public boolean exists(FileInfo fileInfo) {
        if (fileInfo == null) {
            return false;
        }
        return new ExistsAspectChain(this.aspectList, (_fileInfo, _fileStorage) -> _fileStorage.exists(_fileInfo))
            .next(fileInfo, this.getFileStorageVerify(fileInfo));
    }

    /**
     * 获取文件下载器
     */
    public Downloader download(FileInfo fileInfo) {
        return new Downloader(fileInfo, this.aspectList, this.getFileStorageVerify(fileInfo), Downloader.TARGET_FILE);
    }

    /**
     * 获取文件下载器
     */
    public Downloader download(String url) {
        FileInfo fileInfo = this.getFileInfoByUrl(url);
        if (Objects.isNull(fileInfo)) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_NOT_EXISTS);
        }
        return this.download(fileInfo);
    }

    /**
     * 获取缩略图文件下载器
     */
    public Downloader downloadTh(FileInfo fileInfo) {
        return new Downloader(fileInfo, this.aspectList, this.getFileStorageVerify(fileInfo),
            Downloader.TARGET_TH_FILE);
    }

    /**
     * 获取缩略图文件下载器
     */
    public Downloader downloadTh(String url) {
        FileInfo fileInfo = this.getFileInfoByUrl(url);
        if (Objects.isNull(fileInfo)) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_NOT_EXISTS);
        }
        return this.downloadTh(fileInfo);
    }

    /**
     * 创建上传预处理器
     */
    public UploadPretreatment of() {
        UploadPretreatment pre = new UploadPretreatment();
        pre.fileStorageService(this.self);
        pre.platform(this.properties.getDefaultPlatform());
        pre.thumbnailSuffix(this.properties.getThumbnailSuffix());
        return pre;
    }

    /**
     * 根据 MultipartFile 创建上传预处理器
     */
    public UploadPretreatment of(MultipartFile file) {
        UploadPretreatment pre = this.of();
        pre.fileWrapper(new MultipartFileWrapper(file));
        return pre;
    }

    /**
     * 根据 byte[] 创建上传预处理器，name 为空字符串
     */
    public UploadPretreatment of(byte[] bytes) {
        UploadPretreatment pre = this.of();
        String contentType = this.tikaFactory.getTika().detect(bytes);
        pre.fileWrapper(new MultipartFileWrapper(new MockMultipartFile("", "", contentType, bytes)));
        return pre;
    }

    /**
     * 根据 InputStream 创建上传预处理器，originalFileName 为空字符串
     */
    public UploadPretreatment of(InputStream in) {
        try {
            byte[] bytes = IoUtil.readBytes(in);
            String contentType = this.tikaFactory.getTika().detect(bytes);
            UploadPretreatment pre = this.of();
            pre.fileWrapper(new MultipartFileWrapper(new MockMultipartFile("", "", contentType, bytes)));
            return pre;
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FAILED_TO_CREATE_UPLOAD_PREPROCESSOR,
                "InputStream");
        }
    }

    /**
     * 根据 File 创建上传预处理器，originalFileName 为 file 的 name
     */
    public UploadPretreatment of(File file) {
        try {
            UploadPretreatment pre = this.of();
            String contentType = this.tikaFactory.getTika().detect(file);
            pre.fileWrapper(new MultipartFileWrapper(new MockMultipartFile(file.getName(), file.getName(), contentType,
                Files.newInputStream(file.toPath()))));
            return pre;
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FAILED_TO_CREATE_UPLOAD_PREPROCESSOR, "File");
        }
    }

    /**
     * 根据 URL 创建上传预处理器，originalFileName 将尝试自动识别，识别不到则为空字符串
     */
    public UploadPretreatment of(URL url) {
        try {
            UploadPretreatment pre = this.of();

            URLConnection conn = url.openConnection();

            // 尝试获取文件名
            String name = "";
            String disposition = conn.getHeaderField("Content-Disposition");
            if (StrUtil.isNotBlank(disposition)) {
                name = ReUtil.get("fileName=\"(.*?)\"", disposition, 1);
                if (StrUtil.isBlank(name)) {
                    name = StrUtil.subAfter(disposition, "fileName=", true);
                }
            }
            if (StrUtil.isBlank(name)) {
                final String path = url.getPath();
                name = StrUtil.subSuf(path, path.lastIndexOf('/') + 1);
                if (StrUtil.isNotBlank(name)) {
                    name = URLUtil.decode(name, StandardCharsets.UTF_8);
                }
            }

            byte[] bytes = IoUtil.readBytes(conn.getInputStream());
            String contentType = this.tikaFactory.getTika().detect(bytes, name);
            pre.fileWrapper(new MultipartFileWrapper(new MockMultipartFile(url.toString(), name, contentType, bytes)));
            return pre;
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FAILED_TO_CREATE_UPLOAD_PREPROCESSOR, "URL");
        }
    }

    /**
     * 根据 URI 创建上传预处理器，originalFileName 将尝试自动识别，识别不到则为空字符串
     */
    public UploadPretreatment of(URI uri) {
        try {
            return this.of(uri.toURL());
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FAILED_TO_CREATE_UPLOAD_PREPROCESSOR, "URI");
        }
    }

    /**
     * 根据 url 字符串创建上传预处理器，兼容Spring的ClassPath路径、文件路径、HTTP路径等，originalFileName 将尝试自动识别，识别不到则为空字符串
     */
    public UploadPretreatment of(String url) {
        try {
            return this.of(URLUtil.url(url));
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FAILED_TO_CREATE_UPLOAD_PREPROCESSOR,
                "url:" + url);
        }
    }

    @Override
    public void destroy() {
        for (FileStorage fileStorage : this.fileStorageList) {
            try {
                fileStorage.close();
                log.info("销毁存储平台 [{}] 成功", fileStorage.getPlatform());
            } catch (Exception e) {
                log.error("销毁存储平台 [{}] 失败", fileStorage.getPlatform(), e);
            }
        }
    }
}
