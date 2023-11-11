package com.sipa.boot.storage.platform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.function.Consumer;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.storage.FileInfo;
import com.sipa.boot.storage.UploadPretreatment;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpConfig;
import cn.hutool.extra.ftp.FtpMode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * FTP 存储
 * 
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Getter
@Setter
public class FtpFileStorage implements FileStorage {
    /**
     * 主机
     */
    private String host;

    /**
     * 端口，默认21
     */
    private int port;

    /**
     * 用户名，默认 anonymous（匿名）
     */
    private String user;

    /**
     * 密码，默认空
     */
    private String password;

    /**
     * 编码，默认UTF-8
     */
    private Charset charset;

    /**
     * 连接超时时长，单位毫秒，默认10秒 {@link org.apache.commons.net.SocketClient#setConnectTimeout(int)}
     */
    private long connectionTimeout;

    /**
     * Socket连接超时时长，单位毫秒，默认10秒 {@link org.apache.commons.net.SocketClient#setSoTimeout(int)}
     */
    private long soTimeout;

    /**
     * 设置服务器语言，默认空，{@link org.apache.commons.net.ftp.FTPClientConfig#setServerLanguageCode(String)}
     */
    private String serverLanguageCode;

    /**
     * 服务器标识，默认空，{@link org.apache.commons.net.ftp.FTPClientConfig#FTPClientConfig(String)}
     * 例如：org.apache.commons.net.ftp.FTPClientConfig.SYST_NT
     */
    private String systemKey;

    /**
     * 是否主动模式，默认被动模式
     */
    private Boolean isActive = false;

    /**
     * 存储平台
     */
    private String platform;

    private String domain;

    private String basePath;

    private String storagePath;

    /**
     * 不支持单例模式运行，每次使用完了需要销毁
     */
    public Ftp getClient() {
        FtpConfig config = FtpConfig.create()
            .setHost(host)
            .setPort(port)
            .setUser(user)
            .setPassword(password)
            .setCharset(charset)
            .setConnectionTimeout(connectionTimeout)
            .setSoTimeout(soTimeout)
            .setServerLanguageCode(serverLanguageCode)
            .setSystemKey(systemKey);
        return new Ftp(config, isActive ? FtpMode.Active : FtpMode.Passive);
    }

    /**
     * 获取远程绝对路径
     */
    public String getAbsolutePath(String path) {
        return combine(storagePath, path);
    }

    @Override
    public boolean save(FileInfo fileInfo, UploadPretreatment pre) {
        String newFileKey = combine(basePath, fileInfo.getPath(), fileInfo.getFileName());
        fileInfo.setKey(newFileKey);
        fileInfo.setBasePath(basePath);
        fileInfo.setUrl(combine(domain, newFileKey));

        Ftp client = getClient();
        try (InputStream in = pre.fileWrapper().getInputStream()) {
            client.upload(getAbsolutePath(combine(basePath, fileInfo.getPath())), fileInfo.getFileName(), in);

            byte[] thumbnailBytes = pre.thumbnailBytes();
            // 上传缩略图
            if (thumbnailBytes != null) {
                String newThFileKey = combine(basePath, fileInfo.getPath(), fileInfo.getThFileName());
                fileInfo.setThUrl(combine(domain, newThFileKey));
                client.upload(getAbsolutePath(combine(basePath, fileInfo.getPath())), fileInfo.getThFileName(),
                    new ByteArrayInputStream(thumbnailBytes));
            }

            return true;
        } catch (IOException | IORuntimeException e) {
            try {
                client.delFile(getAbsolutePath(newFileKey));
            } catch (IORuntimeException ignored) {
            }
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_UPLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        } finally {
            IoUtil.close(client);
        }
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        try (Ftp client = getClient()) {
            // 删除缩略图
            if (fileInfo.getThFileName() != null) {
                client.delFile(
                    getAbsolutePath(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getThFileName())));
            }
            client
                .delFile(getAbsolutePath(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName())));
            return true;
        } catch (IOException | IORuntimeException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_DELETE_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public boolean exists(FileInfo fileInfo) {
        try (Ftp client = getClient()) {
            return client.existFile(
                getAbsolutePath(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName())));
        } catch (IOException | IORuntimeException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_EXISTS_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public void download(FileInfo fileInfo, Consumer<InputStream> consumer) {
        try (Ftp client = getClient()) {
            client.cd(getAbsolutePath(combine(fileInfo.getBasePath(), fileInfo.getPath())));
            try (InputStream in = client.getClient().retrieveFileStream(fileInfo.getFileName())) {
                if (in == null) {
                    throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_DOWNLOAD_FAILED_NOT_EXISTS,
                        platform, fileInfo.getOriginalFileName());
                }
                consumer.accept(in);
            }
        } catch (IOException | IORuntimeException e) {
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

        try (Ftp client = getClient()) {
            client.cd(getAbsolutePath(combine(fileInfo.getBasePath(), fileInfo.getPath())));
            try (InputStream in = client.getClient().retrieveFileStream(fileInfo.getThFileName())) {
                if (in == null) {
                    throw SystemExceptionFactory.bizException(ESystemErrorCode.THUMBNAIL_DOWNLOAD_FAILED_NOT_EXISTS,
                        platform, fileInfo.getOriginalFileName());
                }
                consumer.accept(in);
            }
        } catch (IOException | IORuntimeException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.THUMBNAIL_DOWNLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }
}
