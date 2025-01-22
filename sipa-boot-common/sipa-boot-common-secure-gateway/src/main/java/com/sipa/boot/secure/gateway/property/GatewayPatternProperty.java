package com.sipa.boot.secure.gateway.property;

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
@ConfigurationProperties(prefix = SipaBootConstant.Gateway.PATTERN)
public class GatewayPatternProperty {
    private String authPattern = "";

    private String[] springDocPattern = {};

    private String[] authExcludePattern = {};

    private String[] backListPattern = {};
}
