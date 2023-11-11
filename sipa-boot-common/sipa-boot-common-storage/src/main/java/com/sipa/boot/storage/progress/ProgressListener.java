package com.sipa.boot.storage.progress;

/**
 * 进度监听器
 * 
 * @author caszhou
 * @date 2022/12/23
 */
public interface ProgressListener {
    /**
     * 开始
     */
    void start();

    /**
     * 进行中
     *
     * @param progressSize
     *            已经进行的大小
     * @param allSize
     *            总大小，来自 fileInfo.getSize()
     */
    void progress(long progressSize, long allSize);

    /**
     * 结束
     */
    void finish();
}
