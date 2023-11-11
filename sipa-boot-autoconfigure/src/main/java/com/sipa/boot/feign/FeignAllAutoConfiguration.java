package com.sipa.boot.feign;

import com.sipa.boot.feign.apm.ApmRequestInterceptor;
import com.sipa.boot.feign.lb.CanaryInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.lb.CanaryLoadBalancer;

import feign.RequestInterceptor;

/**
 * @author caszhou
 * @date 2023/2/18
 */
@Configuration
@ConditionalOnClass({ApmRequestInterceptor.class})
public class FeignAllAutoConfiguration {
    @Bean
    public RequestInterceptor apmRequestInterceptor() {
        return new ApmRequestInterceptor();
    }

    @Bean
    @ConditionalOnClass(CanaryLoadBalancer.class)
    public RequestInterceptor canaryInterceptor() {
        return new CanaryInterceptor();
    }
}
