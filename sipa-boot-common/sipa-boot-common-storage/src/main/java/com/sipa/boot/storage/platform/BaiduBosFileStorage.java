package com.sipa.boot.storage.platform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import org.apache.commons.lang3.StringUtils;

import com.baidubce.Protocol;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.BosObject;
import com.baidubce.services.bos.model.ObjectMetadata;
import com.sipa.boot.storage.FileInfo;
import com.sipa.boot.storage.UploadPretreatment;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 百度云 BOS 存储
 * 
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Getter
@Setter
public class BaiduBosFileStorage implements FileStorage {
    private String platform;

    private String accessKey;

    private String secretKey;

    private String endPoint;

    private String bucketName;

    private String domain;

    private String basePath;

    private BosClient client;

    /**
     * 单例模式运行，不需要每次使用完再销毁了
     */
    public BosClient getClient() {
        if (client == null) {
            BosClientConfiguration config = new BosClientConfiguration();
            config.setCredentials(new DefaultBceCredentials(accessKey, secretKey));
            config.setEndpoint(endPoint);
            config.setProtocol(Protocol.HTTPS);
            client = new BosClient(config);
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

        BosClient client = getClient();
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
        BosClient client = getClient();
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
        BosObject object = getClient().getObject(bucketName,
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
        BosObject object = getClient().getObject(bucketName,
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
