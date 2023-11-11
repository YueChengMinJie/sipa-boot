package com.sipa.boot.storage.progress;

import java.util.function.BiConsumer;

import lombok.RequiredArgsConstructor;

/**
 * @author caszhou
 * @date 2023/5/30
 */
@RequiredArgsConstructor
public class SimpleProgressListener implements ProgressListener {
    private final BiConsumer<Long, Long> progressListener;

    @Override
    public void start() {}

    @Override
    public void progress(long progressSize, long allSize) {
        progressListener.accept(progressSize, allSize);
    }

    @Override
    public void finish() {}
}
