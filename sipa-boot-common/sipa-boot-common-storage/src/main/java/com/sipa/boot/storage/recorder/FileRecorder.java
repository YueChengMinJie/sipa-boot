package com.sipa.boot.storage.recorder;

import com.sipa.boot.storage.FileInfo;

/**
 * 文件记录记录者接口
 * 
 * @author caszhou
 * @date 2022/12/23
 */
public interface FileRecorder {
    /**
     * 保存文件记录
     */
    boolean record(FileInfo fileInfo);

    /**
     * 根据 url 获取文件记录
     */
    FileInfo getByUrl(String url);

    /**
     * 根据 url 删除文件记录
     */
    boolean delete(String url);
}
