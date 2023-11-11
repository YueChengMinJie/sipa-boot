package com.sipa.boot.rest.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import com.sipa.boot.core.constant.SipaBootConstant;

import lombok.Data;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = SipaBootConstant.Rest.KEY)
public class RestProperty {
    /**
     * 连接超时
     */
    private int connectTimeout = 30000;

    /**
     * 获取连接超时
     */
    private int requestTimeout = 30000;

    /**
     * 请求超时
     */
    private int socketTimeout = 60000;

    /**
     * 最大连接数
     */
    private int maxTotalConnections = 50;

    /**
     * 长连接配置key
     */
    private String keepAliveSettingKey = "timeout";

    /**
     * 长连接超时时间
     */
    private int defaultKeepAliveTimeMillis = 20 * 1000;

    /**
     * 最大空闲时间
     */
    private int closeIdleConnectionWaitTimeSecs = 30;

    /**
     * IO错误重试
     */
    private int ioExceptionRetryTime = 3;
}
