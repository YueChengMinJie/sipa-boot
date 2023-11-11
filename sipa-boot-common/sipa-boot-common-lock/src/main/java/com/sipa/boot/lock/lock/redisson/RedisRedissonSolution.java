package com.sipa.boot.lock.lock.redisson;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.core.exception.tool.SipaAssert;
import com.sipa.boot.lock.Lock;
import com.sipa.boot.lock.LockClient;
import com.sipa.boot.lock.client.redisson.RedisRedissonClient;

/**
 * redis 解决方案
 * 
 * @author caszhou
 * @date 2023/4/20
 */
public class RedisRedissonSolution implements LockClient {
    @Override
    public LockClient init() {
        return this;
    }

    @Override
    public Lock newLock(String lockKey) {
        return this.newLock(lockKey, true);
    }

    @Override
    public Lock newLock(String lockKey, boolean reentrant) {
        SipaAssert.isTrueThenThrow(!reentrant,
            SystemExceptionFactory.bizException(ESystemErrorCode.REDISSON_CANNOT_SET_NOT_REENTRANT));
        RedisRedissonClient client = RedisRedissonClient.getInstance();
        return new RedisRedissonLock(client, lockKey);
    }
}
