package com.sipa.boot.storage.aspect;

import java.util.Iterator;

import com.sipa.boot.storage.platform.FileStorage;
import com.sipa.boot.storage.recorder.FileRecorder;
import com.sipa.boot.storage.FileInfo;
import com.sipa.boot.storage.UploadPretreatment;

import lombok.Getter;
import lombok.Setter;

/**
 * 上传的切面调用链
 * 
 * @author caszhou
 * @date 2022/12/23
 */
@Getter
@Setter
public class UploadAspectChain {
    private UploadAspectChainCallback callback;

    private Iterator<FileStorageAspect> aspectIterator;

    public UploadAspectChain(Iterable<FileStorageAspect> aspects, UploadAspectChainCallback callback) {
        this.aspectIterator = aspects.iterator();
        this.callback = callback;
    }

    /**
     * 调用下一个切面
     */
    public FileInfo next(FileInfo fileInfo, UploadPretreatment pre, FileStorage fileStorage,
        FileRecorder fileRecorder) {
        // 还有下一个
        if (aspectIterator.hasNext()) {
            return aspectIterator.next().uploadAround(this, fileInfo, pre, fileStorage, fileRecorder);
        } else {
            return callback.run(fileInfo, pre, fileStorage, fileRecorder);
        }
    }
}
