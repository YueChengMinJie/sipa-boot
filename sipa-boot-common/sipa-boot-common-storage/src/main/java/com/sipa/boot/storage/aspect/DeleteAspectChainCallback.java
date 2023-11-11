package com.sipa.boot.storage.aspect;

import com.sipa.boot.storage.platform.FileStorage;
import com.sipa.boot.storage.recorder.FileRecorder;
import com.sipa.boot.storage.FileInfo;

/**
 * 删除切面调用链结束回调
 * 
 * @author caszhou
 * @date 2022/12/23
 */
public interface DeleteAspectChainCallback {
    boolean run(FileInfo fileInfo, FileStorage fileStorage, FileRecorder fileRecorder);
}
