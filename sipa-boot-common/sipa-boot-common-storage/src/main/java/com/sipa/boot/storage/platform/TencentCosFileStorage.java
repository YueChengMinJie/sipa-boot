package com.sipa.boot.storage.platform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import org.apache.commons.lang3.StringUtils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.region.Region;
import com.sipa.boot.storage.FileInfo;
import com.sipa.boot.storage.UploadPretreatment;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 腾讯云 COS 存储
 * 
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Getter
@Setter
public class TencentCosFileStorage implements FileStorage {
    private String platform;

    private String secretId;

    private String secretKey;

    private String region;

    private String bucketName;

    private String domain;

    private String basePath;

    private COSClient client;

    /**
     * 单例模式运行，不需要每次使用完再销毁了
     */
    public COSClient getClient() {
        if (client == null) {
            COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
            ClientConfig clientConfig = new ClientConfig(new Region(region));
            clientConfig.setHttpProtocol(HttpProtocol.https);
            client = new COSClient(cred, clientConfig);
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

        COSClient client = getClient();
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
        COSClient client = getClient();
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
        COSObject object = getClient().getObject(bucketName,
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
        COSObject object = getClient().getObject(bucketName,
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
