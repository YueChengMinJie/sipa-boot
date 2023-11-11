package com.sipa.boot.lock.lock.etcd;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.core.exception.tool.SipaAssert;
import com.sipa.boot.lock.Lock;
import com.sipa.boot.lock.LockClient;
import com.sipa.boot.lock.client.etcd.EtcdClient;
import com.sipa.boot.lock.property.LockProperty;

/**
 * etcd 锁方案
 * 
 * @author caszhou
 * @date 2023/4/20
 */
public class EtcdSolution implements LockClient {
    private static final long START = 5;

    private static final long END = TimeUnit.SECONDS.toMillis(2);

    private boolean build;

    private EtcdClient etcdclient;

    private long timeout;

    /**
     * 单位ms，设置后调用acquire方法最大等待时间，非必须设置
     */
    public EtcdSolution timeout(long timeout) {
        SipaAssert.isTrueThenThrow(timeout < START || timeout > END,
            SystemExceptionFactory.bizException(ESystemErrorCode.TIMEOUT_IS_NOT_RIGHT, START, END));
        this.timeout = timeout;
        return this;
    }

    @Override
    public LockClient init() {
        this.timeout = this.timeout == 0 ? 1000 : this.timeout;
        this.etcdclient = EtcdClient.getInstance();
        this.build = true;
        return this;
    }

    @Override
    public Lock newLock(String lockKey) {
        return this.newLock(lockKey, false);
    }

    @Override
    public Lock newLock(String lockKey, boolean reentrant) {
        SipaAssert.isTrueThenThrow(!this.build, SystemExceptionFactory.bizException(ESystemErrorCode.PLEASE_INIT));
        SipaAssert.isTrueThenThrow(StringUtils.isEmpty(LockProperty.self().getEtcd().getClusterName()),
            SystemExceptionFactory.bizException(ESystemErrorCode.CLUSTER_NAME_IS_EMPTY));
        return new EtcdLock(this.etcdclient, lockKey, reentrant);
    }
}
