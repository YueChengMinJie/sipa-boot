package com.sipa.boot.storage.platform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Consumer;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import org.apache.commons.lang3.StringUtils;

import com.upyun.RestManager;
import com.upyun.UpException;
import com.sipa.boot.storage.FileInfo;
import com.sipa.boot.storage.UploadPretreatment;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 又拍云 USS 存储
 * 
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Getter
@Setter
public class UpyunUssFileStorage implements FileStorage {
    private String platform;

    private String username;

    private String password;

    private String bucketName;

    private String domain;

    private String basePath;

    private RestManager client;

    public RestManager getClient() {
        if (client == null) {
            client = new RestManager(bucketName, username, password);
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

        RestManager manager = getClient();
        try (InputStream in = pre.fileWrapper().getInputStream()) {
            HashMap<String, String> params = new HashMap<>(16);
            params.put(RestManager.PARAMS.CONTENT_TYPE.getValue(), fileInfo.getContentType());
            try (Response result = manager.writeFile(newFileKey, in, params)) {
                if (!result.isSuccessful()) {
                    throw new UpException(result.toString());
                }
            }

            byte[] thumbnailBytes = pre.thumbnailBytes();
            // 上传缩略图
            if (thumbnailBytes != null) {
                String newThFileKey = combine(basePath, fileInfo.getPath(), fileInfo.getThFileName());
                fileInfo.setThUrl(combine(domain, newThFileKey));
                HashMap<String, String> thParams = new HashMap<>(16);
                thParams.put(RestManager.PARAMS.CONTENT_TYPE.getValue(), fileInfo.getThContentType());
                Response thResult = manager.writeFile(newThFileKey, new ByteArrayInputStream(thumbnailBytes), thParams);
                if (!thResult.isSuccessful()) {
                    throw new UpException(thResult.toString());
                }
            }

            return true;
        } catch (IOException | UpException e) {
            try {
                manager.deleteFile(newFileKey, null).close();
            } catch (IOException | UpException ignored) {
            }
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_UPLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        RestManager manager = getClient();
        String file = combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName());
        String thFile = combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getThFileName());

        try (Response ignored = fileInfo.getThFileName() != null ? manager.deleteFile(thFile, null) : null;
            Response ignored2 = manager.deleteFile(file, null)) {
            return true;
        } catch (IOException | UpException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_DELETE_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public boolean exists(FileInfo fileInfo) {
        try (Response response =
            getClient().getFileInfo(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName()))) {
            return StrUtil.isNotBlank(response.header("x-upyun-file-size"));
        } catch (IOException | UpException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_EXISTS_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public void download(FileInfo fileInfo, Consumer<InputStream> consumer) {
        try (
            Response response =
                getClient().readFile(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName()));
            ResponseBody body = response.body(); InputStream in = body == null ? null : body.byteStream()) {
            if (body == null) {
                throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_DOWNLOAD_FAILED_NOT_EXISTS, platform,
                    fileInfo.getOriginalFileName());
            }
            if (!response.isSuccessful()) {
                throw new UpException(IoUtil.read(in, StandardCharsets.UTF_8));
            }
            consumer.accept(in);
        } catch (IOException | UpException e) {
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
        try (
            Response response =
                getClient().readFile(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getThFileName()));
            ResponseBody body = response.body(); InputStream in = body == null ? null : body.byteStream()) {
            if (body == null) {
                throw SystemExceptionFactory.bizException(ESystemErrorCode.THUMBNAIL_DOWNLOAD_FAILED_NOT_EXISTS,
                    platform, fileInfo.getOriginalFileName());
            }
            if (!response.isSuccessful()) {
                throw new UpException(IoUtil.read(in, StandardCharsets.UTF_8));
            }
            consumer.accept(in);
        } catch (IOException | UpException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.THUMBNAIL_DOWNLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }
}
