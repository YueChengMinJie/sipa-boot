package com.sipa.boot.lock.client.etcd;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemException;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.core.exception.tool.SipaAssert;
import com.sipa.boot.lock.property.LockProperty;
import com.sipa.boot.rest.util.RestUtil;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * etcd客户端对象：支持etcd集群节点设置，并支持etcd节点自动探活
 * 
 * @author caszhou
 * @date 2023/4/20
 */
@Slf4j
public class EtcdClient {
    /**
     * 最大重试次数
     */
    private static final int CONNECT_RETRY_COUNT_MAX = 2;

    /**
     * 所有etcd节点
     */
    private final List<String> allEtcdNodes = new ArrayList<>();

    /**
     * 活跃节点列表
     */
    private List<String> availableEtcdNodes = new ArrayList<>();

    /**
     * 失联节点列表
     */
    private List<String> brokenEtcdNodes = new ArrayList<>();

    /**
     * etcd 状态更改重入锁
     */
    private final ReentrantLock changeEtcdNodeStatusLock = new ReentrantLock();

    /**
     * @param baseUrls
     *            etcd节点列表
     */
    private EtcdClient(List<String> baseUrls) {
        SipaAssert.isTrueThenThrow(CollectionUtils.isEmpty(baseUrls),
            SystemExceptionFactory.bizException(ESystemErrorCode.URL_IS_EMPTY));
        for (String baseUrl : baseUrls) {
            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }
            this.allEtcdNodes.add(baseUrl);
        }
        this.availableEtcdNodes.addAll(this.allEtcdNodes);

        // etcd节点心跳任务
        EtcdHeartbeatTask etcdHeartbeatTask = new EtcdHeartbeatTask(this);
        etcdHeartbeatTask.setDaemon(true);
        etcdHeartbeatTask.setName("EtcdHeartbeatTask");
        etcdHeartbeatTask.start();
    }

    public static EtcdClient getInstance() {
        return new EtcdClient(LockProperty.self().getEtcd().getUrls());
    }

    /**
     * etcd 原子赋值操作
     */
    public EtcdResponse casVal(String key, String value, Long ttl) throws SystemException {
        return this.syncPut(key, value, null, false, ttl, 0);
    }

    /**
     * etcd 原子刷新操作
     */
    public EtcdResponse casExist(String key, String value, Long ttl) throws SystemException {
        return this.syncPut(key, null, value, true, ttl, 0);
    }

    /**
     * etcd 原子删除操作
     */
    public EtcdResponse casDelete(String key, String prevValue) throws SystemException {
        return this.syncDelete(key, prevValue, 0);
    }

    /**
     * 同步put请求
     */
    private EtcdResponse syncPut(String key, String value, String prevValue, Boolean exist, Long ttl,
        int connectRetryCount) throws SystemException {
        String uri = this.getUri(key, prevValue);
        String url = this.getRandomAvailableEtcdNode() + uri;

        EtcdRequest request = new EtcdRequest();
        if (!StringUtils.isEmpty(value)) {
            request.setValue(value);
        }
        if (null != exist) {
            request.setPrevExist(exist);
            if (exist) {
                request.setRefresh(true);
            }
        }
        if (ttl != null) {
            request.setTtl(ttl);
        }
        try {
            return RestUtil.put(url, null, request, EtcdResponse.class);
        } catch (RuntimeException e) {
            if (connectRetryCount > CONNECT_RETRY_COUNT_MAX) {
                throw SystemExceptionFactory.sysException(ESystemErrorCode.ETCD_CANNOT_CONNECT, e);
            }
            connectRetryCount++;
            return this.syncPut(key, value, prevValue, exist, ttl, connectRetryCount);
        } catch (Exception e1) {
            throw SystemExceptionFactory.sysException(ESystemErrorCode.ETCD_HTTP_FAIL, e1);
        }
    }

    private String getUri(String key, String prevValue) {
        return StringUtils.isEmpty(prevValue) ? this.buildUri("v2/keys", key)
            : this.buildUri("v2/keys", key).concat("?prevValue=").concat(prevValue);
    }

    /**
     * 同步delete请求
     */
    private EtcdResponse syncDelete(String key, String prevValue, int connectRetryCount) throws SystemException {
        String uri = this.buildUri("v2/keys", key).concat("?prevValue=").concat(prevValue);
        try {
            String url = this.getRandomAvailableEtcdNode() + uri;
            return RestUtil.delete(url, EtcdResponse.class);
        } catch (RuntimeException e) {
            if (connectRetryCount > CONNECT_RETRY_COUNT_MAX) {
                throw SystemExceptionFactory.sysException(ESystemErrorCode.ETCD_CANNOT_CONNECT, e);
            }
            connectRetryCount++;
            return this.syncDelete(key, prevValue, connectRetryCount);
        } catch (Exception e1) {
            throw SystemExceptionFactory.sysException(ESystemErrorCode.ETCD_CANNOT_CONNECT, e1);
        }
    }

    /**
     * 生成请求地址：对锁名进行url编码防止地址错误
     */
    private String buildUri(String prefix, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        if (key.startsWith(SipaConstant.Symbol.SLASH)) {
            key = key.substring(1);
        }
        String[] keys = StringUtils.split(key, SipaConstant.Symbol.SLASH);
        if (null == keys) {
            keys = new String[] {key};
        }
        for (String subKey : keys) {
            sb.append("/");
            sb.append(URLEncoder.encode(subKey, StandardCharsets.UTF_8));
        }
        return URI.create(sb.toString()).getPath();
    }

    /**
     * 追加活跃节点
     */
    void setAvailableEtcdNode(String url) {
        try {
            if (this.changeEtcdNodeStatusLock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    List<String> availableEtcdNodes = new ArrayList<>(this.availableEtcdNodes);
                    List<String> brokenEtcdNodes = new ArrayList<>(this.brokenEtcdNodes);

                    availableEtcdNodes.add(url);
                    brokenEtcdNodes.remove(url);

                    this.availableEtcdNodes = availableEtcdNodes;
                    this.brokenEtcdNodes = brokenEtcdNodes;
                } finally {
                    this.changeEtcdNodeStatusLock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    /**
     * 追加失联节点
     */
    void setBrokenEtcdNode(String url) {
        try {
            if (this.changeEtcdNodeStatusLock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    List<String> availableEtcdNodes = new ArrayList<>(this.availableEtcdNodes);
                    List<String> brokenEtcdNodes = new ArrayList<>(this.brokenEtcdNodes);

                    availableEtcdNodes.remove(url);
                    brokenEtcdNodes.add(url);

                    this.availableEtcdNodes = availableEtcdNodes;
                    this.brokenEtcdNodes = brokenEtcdNodes;
                } finally {
                    this.changeEtcdNodeStatusLock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    /**
     * 获取etcd随机活跃节点
     */
    private String getRandomAvailableEtcdNode() {
        List<String> availableEtcdNodes = this.availableEtcdNodes;
        if (availableEtcdNodes.size() == 0) {
            log.error("Lock all etcd nodes has broken, use  var [allEtcdNodes] instead...");
            availableEtcdNodes = this.allEtcdNodes;
        }
        return availableEtcdNodes.get(RandomUtil.randomInt(0, availableEtcdNodes.size() - 1));
    }

    List<String> getAvailableEtcdNodes() {
        return this.availableEtcdNodes;
    }

    List<String> getBrokenEtcdNodes() {
        return this.brokenEtcdNodes;
    }
}
