package com.sipa.boot.nacos.env;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.sipa.boot.core.constant.SipaBootConstant;
import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.env.EnvConstant;

/**
 * @author caszhou
 * @date 2022-09-14
 */
public class NacosMetadataApplicationContextInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.getBeanFactory().addBeanPostProcessor(new InstantiationAwareBeanPostProcessorAdapter() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof NacosDiscoveryProperties) {
                    NacosDiscoveryProperties nacosDiscoveryProperties = (NacosDiscoveryProperties)bean;
                    Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
                    NacosMetadataApplicationContextInitializer.this.persistMetaInfo(metadata);
                }
                return bean;
            }
        });
    }

    private void persistMetaInfo(Map<String, String> metadata) {
        metadata.put("nacos.version", NacosVersion.getNacosVersion());
        metadata.put("sipa.version", "1.0.0");

        Properties properties = System.getProperties();
        for (String key : properties.stringPropertyNames()) {
            if (StringUtils.startsWithIgnoreCase(key, SipaBootConstant.Core.SIPA_NACOS_PREFIX)) {
                String value = properties.getProperty(key);
                metadata.put(key, StringUtils.isEmpty(value) ? SipaBootConstant.Core.UNKNOWN : value);
            }
        }

        Map<String, String> envs = System.getenv();
        if (MapUtils.isNotEmpty(envs)) {
            String canaryValue = envs.get(EnvConstant.CANARY_NAME.toUpperCase());
            if (StringUtils.isNotBlank(canaryValue)) {
                metadata.put(SipaBootConstant.Core.SIPA_NACOS_PREFIX + EnvConstant.CANARY_NAME,
                    SipaConstant.StringBoolean.TRUE);
            }
        }
    }
}
