package com.sipa.boot.lb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author caszhou
 * @date 2023/8/16
 */
@Configuration
@ConditionalOnClass({CanaryLoadBalancer.class})
@LoadBalancerClients(defaultConfiguration = LoadBalancerAutoConfiguration.class)
public class LoadBalancerAutoConfiguration {
    @Bean
    public ReactorLoadBalancer<ServiceInstance> canaryLoadBalancer(Environment environment,
        LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new CanaryLoadBalancer(name,
            loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class));
    }
}
