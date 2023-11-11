package com.sipa.boot.storage.aspect;

import java.util.Iterator;

import com.sipa.boot.storage.platform.FileStorage;
import com.sipa.boot.storage.recorder.FileRecorder;
import com.sipa.boot.storage.FileInfo;

import lombok.Getter;
import lombok.Setter;

/**
 * 删除的切面调用链
 * 
 * @author caszhou
 * @date 2022/12/23
 */
@Getter
@Setter
public class DeleteAspectChain {
    private DeleteAspectChainCallback callback;

    private Iterator<FileStorageAspect> aspectIterator;

    public DeleteAspectChain(Iterable<FileStorageAspect> aspects, DeleteAspectChainCallback callback) {
        this.aspectIterator = aspects.iterator();
        this.callback = callback;
    }

    /**
     * 调用下一个切面
     */
    public boolean next(FileInfo fileInfo, FileStorage fileStorage, FileRecorder fileRecorder) {
        // 还有下一个
        if (aspectIterator.hasNext()) {
            return aspectIterator.next().deleteAround(this, fileInfo, fileStorage, fileRecorder);
        } else {
            return callback.run(fileInfo, fileStorage, fileRecorder);
        }
    }
}
