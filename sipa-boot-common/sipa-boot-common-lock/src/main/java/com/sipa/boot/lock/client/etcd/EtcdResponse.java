package com.sipa.boot.lock.client.etcd;

import com.alibaba.fastjson2.JSON;

import lombok.Data;

/**
 * 节点响应对象
 * 
 * @author caszhou
 * @date 2023/4/20
 */
@Data
public class EtcdResponse {
    /**
     * http状态码
     */
    private Integer httpCode = 200;

    /**
     * 服务吗
     */
    private Integer serverCode;

    private String action;

    /**
     * 节点数据
     */
    private EtcdNodeData node;

    /**
     * 前任节点数据
     */
    private EtcdNodeData prevNode;

    /**
     * 错误码
     */
    private Integer errorCode;

    /**
     * 消息
     */
    private String message;

    /**
     * 异常原因
     */
    private String cause;

    /**
     * 错误索引
     */
    private int errorIndex;

    public boolean isError() {
        return errorCode != null;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
