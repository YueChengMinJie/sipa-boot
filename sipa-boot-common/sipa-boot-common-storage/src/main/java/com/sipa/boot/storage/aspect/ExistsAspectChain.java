package com.sipa.boot.storage.aspect;

import java.util.Iterator;

import com.sipa.boot.storage.platform.FileStorage;
import com.sipa.boot.storage.FileInfo;

import lombok.Getter;
import lombok.Setter;

/**
 * 文件是否存在的切面调用链
 * 
 * @author caszhou
 * @date 2022/12/23
 */
@Getter
@Setter
public class ExistsAspectChain {
    private ExistsAspectChainCallback callback;

    private Iterator<FileStorageAspect> aspectIterator;

    public ExistsAspectChain(Iterable<FileStorageAspect> aspects, ExistsAspectChainCallback callback) {
        this.aspectIterator = aspects.iterator();
        this.callback = callback;
    }

    /**
     * 调用下一个切面
     */
    public boolean next(FileInfo fileInfo, FileStorage fileStorage) {
        // 还有下一个
        if (aspectIterator.hasNext()) {
            return aspectIterator.next().existsAround(this, fileInfo, fileStorage);
        } else {
            return callback.run(fileInfo, fileStorage);
        }
    }
}
