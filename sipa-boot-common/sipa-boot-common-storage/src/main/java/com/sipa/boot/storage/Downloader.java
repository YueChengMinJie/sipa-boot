package com.sipa.boot.storage;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.storage.aspect.DownloadAspectChain;
import com.sipa.boot.storage.aspect.DownloadThAspectChain;
import com.sipa.boot.storage.aspect.FileStorageAspect;
import com.sipa.boot.storage.platform.FileStorage;
import com.sipa.boot.storage.progress.ProgressListener;
import com.sipa.boot.storage.progress.SimpleProgressListener;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;

/**
 * 下载器
 * 
 * @author caszhou
 * @date 2022/12/23
 */
public class Downloader {
    /**
     * 下载目标：文件
     */
    public static final int TARGET_FILE = 1;

    /**
     * 下载目标：缩略图文件
     */
    public static final int TARGET_TH_FILE = 2;

    private final FileStorage fileStorage;

    private final List<FileStorageAspect> aspectList;

    private final FileInfo fileInfo;

    private final Integer target;

    private ProgressListener progressListener;

    /**
     * 构造下载器
     *
     * @param target
     *            下载目标：{@link Downloader#TARGET_FILE}下载文件，{@link Downloader#TARGET_TH_FILE}下载缩略图文件
     */
    public Downloader(FileInfo fileInfo, List<FileStorageAspect> aspectList, FileStorage fileStorage, Integer target) {
        this.fileStorage = fileStorage;
        this.aspectList = aspectList;
        this.fileInfo = fileInfo;
        this.target = target;
    }

    /**
     * 设置下载进度监听器
     * 
     * @param progressListener
     *            提供一个参数，表示已传输字节数
     */
    public Downloader setProgressMonitor(Consumer<Long> progressListener) {
        return setProgressMonitor((progressSize, allSize) -> progressListener.accept(progressSize));
    }

    /**
     * 设置下载进度监听器
     */
    public Downloader setProgressMonitor(BiConsumer<Long, Long> progressListener) {
        return setProgressMonitor(new SimpleProgressListener(progressListener));
    }

    /**
     * 设置下载进度监听器
     */
    public Downloader setProgressMonitor(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    /**
     * 获取 InputStream ，在此方法结束后会自动关闭 InputStream
     */
    public void inputStream(Consumer<InputStream> consumer) {
        if (target == TARGET_FILE) {
            // 下载文件
            new DownloadAspectChain(aspectList,
                (chainFileInfo, chainFileStorage, chainConsumer) -> chainFileStorage.download(chainFileInfo,
                    chainConsumer)).next(fileInfo, fileStorage,
                        in -> consumer.accept(progressListener == null ? in
                            : new ProgressInputStream(in, progressListener, fileInfo.getSize())));
        } else if (target == TARGET_TH_FILE) {
            // 下载缩略图文件
            new DownloadThAspectChain(aspectList,
                (chainFileInfo, chainFileStorage, chainConsumer) -> chainFileStorage.downloadTh(chainFileInfo,
                    chainConsumer)).next(fileInfo, fileStorage,
                        in -> consumer.accept(progressListener == null ? in
                            : new ProgressInputStream(in, progressListener, fileInfo.getThSize())));
        } else {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.NO_CORRESPONDING_DOWNLOAD_TARGET_FOUND);
        }
    }

    /**
     * 下载 byte 数组
     */
    public byte[] bytes() {
        byte[][] bytes = new byte[1][];
        inputStream(in -> bytes[0] = IoUtil.readBytes(in));
        return bytes[0];
    }

    /**
     * 下载到指定文件
     */
    public void file(File file) {
        inputStream(in -> FileUtil.writeFromStream(in, file));
    }

    /**
     * 下载到指定文件
     */
    public void file(String fileName) {
        inputStream(in -> FileUtil.writeFromStream(in, fileName));
    }

    /**
     * 下载到指定输出流
     */
    public void outputStream(OutputStream out) {
        inputStream(in -> IoUtil.copy(in, out));
    }
}
