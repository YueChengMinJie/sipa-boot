package com.sipa.boot.lock.lock.redisson;

import com.sipa.boot.lock.client.redisson.RedisRedissonClient;
import org.redisson.api.RLock;

import com.sipa.boot.lock.lock.BaseLock;

import cn.hutool.core.lang.UUID;

/**
 * @author caszhou
 * @date 2023/5/27
 */
public class RedisRedissonLock extends BaseLock {
    /**
     * redis redisson 客户端
     */
    RedisRedissonClient client;

    private RLock lock;

    public RedisRedissonLock(RedisRedissonClient client, String key) {
        super(key, UUID.fastUUID().toString(), true);
        this.client = client;
    }

    @Override
    protected boolean lock() {
        lock = client.getLock(key);
        boolean success = lock.tryLock();
        hold.set(success);
        return success;
    }

    @Override
    public void release() {
        if (hold.get()) {
            lock.unlock();
        }
    }

    @Override
    protected Object getClient() {
        return client;
    }
}
