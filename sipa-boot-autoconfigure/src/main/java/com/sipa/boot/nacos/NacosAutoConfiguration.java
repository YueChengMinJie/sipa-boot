package com.sipa.boot.nacos;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration;
import com.sipa.boot.nacos.shared.SipaNacosDiscoveryProperties;
import com.sipa.boot.nacos.shared.SipaNacosServiceDiscovery;

/**
 * @author caszhou
 * @date 2023/4/4
 */
@ConditionalOnDiscoveryEnabled
@ConditionalOnNacosDiscoveryEnabled
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(SipaNacosDiscoveryProperties.class)
@AutoConfigureBefore({NacosDiscoveryAutoConfiguration.class})
public class NacosAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public SipaNacosDiscoveryProperties nacosProperties() {
        return new SipaNacosDiscoveryProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public SipaNacosServiceDiscovery nacosServiceDiscovery(SipaNacosDiscoveryProperties nacosProperties,
        NacosServiceManager nacosServiceManager) {
        return new SipaNacosServiceDiscovery(nacosProperties, nacosServiceManager);
    }
}
