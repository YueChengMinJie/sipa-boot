package com.sipa.boot.storage.aspect;

import java.io.InputStream;
import java.util.function.Consumer;

import com.sipa.boot.storage.platform.FileStorage;
import com.sipa.boot.storage.FileInfo;

/**
 * 下载缩略图切面调用链结束回调
 * 
 * @author caszhou
 * @date 2022/12/23
 */
public interface DownloadThAspectChainCallback {
    void run(FileInfo fileInfo, FileStorage fileStorage, Consumer<InputStream> consumer);
}
