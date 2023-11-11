package com.sipa.boot.lock.client.etcd;

import java.util.List;

import com.alibaba.fastjson2.JSON;

/**
 * 节点数据对象
 * 
 * @author caszhou
 * @date 2023/4/20
 */
public class EtcdNodeData {
    public String key;

    public long createdIndex;

    public long modifiedIndex;

    public String value;

    public String expiration;

    public Long ttl;

    public boolean dir;

    public List<EtcdNodeData> nodes;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
