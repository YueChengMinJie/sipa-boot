package com.sipa.boot.storage.platform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import org.apache.commons.lang3.StringUtils;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import com.sipa.boot.storage.FileInfo;
import com.sipa.boot.storage.UploadPretreatment;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Getter
@Setter
public class GoogleCloudStorage implements FileStorage {
    private String projectId;

    private String bucketName;

    /**
     * 证书路径，兼容Spring的ClassPath路径、文件路径、HTTP路径等
     */
    private String credentialsPath;

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

    private Storage client;

    /**
     * 单例模式运行，不需要每次使用完再销毁了
     */
    public Storage getClient() {
        if (client == null) {
            ServiceAccountCredentials credentialsFromStream;
            try (InputStream in = URLUtil.url(credentialsPath).openStream()) {
                credentialsFromStream = ServiceAccountCredentials.fromStream(in);
            } catch (IOException e) {
                log.error(StringUtils.EMPTY, e);
                throw SystemExceptionFactory.bizException(ESystemErrorCode.CLIENT_NOT_AUTHORIZED, platform,
                    credentialsPath);
            }
            List<String> scopes = Collections.singletonList("https://www.googleapis.com/auth/cloud-platform");
            ServiceAccountCredentials credentials = credentialsFromStream.toBuilder().setScopes(scopes).build();
            StorageOptions storageOptions =
                StorageOptions.newBuilder().setProjectId(projectId).setCredentials(credentials).build();
            client = storageOptions.getService();
        }
        return client;
    }

    @Override
    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception ignored) {
            }
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
        Storage client = getClient();

        BlobId blobId = BlobId.of(bucketName, newFileKey);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(fileInfo.getContentType()).build();
        try (InputStream in = pre.fileWrapper().getInputStream()) {
            // 上传原文件
            client.createFrom(blobInfo, in);
            // 上传缩略图
            byte[] thumbnailBytes = pre.thumbnailBytes();
            if (thumbnailBytes != null) {
                String newThFileKey = combine(basePath, fileInfo.getPath(), fileInfo.getThFileName());
                fileInfo.setThUrl(combine(domain, newThFileKey));
                BlobId thBlobId = BlobId.of(bucketName, newThFileKey);
                BlobInfo thBlobInfo = BlobInfo.newBuilder(thBlobId).setContentType(fileInfo.getThContentType()).build();
                client.createFrom(thBlobInfo, new ByteArrayInputStream(thumbnailBytes));
            }
            return true;
        } catch (IOException e) {
            checkAndDelete(newFileKey);
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_UPLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    /**
     * 检查并删除对象 <a href=
     * "https://github.com/googleapis/java-storage/blob/main/samples/snippets/src/main/java/com/example/storage/object/DeleteObject.java">Source
     * Example</a>
     *
     * @param fileKey
     *            对象 key
     */
    private void checkAndDelete(String fileKey) {
        Storage client = getClient();
        Blob blob = client.get(bucketName, fileKey);
        if (blob != null) {
            Storage.BlobSourceOption precondition = Storage.BlobSourceOption.generationMatch(blob.getGeneration());
            client.delete(bucketName, fileKey, precondition);
        }
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        // 删除缩略图
        if (fileInfo.getThFileName() != null) {
            checkAndDelete(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getThFileName()));
        }
        checkAndDelete(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName()));
        return true;
    }

    @Override
    public boolean exists(FileInfo fileInfo) {
        Storage client = getClient();
        BlobId blobId =
            BlobId.of(bucketName, combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName()));
        return client.get(blobId) != null;
    }

    @Override
    public void download(FileInfo fileInfo, Consumer<InputStream> consumer) {
        Storage client = getClient();
        BlobId blobId =
            BlobId.of(bucketName, combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName()));
        ReadChannel readChannel = client.reader(blobId);
        InputStream in = Channels.newInputStream(readChannel);
        consumer.accept(in);
    }

    @Override
    public void downloadTh(FileInfo fileInfo, Consumer<InputStream> consumer) {
        if (StrUtil.isBlank(fileInfo.getThFileName())) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.THUMBNAIL_NOT_EXISTS, platform,
                fileInfo.getOriginalFileName());
        }
        Storage client = getClient();
        BlobId thBlobId =
            BlobId.of(bucketName, combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getThFileName()));
        ReadChannel readChannel = client.reader(thBlobId);
        InputStream in = Channels.newInputStream(readChannel);
        consumer.accept(in);
    }
}
