package com.sipa.boot.storage.aspect;

import java.io.InputStream;
import java.util.Iterator;
import java.util.function.Consumer;

import com.sipa.boot.storage.platform.FileStorage;
import com.sipa.boot.storage.FileInfo;

import lombok.Getter;
import lombok.Setter;

/**
 * 下载缩略图的切面调用链
 * 
 * @author caszhou
 * @date 2022/12/23
 */
@Getter
@Setter
public class DownloadThAspectChain {
    private DownloadThAspectChainCallback callback;

    private Iterator<FileStorageAspect> aspectIterator;

    public DownloadThAspectChain(Iterable<FileStorageAspect> aspects, DownloadThAspectChainCallback callback) {
        this.aspectIterator = aspects.iterator();
        this.callback = callback;
    }

    /**
     * 调用下一个切面
     */
    public void next(FileInfo fileInfo, FileStorage fileStorage, Consumer<InputStream> consumer) {
        // 还有下一个
        if (aspectIterator.hasNext()) {
            aspectIterator.next().downloadThAround(this, fileInfo, fileStorage, consumer);
        } else {
            callback.run(fileInfo, fileStorage, consumer);
        }
    }
}
