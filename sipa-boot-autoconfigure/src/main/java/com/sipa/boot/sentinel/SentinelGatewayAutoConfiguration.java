package com.sipa.boot.sentinel;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.sipa.boot.sentinel.gateway.SentinelGatewayEnvProcessor;
import com.sipa.boot.sentinel.gateway.SipaBlockRequestHandler;

/**
 * @author caszhou
 * @date 2024/12/20
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(SentinelGatewayEnvProcessor.class)
public class SentinelGatewayAutoConfiguration {
    @Bean
    public BlockRequestHandler sipaBlockRequestHandler() {
        return new SipaBlockRequestHandler();
    }
}
