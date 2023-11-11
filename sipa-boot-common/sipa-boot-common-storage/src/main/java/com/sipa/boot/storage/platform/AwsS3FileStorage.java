package com.sipa.boot.storage.platform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import org.apache.commons.lang3.StringUtils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.sipa.boot.storage.FileInfo;
import com.sipa.boot.storage.UploadPretreatment;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * AWS S3 存储
 * 
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Getter
@Setter
public class AwsS3FileStorage implements FileStorage {
    private String platform;

    private String accessKey;

    private String secretKey;

    private String region;

    private String endPoint;

    private String bucketName;

    private String domain;

    private String basePath;

    private AmazonS3 client;

    /**
     * 单例模式运行，不需要每次使用完再销毁了
     */
    public AmazonS3 getClient() {
        if (client == null) {
            AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)));
            if (StrUtil.isNotBlank(endPoint)) {
                builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, region));
            } else if (StrUtil.isNotBlank(region)) {
                builder.withRegion(region);
            }
            client = builder.build();
        }
        return client;
    }

    /**
     * 仅在移除这个存储平台时调用
     */
    @Override
    public void close() {
        if (client != null) {
            client.shutdown();
            client = null;
        }
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

        AmazonS3 client = getClient();
        try (InputStream in = pre.fileWrapper().getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileInfo.getSize());
            metadata.setContentType(fileInfo.getContentType());
            client.putObject(bucketName, newFileKey, in, metadata);

            byte[] thumbnailBytes = pre.thumbnailBytes();
            // 上传缩略图
            if (thumbnailBytes != null) {
                String newThFileKey = combine(basePath, fileInfo.getPath(), fileInfo.getThFileName());
                fileInfo.setThUrl(combine(domain, newThFileKey));
                ObjectMetadata thMetadata = new ObjectMetadata();
                thMetadata.setContentLength(thumbnailBytes.length);
                thMetadata.setContentType(fileInfo.getThContentType());
                client.putObject(bucketName, newThFileKey, new ByteArrayInputStream(thumbnailBytes), thMetadata);
            }

            return true;
        } catch (IOException e) {
            client.deleteObject(bucketName, newFileKey);
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_UPLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        AmazonS3 client = getClient();
        // 删除缩略图
        if (fileInfo.getThFileName() != null) {
            client.deleteObject(bucketName,
                combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getThFileName()));
        }
        client.deleteObject(bucketName, combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName()));
        return true;
    }

    @Override
    public boolean exists(FileInfo fileInfo) {
        return getClient().doesObjectExist(bucketName,
            combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName()));
    }

    @Override
    public void download(FileInfo fileInfo, Consumer<InputStream> consumer) {
        S3Object object = getClient().getObject(bucketName,
            combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName()));
        try (InputStream in = object.getObjectContent()) {
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
        S3Object object = getClient().getObject(bucketName,
            combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getThFileName()));
        try (InputStream in = object.getObjectContent()) {
            consumer.accept(in);
        } catch (IOException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.THUMBNAIL_DOWNLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }
}
