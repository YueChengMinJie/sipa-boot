package com.sipa.boot.sentinel;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.alibaba.cloud.sentinel.SentinelProperties;
import com.alibaba.cloud.sentinel.SentinelWebAutoConfiguration;
import com.alibaba.cloud.sentinel.custom.SentinelDataSourceHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.sipa.boot.sentinel.server.SentinelServerEnvProcessor;
import com.sipa.boot.sentinel.server.SipaBlockExceptionHandler;
import com.sipa.boot.sentinel.server.SipaSentinelDataSourceHandler;

/**
 * @author caszhou
 * @date 2024/12/20
 */
@Configuration
@ConditionalOnClass(SentinelServerEnvProcessor.class)
@AutoConfigureBefore(value = {SentinelWebAutoConfiguration.class})
public class SentinelServerAutoConfiguration {
    @Bean
    public BlockExceptionHandler blockExceptionHandler() {
        return new SipaBlockExceptionHandler();
    }

    @Bean
    public SentinelDataSourceHandler sentinelDataSourceHandler(DefaultListableBeanFactory beanFactory,
        SentinelProperties sentinelProperties, Environment env) {
        return new SipaSentinelDataSourceHandler(beanFactory, sentinelProperties, env);
    }
}
