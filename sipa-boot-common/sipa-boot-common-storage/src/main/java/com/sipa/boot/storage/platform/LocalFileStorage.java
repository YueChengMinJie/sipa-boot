package com.sipa.boot.storage.platform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.storage.FileInfo;
import com.sipa.boot.storage.UploadPretreatment;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 本地文件存储
 * 
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Getter
@Setter
public class LocalFileStorage implements FileStorage {
    /**
     * 基础路径
     */
    private String basePath;

    /**
     * 存储平台
     */
    private String platform;

    /**
     * 访问域名
     */
    private String domain;

    @Override
    public boolean save(FileInfo fileInfo, UploadPretreatment pre) {
        String path = fileInfo.getPath();

        File newFile = FileUtil.touch(combine(basePath, path), fileInfo.getFileName());
        fileInfo.setBasePath(basePath);
        fileInfo.setUrl(combine(domain, path, fileInfo.getFileName()));

        try {
            pre.fileWrapper().transferTo(newFile);

            byte[] thumbnailBytes = pre.thumbnailBytes();
            // 上传缩略图
            if (thumbnailBytes != null) {
                fileInfo.setThUrl(combine(domain, path, fileInfo.getThFileName()));
                FileUtil.writeBytes(thumbnailBytes, combine(basePath, path, fileInfo.getThFileName()));
            }
            return true;
        } catch (IOException e) {
            FileUtil.del(newFile);
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_UPLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        // 删除缩略图
        if (fileInfo.getThFileName() != null) {
            FileUtil.del(new File(combine(fileInfo.getBasePath(), fileInfo.getPath()), fileInfo.getThFileName()));
        }
        return FileUtil.del(new File(combine(fileInfo.getBasePath(), fileInfo.getPath()), fileInfo.getFileName()));
    }

    @Override
    public boolean exists(FileInfo fileInfo) {
        return new File(combine(fileInfo.getBasePath(), fileInfo.getPath()), fileInfo.getFileName()).exists();
    }

    @Override
    public void download(FileInfo fileInfo, Consumer<InputStream> consumer) {
        try (InputStream in =
            FileUtil.getInputStream(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName()))) {
            consumer.accept(in);
        } catch (IOException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_DOWNLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public void downloadTh(FileInfo fileInfo, Consumer<InputStream> consumer) {
        if (StrUtil.isBlank(fileInfo.getThFileName())) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.THUMBNAIL_NOT_EXISTS, platform,
                fileInfo.getOriginalFileName());
        }
        try (InputStream in =
            FileUtil.getInputStream(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getThFileName()))) {
            consumer.accept(in);
        } catch (IOException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.THUMBNAIL_DOWNLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }
}
