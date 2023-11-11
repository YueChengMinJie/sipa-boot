package com.sipa.boot.feign.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.sipa.boot.core.constant.TcpCloudConstant;

import lombok.Data;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Data
@ConfigurationProperties(prefix = TcpCloudConstant.Feign.PREFIX)
public class FeignProperty {
    /**
     * 开关，默认为false
     */
    private String enabled = "false";
}
