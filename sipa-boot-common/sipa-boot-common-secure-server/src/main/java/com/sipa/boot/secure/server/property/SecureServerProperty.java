package com.sipa.boot.secure.server.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import com.sipa.boot.core.constant.TcpCloudConstant;

import lombok.Data;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = TcpCloudConstant.SecureServer.PATTERN)
public class SecureServerProperty {
    private String authPattern = "/**";

    private String[] springDocPattern =
        {"/v3/api-docs", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/login/**"};
}
