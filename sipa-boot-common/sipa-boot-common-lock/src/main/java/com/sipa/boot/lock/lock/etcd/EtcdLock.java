package com.sipa.boot.lock.lock.etcd;

import java.net.SocketTimeoutException;

import com.sipa.boot.lock.client.etcd.EtcdClient;
import com.sipa.boot.lock.client.etcd.EtcdResponse;
import com.sipa.boot.lock.client.jedis.RedisJedisRenewTask;
import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemException;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.lock.lock.BaseLock;
import com.sipa.boot.lock.property.LockProperty;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * etcd锁对象：
 *
 * @author caszhou
 * @date 2023/4/20
 */
@Slf4j
public class EtcdLock extends BaseLock {
    protected EtcdClient client;

    /**
     * 续租线程
     */
    private RedisJedisRenewTask hbThread = null;

    EtcdLock(EtcdClient client, String key, boolean reentrant) {
        super(LockProperty.self().getEtcd().getClusterName() + "_" + key, "EtcdLock_Name_" + RandomUtil.randomInt(),
            reentrant);
        this.client = client;
    }

    @Override
    protected boolean lock() {
        try {
            // 抢锁
            EtcdResponse etcdResult = client.casVal(key, value, this.ttl);
            if (etcdResult != null && !etcdResult.isError() && httpOk(etcdResult)) {
                hbThread = new RedisJedisRenewTask(() -> {
                    EtcdResponse result = client.casExist(key, value, ttl);
                    if (result.isError()) {
                        close();
                    }
                }, this.ttl);
                hbThread.setDaemon(true);
                hbThread.start();
                hold.set(true);
                if (log.isDebugEnabled()) {
                    log.debug(key + "抢锁成功!");
                }
            } else {
                hold.set(false);
                if (etcdResult != null && etcdResult.isError() && etcdResult.getErrorCode() > HttpStatus.HTTP_OK) {
                    log.error(JSONUtil.toJsonStr(etcdResult));
                    throw SystemExceptionFactory.sysException(ESystemErrorCode.ETCD_HTTP_ERROR,
                        String.valueOf(etcdResult.getErrorCode()));
                }
            }
        } catch (SystemException e) {
            hold.set(false);
            Throwable cause = e.getCause();
            if (cause instanceof SocketTimeoutException) {
                return hold.get();
            }
            log.error("Error encountered when attempting to acquire lock", e);
        } finally {
            if (reentrant && hold.get()) {
                HOLD_LOCKS.get().add(key);
            }
        }
        return hold.get();
    }

    private static boolean httpOk(EtcdResponse etcdResult) {
        return etcdResult.getHttpCode() == HttpStatus.HTTP_CREATED || etcdResult.getHttpCode() == HttpStatus.HTTP_OK;
    }

    @Override
    public void release() {
        if (hold.get()) {
            try {
                hold.set(false);
                HOLD_LOCKS.get().remove(key);
                client.casDelete(key, value);
                if (log.isDebugEnabled()) {
                    log.debug(key + "放锁成功!");
                }
            } catch (SystemException e) {
                log.warn(StringUtils.EMPTY, e);
            } finally {
                if (hbThread != null) {
                    hbThread.close();
                }
            }
        }
    }

    @Override
    protected Object getClient() {
        return client;
    }
}
