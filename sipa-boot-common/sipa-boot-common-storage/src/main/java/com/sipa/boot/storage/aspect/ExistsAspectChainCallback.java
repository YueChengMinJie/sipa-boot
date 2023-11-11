package com.sipa.boot.storage.aspect;

import com.sipa.boot.storage.platform.FileStorage;
import com.sipa.boot.storage.FileInfo;

/**
 * 文件是否存在切面调用链结束回调
 * 
 * @author caszhou
 * @date 2022/12/23
 */
public interface ExistsAspectChainCallback {
    boolean run(FileInfo fileInfo, FileStorage fileStorage);
}
