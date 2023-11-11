package com.sipa.boot.storage.platform;

import static com.jcraft.jsch.ChannelSftp.SSH_FX_NO_SUCH_FILE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.sipa.boot.storage.FileInfo;
import com.sipa.boot.storage.UploadPretreatment;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.ssh.JschRuntimeException;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.extra.ssh.Sftp;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * SFTP 存储
 *
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Getter
@Setter
public class SftpFileStorage implements FileStorage {
    /**
     * 主机
     */
    private String host;

    /**
     * 端口，默认22
     */
    private int port;

    /**
     * 用户名
     */
    private String user;

    /**
     * 密码，默认空
     */
    private String password;

    /**
     * 私钥路径，默认空
     */
    private String privateKeyPath;

    /**
     * 编码，默认UTF-8
     */
    private Charset charset;

    /**
     * 连接超时时长，单位毫秒，默认10秒
     */
    private long connectionTimeout;

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
    public Sftp getClient() {
        Session session = null;
        try {
            if (StrUtil.isNotBlank(privateKeyPath)) {
                // 使用秘钥连接，这里手动读取 byte 进行构造用于兼容Spring的ClassPath路径、文件路径、HTTP路径等
                byte[] passphrase = StrUtil.isBlank(password) ? null : password.getBytes(StandardCharsets.UTF_8);
                JSch jsch = new JSch();
                byte[] privateKey = IoUtil.readBytes(URLUtil.url(privateKeyPath).openStream());
                jsch.addIdentity(privateKeyPath, privateKey, null, passphrase);
                session = JschUtil.createSession(jsch, host, port, user);
                session.connect((int)connectionTimeout);
            } else {
                session = JschUtil.openSession(host, port, user, password, (int)connectionTimeout);
            }
            return new Sftp(session, charset, connectionTimeout);
        } catch (Exception e) {
            JschUtil.close(session);
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.CLIENT_CANNOT_CONNECTED, platform, host, port);
        }
    }

    @Override
    public void close() {}

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

        Sftp client = getClient();
        try (InputStream in = pre.fileWrapper().getInputStream()) {
            String path = getAbsolutePath(combine(basePath, fileInfo.getPath()));
            if (!client.exist(path)) {
                client.mkDirs(path);
            }
            client.upload(path, fileInfo.getFileName(), in);

            byte[] thumbnailBytes = pre.thumbnailBytes();
            // 上传缩略图
            if (thumbnailBytes != null) {
                String newThFileKey = combine(basePath, fileInfo.getPath(), fileInfo.getThFileName());
                fileInfo.setThUrl(combine(domain, newThFileKey));
                client.upload(path, fileInfo.getThFileName(), new ByteArrayInputStream(thumbnailBytes));
            }

            return true;
        } catch (IOException | JschRuntimeException e) {
            try {
                client.delFile(getAbsolutePath(newFileKey));
            } catch (JschRuntimeException ignored) {
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
        try (Sftp client = getClient()) {
            // 删除缩略图
            if (fileInfo.getThFileName() != null) {
                delFile(client,
                    getAbsolutePath(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getThFileName())));
            }
            delFile(client,
                getAbsolutePath(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName())));
            return true;
        } catch (JschRuntimeException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_DELETE_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    public void delFile(Sftp client, String fileName) {
        try {
            client.delFile(fileName);
        } catch (JschRuntimeException e) {
            if (!(e.getCause() instanceof SftpException && ((SftpException)e.getCause()).id == SSH_FX_NO_SUCH_FILE)) {
                throw e;
            }
        }
    }

    @Override
    public boolean exists(FileInfo fileInfo) {
        try (Sftp client = getClient()) {
            return client
                .exist(getAbsolutePath(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName())));
        } catch (JschRuntimeException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.FILE_EXISTS_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }

    @Override
    public void download(FileInfo fileInfo, Consumer<InputStream> consumer) {
        try (Sftp client = getClient(); InputStream in = client.getClient()
            .get(getAbsolutePath(combine(fileInfo.getBasePath(), fileInfo.getPath(), fileInfo.getFileName())))) {
            consumer.accept(in);
        } catch (IOException | JschRuntimeException | SftpException e) {
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

        try (Sftp client = getClient(); InputStream in = client.getClient()
            .get(getAbsolutePath(combine(fileInfo.getBasePath(), fileInfo.getPath())) + fileInfo.getThFileName())) {
            consumer.accept(in);
        } catch (IOException | JschRuntimeException | SftpException e) {
            log.error(StringUtils.EMPTY, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.THUMBNAIL_DOWNLOAD_FAILED, platform,
                fileInfo.getOriginalFileName());
        }
    }
}
