package com.sipa.boot.mqttv3.property;

import lombok.Getter;
import lombok.Setter;

/**
 * MQTT连接配置
 * 
 * @author caszhou
 * @date 2022/6/23
 */
@Setter
@Getter
public class ConnectionProperty {
    private String enabled = "true";

    /**
     * MQTT服务器地址, 必填, 可以配置多个. -- GETTER -- MQTT服务器地址, 必填, 可以配置多个.
     */
    private String[] uri = new String[] {"tcp://127.0.0.1:1883"};

    /**
     * 客户端ID -- GETTER -- 客户端ID
     */
    private String clientId;

    /**
     * 用户名. -- GETTER -- 用户名.
     */
    private String username;

    /**
     * 密码. -- GETTER -- 密码.
     */
    private String password;

    /**
     * 是否启用共享订阅,对于不同的Broker,共享订阅可能无效(EMQ已测可用). -- GETTER -- 是否启用共享订阅,对于不同的Broker,共享订阅可能无效(EMQ已测可用).
     */
    private Boolean enableSharedSubscription;

    /**
     * 发布消息默认使用的QOS, 默认 0. -- GETTER -- 发布消息默认使用的QOS, 默认 2.
     */
    private Integer defaultPublishQos;

    /**
     * 最大重连等待时间(秒). -- GETTER -- 最大重连等待时间(秒).
     */
    private Integer maxReconnectDelay;

    /**
     * KeepAlive 周期(秒). -- GETTER -- KeepAlive 周期(秒).
     */
    private Integer keepAliveInterval;

    /**
     * 连接超时时间(秒). -- GETTER -- 连接超时时间(秒).
     */
    private Integer connectionTimeout;

    /**
     * 发送超时时间(秒). -- GETTER -- 发送超时时间(秒).
     */
    private Integer executorServiceTimeout;

    /**
     * 是否清除会话. -- GETTER -- 是否清除会话.
     */
    private Boolean cleanSession;

    /**
     * 断开是否重新连接. -- GETTER -- 断开是否重新连接.
     */
    private Boolean automaticReconnect;

    /**
     * 遗愿相关配置. -- GETTER -- 遗愿相关配置.
     */
    private WillProperty will;

    /**
     * 最大飞行窗口时间
     */
    private int maxInflight;
}
