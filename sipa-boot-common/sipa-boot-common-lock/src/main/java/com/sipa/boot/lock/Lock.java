package com.sipa.boot.lock;

/**
 * 分布式锁对象
 * 
 * @author caszhou
 * @date 2023/4/20
 */
public interface Lock extends AutoCloseable {
    LockFactory factory = new LockFactory();

    /**
     * 获取锁，如果没得到，不阻塞
     */
    boolean acquire(int ttl);

    /**
     * 获取锁，直到超时
     */
    boolean acquire(int ttl, double interval, int maxRetry);

    /**
     * 释放锁
     */
    void release();

    /**
     * 获取锁工厂
     */
    static LockFactory factory() {
        return factory;
    }

    /**
     * 释放相关资源
     */
    @Override
    void close();
}
