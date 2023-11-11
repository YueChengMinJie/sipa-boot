package com.sipa.boot.storage.platform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.storage.FileInfo;
import com.sipa.boot.storage.UploadPretreatment;

import cn.hutool.core.util.StrUtil;
import io.minio.*;
import io.minio.errors.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * MinIO 存储
 * 
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Getter
@Setter
public class MinIoFileStorage implements FileStorage {
    private static final String NO_SUCH_KEY = "NoSuchKey";

    /**
     * 存储平台
     */
    private String platform;

    private String accessKey;

    private String secretKey;

    private String endPoint;

    private String bucketName;

    private String domain;

    private String basePath;

    private MinioClient client;

    /**
     * 单例模式运行，不需要每次使用完再销毁了
     */
    public MinioClient getClient() {
        if (client == null) {
            client = new MinioClient.Builder().credentials(accessKey, secretKey).endpoint(endPoint).build();
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

        MinioClient client = getClient();
        try (InputStream in = pre.fileWrapper().getInputStream()) {
            client.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(newFileKey)
                .stream(in, pre.fileWrapper().getSize(), -1)
                .contentType(fileInfo.getContentType())
                .build());

            byte[] thumbnailBytes = pre.thumbnailBytes();
            // 上传缩略图
            if (thumbnailBytes != null) {
                String newThFileKey = combine(basePath, fileInfo.getPath(), fileInfo.getThFileName());
                fileInfo.setThUrl(combine(domain, newThFileKey));
                client.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(newThFileKey)
                    .stream(new ByteArrayInputStream(thumbnailBytes), thumbnailBytes.length, -1)
                    .contentType(fileInfo.getThContentType())
                    .build());
            }

            return true;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | ServerException
            | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException
            | XmlParserException e) {
            try {
                client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(newFileKey).build());
            } catch (Exception ignored) {
            }
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_UPLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        MinioClient client = getClient();
        try {
            // 删除缩略图
            if (fileInfo.getThFileName() != null) {
                client.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getThFileName()))
                    .build());
            }
            client.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName()))
                .build());
            return true;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | ServerException
            | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException
            | XmlParserException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_DELETE_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public boolean exists(FileInfo fileInfo) {
        MinioClient client = getClient();
        try {
            StatObjectResponse stat = client.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName()))
                .build());
            return stat != null && stat.lastModified() != null;
        } catch (ErrorResponseException e) {
            String code = e.errorResponse().code();
            if (NO_SUCH_KEY.equals(code)) {
                return false;
            }
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_EXISTS_FAILED, platform,
                fileInfo.getOriginalFileName());
        } catch (InsufficientDataException | InternalException | ServerException | InvalidKeyException
            | InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_EXISTS_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public void download(FileInfo fileInfo, Consumer<InputStream> consumer) {
        MinioClient client = getClient();
        try (InputStream in = client.getObject(GetObjectArgs.builder()
            .bucket(bucketName)
            .object(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName()))
            .build())) {
            consumer.accept(in);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | ServerException
            | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException
            | XmlParserException e) {
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
        MinioClient client = getClient();
        try (InputStream in = client.getObject(GetObjectArgs.builder()
            .bucket(bucketName)
            .object(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getThFileName()))
            .build())) {
            consumer.accept(in);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | ServerException
            | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException
            | XmlParserException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.THUMBNAIL_DOWNLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }
}
