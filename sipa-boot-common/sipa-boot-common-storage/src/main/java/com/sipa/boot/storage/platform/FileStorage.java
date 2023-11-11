package com.sipa.boot.storage.platform;

import java.io.InputStream;
import java.util.function.Consumer;

import com.sipa.boot.storage.FileInfo;
import com.sipa.boot.storage.UploadPretreatment;
import com.sipa.boot.storage.util.PathUtil;

/**
 * 文件存储接口，对应各个平台
 * 
 * @author caszhou
 * @date 2022/12/23
 */
public interface FileStorage extends AutoCloseable {
    /**
     * 获取平台
     */
    String getPlatform();

    /**
     * 获取桶
     */
    default String getBucket() {
        return null;
    }

    /**
     * 设置平台
     */
    void setPlatform(String platform);

    /**
     * 保存文件
     */
    boolean save(FileInfo fileInfo, UploadPretreatment pre);

    /**
     * 删除文件
     */
    boolean delete(FileInfo fileInfo);

    /**
     * 文件是否存在
     */
    boolean exists(FileInfo fileInfo);

    /**
     * 下载文件
     */
    void download(FileInfo fileInfo, Consumer<InputStream> consumer);

    /**
     * 下载缩略图文件
     */
    void downloadTh(FileInfo fileInfo, Consumer<InputStream> consumer);

    /**
     * 释放相关资源
     */
    @Override
    default void close() {}

    /**
     * 拼接path
     */
    default String combine(Object... paths) {
        return PathUtil.combine(paths);
    }
}
