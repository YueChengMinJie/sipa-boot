package com.sipa.boot.lock.lock.jedis;

import java.net.SocketTimeoutException;

import com.sipa.boot.lock.client.jedis.RedisJedisClient;
import com.sipa.boot.lock.client.jedis.RedisJedisRenewTask;
import com.sipa.boot.lock.lock.BaseLock;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * redis 锁
 * 
 * @author caszhou
 * @date 2023/4/20
 */
@Slf4j
public class RedisJedisLock extends BaseLock {
    private final static String RESULT_OK = "OK";

    /**
     * redis jedis 客户端
     */
    RedisJedisClient client;

    /**
     * 续租线程
     */
    RedisJedisRenewTask renewalTask;

    public RedisJedisLock(RedisJedisClient client, String key, boolean reentrant) {
        super(key, UUID.randomUUID().toString(), reentrant);
        this.client = client;
    }

    @Override
    protected boolean lock() {
        try {
            // 抢锁
            if (RESULT_OK.equals(client.setNxPx(key, value, this.ttl))) {
                renewalTask = new RedisJedisRenewTask(() -> {
                    // 刷新值
                    client.expire(key, ttl <= 0 ? 10 : ttl);
                }, ttl);
                renewalTask.setDaemon(true);
                renewalTask.start();
                hold.set(true);
            } else {
                hold.set(false);
            }
        } catch (Exception e) {
            hold.set(false);
            Throwable cause = e.getCause();
            if (cause instanceof SocketTimeoutException) {
                return hold.get();
            }
            log.error("Error encountered when attempting to acquire lock", e);
            throw e;
        } finally {
            if (reentrant && hold.get()) {
                HOLD_LOCKS.get().add(key);
            }
        }
        return hold.get();
    }

    @Override
    public void release() {
        if (hold.get()) {
            hold.set(false);
            try {
                client.delete(key, value);
                HOLD_LOCKS.get().remove(key);
            } finally {
                if (renewalTask != null) {
                    renewalTask.close();
                }
            }
        }
    }

    @Override
    protected Object getClient() {
        return client;
    }
}
