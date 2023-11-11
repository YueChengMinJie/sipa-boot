package com.sipa.boot.lock.client.jedis;

import com.sipa.boot.core.exception.system.SystemException;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 续租线程
 * 
 * @author caszhou
 * @date 2023/4/20
 */
@Slf4j
public class RedisJedisRenewTask extends Thread {
    public volatile boolean isRunning = true;

    /**
     * 过期时间，单位s
     */
    private final Long ttl;

    private final RedisJedisRenewalHandler call;

    public RedisJedisRenewTask(RedisJedisRenewalHandler call, Long ttl) {
        this.call = call;
        this.ttl = ttl <= 0 ? 10 : ttl;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                // 1、续租，刷新值
                call.callBack();
                log.debug("续租成功!");
                // 2、三分之一过期时间续租
                ThreadUtil.safeSleep(this.ttl * 333);
            } catch (SystemException e) {
                close();
            }
        }
    }

    public void close() {
        isRunning = false;
    }
}
