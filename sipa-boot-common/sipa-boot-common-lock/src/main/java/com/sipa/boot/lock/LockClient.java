package com.sipa.boot.lock;

/**
 * 锁客户端
 * 
 * @author caszhou
 * @date 2023/4/20
 */
public interface LockClient {
    /**
     * 获取锁客户端
     */
    LockClient init();

    /**
     * 获取锁
     */
    Lock newLock(String lockKey);

    /**
     * 获取锁
     */
    Lock newLock(String lockKey, boolean reentrant);
}
