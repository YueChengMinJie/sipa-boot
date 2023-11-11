package com.sipa.boot.xxljob.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.sipa.boot.core.constant.TcpCloudConstant;

import lombok.Data;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Data
@ConfigurationProperties(prefix = TcpCloudConstant.Xxljob.PREFIX)
public class XxljobProperty {
    /**
     * admin集群地址
     */
    private String adminAddresses;

    /**
     * token，默认为f03f6254-8143-4a89-b169-844f34a18787
     */
    private String accessToken = "f03f6254-8143-4a89-b169-844f34a18787";

    /**
     * 日志保留天数，默认为30天
     */
    private Integer logRetentionDays = 30;
}
