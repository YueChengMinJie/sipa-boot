package com.sipa.boot.lock.lock;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.lock.Lock;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/4/20
 */
@Slf4j
public abstract class BaseLock implements Lock {
    /**
     * 默认过期时间，单位s
     */
    protected final static long DEFAULT_TTL_SECOND = 10L;

    /**
     * 默认过期时间，单位s
     */
    protected final static long DEFAULT_SLEEP_MILL = 100L;

    /**
     * 活跃重入锁
     */
    protected static final ThreadLocal<HashSet<String>> HOLD_LOCKS = TransmittableThreadLocal.withInitial(HashSet::new);

    /**
     * 锁名
     */
    protected String key;

    /**
     * 锁值
     */
    protected String value;

    /**
     * 过期时间，单位s
     */
    protected long ttl;

    /**
     * 是否支持锁重入
     */
    protected boolean reentrant;

    /**
     * 是否已经持有锁
     */
    protected final AtomicBoolean hold = new AtomicBoolean(false);

    public BaseLock(String key, String value, boolean reentrant) {
        this.key = key;
        this.value = value;
        this.ttl = DEFAULT_TTL_SECOND;
        this.reentrant = reentrant;
    }

    @Override
    public boolean acquire(int ttl) {
        if (this.getClient() == null || StringUtils.isEmpty(this.key)) {
            // 空锁
            return false;
        } else if (this.reentrant && HOLD_LOCKS.get().contains(this.key)) {
            // 重入锁
            return true;
        }
        this.ttl = (ttl <= 0 ? DEFAULT_TTL_SECOND : ttl);
        StopWatch watcher = new StopWatch();
        boolean isAcquire = this.lock();
        if (watcher.isRunning()) {
            watcher.stop();
            log.info("acquire use {}ms", watcher.getLastTaskTimeMillis());
        }
        return isAcquire;
    }

    @Override
    public boolean acquire(int ttl, double interval, int maxRetry) {
        if (this.getClient() == null || StringUtils.isEmpty(this.key)) {
            // 空锁
            return false;
        } else if (this.reentrant && HOLD_LOCKS.get().contains(this.key)) {
            // 重入锁
            return true;
        }
        this.ttl = (ttl <= 0 ? DEFAULT_TTL_SECOND : ttl);
        try {
            if (!this.lock()) {
                // 重试抢锁
                if (maxRetry > 0) {
                    sleep(interval);
                    return this.acquire(ttl, interval, maxRetry - SipaConstant.Number.INT_1);
                }
            }
        } catch (Exception e) {
            // 重试抢锁
            if (maxRetry > 0) {
                ThreadUtil.safeSleep(getSleepInterval(interval));
                log.info(Thread.currentThread().getName() + "开始睡眠");
                return this.acquire(ttl, interval, maxRetry - SipaConstant.Number.INT_1);
            }
        }
        return this.hold.get();
    }

    private static void sleep(double interval) {
        if (log.isDebugEnabled()) {
            log.debug(Thread.currentThread().getName() + "开始睡眠");
        }
        ThreadUtil.safeSleep(getSleepInterval(interval));
    }

    private static long getSleepInterval(double interval) {
        return interval == 0 ? DEFAULT_SLEEP_MILL : (long)interval * 1000;
    }

    @Override
    public void close() {
        if (this.hold.get()) {
            StopWatch watcher = new StopWatch();
            this.release();
            if (watcher.isRunning()) {
                watcher.stop();
                log.info("close use {}ms", watcher.getLastTaskTimeMillis());
            }
        }
    }

    /**
     * 抢锁
     */
    protected abstract boolean lock();

    /**
     * 释放锁
     */
    @Override
    public abstract void release();

    /**
     * 获取方案客户端
     */
    protected abstract Object getClient();
}
