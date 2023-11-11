package com.sipa.boot.lock.lock.jedis;

import com.sipa.boot.lock.client.jedis.RedisJedisClient;
import com.sipa.boot.lock.Lock;
import com.sipa.boot.lock.LockClient;

/**
 * redis 解决方案
 * 
 * @author caszhou
 * @date 2023/4/20
 */
public class RedisJedisSolution implements LockClient {
    @Override
    public LockClient init() {
        return this;
    }

    @Override
    public Lock newLock(String lockKey) {
        return newLock(lockKey, false);
    }

    @Override
    public Lock newLock(String lockKey, boolean reentrant) {
        RedisJedisClient client = RedisJedisClient.getInstance();
        return new RedisJedisLock(client, lockKey, reentrant);
    }
}
