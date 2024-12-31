package com.sipa.boot.sentinel;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.init.InitExecutor;
import com.sipa.boot.sentinel.gateway.SentinelGatewayEnvProcessor;
import com.sipa.boot.sentinel.gateway.SipaBlockRequestHandler;

/**
 * @author caszhou
 * @date 2024/12/20
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(SentinelGatewayEnvProcessor.class)
public class SentinelGatewayAutoConfiguration {
    @PostConstruct
    private void init() {
        InitExecutor.doInit();
    }

    @Bean
    public BlockRequestHandler sipaBlockRequestHandler() {
        return new SipaBlockRequestHandler();
    }
}
