package com.sipa.boot.storage.aspect;

import com.sipa.boot.storage.platform.FileStorage;
import com.sipa.boot.storage.recorder.FileRecorder;
import com.sipa.boot.storage.FileInfo;
import com.sipa.boot.storage.UploadPretreatment;

/**
 * 上传切面调用链结束回调
 * 
 * @author caszhou
 * @date 2022/12/23
 */
public interface UploadAspectChainCallback {
    FileInfo run(FileInfo fileInfo, UploadPretreatment pre, FileStorage fileStorage, FileRecorder fileRecorder);
}
