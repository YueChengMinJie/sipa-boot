package com.sipa.boot.storage.platform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.Consumer;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import org.apache.commons.lang3.StringUtils;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.sipa.boot.storage.FileInfo;
import com.sipa.boot.storage.UploadPretreatment;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 七牛云 Kodo 存储
 * 
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Getter
@Setter
public class QiniuKodoFileStorage implements FileStorage {
    private static final int ERROR_CODE = 612;

    /**
     * 存储平台
     */
    private String platform;

    private String accessKey;

    private String secretKey;

    private String bucketName;

    private String domain;

    private String basePath;

    private Region region;

    private QiniuKodoClient client;

    /**
     * 单例模式运行，不需要每次使用完再销毁了
     */
    public QiniuKodoClient getClient() {
        if (client == null) {
            client = new QiniuKodoClient(accessKey, secretKey);
        }
        return client;
    }

    /**
     * 仅在移除这个存储平台时调用
     */
    @Override
    public void close() {
        client = null;
    }

    @Override
    public String getBucket() {
        return this.bucketName;
    }

    @Override
    public boolean save(FileInfo fileInfo, UploadPretreatment pre) {
        String newFileKey = combine(basePath, fileInfo.getPath(), fileInfo.getFileName());
        fileInfo.setKey(newFileKey);
        fileInfo.setBasePath(basePath);
        fileInfo.setUrl(combine(domain, newFileKey));

        try (InputStream in = pre.fileWrapper().getInputStream()) {
            QiniuKodoClient client = getClient();
            UploadManager uploadManager = client.getUploadManager();
            String token = client.getAuth().uploadToken(bucketName);
            uploadManager.put(in, newFileKey, token, null, fileInfo.getContentType());

            byte[] thumbnailBytes = pre.thumbnailBytes();
            // 上传缩略图
            if (thumbnailBytes != null) {
                String newThFileKey = combine(basePath, fileInfo.getPath(), fileInfo.getThFileName());
                fileInfo.setThUrl(combine(domain, newThFileKey));
                uploadManager.put(new ByteArrayInputStream(thumbnailBytes), newThFileKey, token, null,
                    fileInfo.getThContentType());
            }

            return true;
        } catch (IOException e) {
            try {
                client.getBucketManager().delete(bucketName, newFileKey);
            } catch (QiniuException ignored) {
            }
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_UPLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        BucketManager manager = getClient().getBucketManager();
        try {
            // 删除缩略图
            if (fileInfo.getThFileName() != null) {
                delete(manager, combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getThFileName()));
            }
            delete(manager, combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName()));
        } catch (QiniuException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_DELETE_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
        return true;
    }

    public void delete(BucketManager manager, String fileName) throws QiniuException {
        try {
            manager.delete(bucketName, fileName);
        } catch (QiniuException e) {
            if (!(e.response != null && e.response.statusCode == ERROR_CODE)) {
                throw e;
            }
        }
    }

    @Override
    public boolean exists(FileInfo fileInfo) {
        BucketManager manager = getClient().getBucketManager();
        try {
            com.qiniu.storage.model.FileInfo stat =
                manager.stat(bucketName, combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName()));
            if (stat != null && stat.md5 != null) {
                return true;
            }
        } catch (QiniuException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_EXISTS_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
        return false;
    }

    @Override
    public void download(FileInfo fileInfo, Consumer<InputStream> consumer) {
        String url = getClient().getAuth().privateDownloadUrl(fileInfo.getUrl());
        try (InputStream in = new URL(url).openStream()) {
            consumer.accept(in);
        } catch (IOException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_DOWNLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public void downloadTh(FileInfo fileInfo, Consumer<InputStream> consumer) {
        if (StrUtil.isBlank(fileInfo.getThUrl())) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.THUMBNAIL_NOT_EXISTS, platform,
                fileInfo.getOriginalFileName());
        }
        String url = getClient().getAuth().privateDownloadUrl(fileInfo.getThUrl());
        try (InputStream in = new URL(url).openStream()) {
            consumer.accept(in);
        } catch (IOException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.THUMBNAIL_DOWNLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Getter
    @Setter
    public static class QiniuKodoClient {
        private String accessKey;

        private String secretKey;

        private Auth auth;

        private BucketManager bucketManager;

        private UploadManager uploadManager;

        public QiniuKodoClient(String accessKey, String secretKey) {
            this.accessKey = accessKey;
            this.secretKey = secretKey;
        }

        public Auth getAuth() {
            if (auth == null) {
                auth = Auth.create(accessKey, secretKey);
            }
            return auth;
        }

        public BucketManager getBucketManager() {
            if (bucketManager == null) {
                bucketManager = new BucketManager(getAuth(), new Configuration(Region.autoRegion()));
            }
            return bucketManager;
        }

        public UploadManager getUploadManager() {
            if (uploadManager == null) {
                uploadManager = new UploadManager(new Configuration(Region.autoRegion()));
            }
            return uploadManager;
        }
    }
}
