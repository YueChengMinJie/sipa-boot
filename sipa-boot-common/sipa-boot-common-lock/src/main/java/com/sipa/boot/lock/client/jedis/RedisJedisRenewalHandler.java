package com.sipa.boot.lock.client.jedis;

import com.sipa.boot.core.exception.system.SystemException;

/**
 * @author caszhou
 * @date 2023/4/20
 */
public interface RedisJedisRenewalHandler {
    void callBack() throws SystemException;
}
