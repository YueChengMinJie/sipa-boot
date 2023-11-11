package com.sipa.boot.storage.platform;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import org.apache.commons.lang3.StringUtils;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.github.sardine.impl.SardineException;
import com.sipa.boot.storage.FileInfo;
import com.sipa.boot.storage.UploadPretreatment;
import com.sipa.boot.storage.util.PathUtil;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * WebDav 存储
 * 
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Getter
@Setter
public class WebDavFileStorage implements FileStorage {
    private String server;

    private String user;

    private String password;

    private String platform;

    private String domain;

    private String basePath;

    private String storagePath;

    private Sardine client;

    /**
     * 不支持单例模式运行，每次使用完了需要销毁
     */
    public Sardine getClient() {
        if (client == null) {
            client = SardineFactory.begin(user, password);
        }
        return client;
    }

    /**
     * 仅在移除这个存储平台时调用
     */
    @Override
    @SneakyThrows(IOException.class)
    public void close() {
        if (client != null) {
            client.shutdown();
            client = null;
        }
    }

    /**
     * 获取远程绝对路径
     */
    public String getUrl(String path) {
        return PathUtil.join(server, combine(storagePath, path));
    }

    /**
     * 递归创建目录
     */
    public void createDirectory(Sardine client, String path) throws IOException {
        if (!client.exists(path)) {
            createDirectory(client, PathUtil.join(PathUtil.getParent(path), "/"));
            client.createDirectory(path);
        }
    }

    @Override
    public boolean save(FileInfo fileInfo, UploadPretreatment pre) {
        String path = combine(basePath, fileInfo.getPath());
        String newFileKey = combine(path, fileInfo.getFileName());
        fileInfo.setKey(newFileKey);
        fileInfo.setBasePath(basePath);
        fileInfo.setUrl(combine(domain, newFileKey));

        Sardine client = getClient();
        try (InputStream in = pre.fileWrapper().getInputStream()) {
            byte[] bytes = IoUtil.readBytes(in);
            createDirectory(client, getUrl(path));
            client.put(getUrl(newFileKey), bytes);

            byte[] thumbnailBytes = pre.thumbnailBytes();
            // 上传缩略图
            if (thumbnailBytes != null) {
                String newThFileKey = combine(path, fileInfo.getThFileName());
                fileInfo.setThUrl(combine(domain, newThFileKey));
                client.put(getUrl(newThFileKey), thumbnailBytes);
            }

            return true;
        } catch (IOException | IORuntimeException e) {
            try {
                client.delete(getUrl(newFileKey));
            } catch (IOException ignored) {
            }
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_UPLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        Sardine client = getClient();
        try {
            // 删除缩略图
            if (fileInfo.getThFileName() != null) {
                try {
                    client
                        .delete(getUrl(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getThFileName())));
                } catch (SardineException e) {
                    if (e.getStatusCode() != HttpStatus.HTTP_NOT_FOUND) {
                        throw e;
                    }
                }
            }
            try {
                client.delete(getUrl(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName())));
            } catch (SardineException e) {
                if (e.getStatusCode() != HttpStatus.HTTP_NOT_FOUND) {
                    throw e;
                }
            }
            return true;
        } catch (IOException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_DELETE_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public boolean exists(FileInfo fileInfo) {
        try {
            return getClient()
                .exists(getUrl(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName())));
        } catch (IOException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_EXISTS_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public void download(FileInfo fileInfo, Consumer<InputStream> consumer) {
        try (InputStream in =
            getClient().get(getUrl(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName())))) {
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
            getClient().get(getUrl(combine(fileInfo.getBasePath(), fileInfo.getPath())) + fileInfo.getThFileName())) {
            consumer.accept(in);
        } catch (IOException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.THUMBNAIL_DOWNLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }
}
