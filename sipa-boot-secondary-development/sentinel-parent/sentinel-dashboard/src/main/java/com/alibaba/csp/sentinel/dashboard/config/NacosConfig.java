package com.alibaba.csp.sentinel.dashboard.config;

import static com.alibaba.nacos.api.PropertyKeyConst.*;

import java.util.Objects;
import java.util.Properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;

/**
 * @author caszhou
 * @date 2024/12/19
 */
@Configuration
@EnableConfigurationProperties(NacosProperties.class)
public class NacosConfig {
    @Bean
    public ConfigService configService(NacosProperties nacosProperties) throws NacosException {
        return NacosFactory.createConfigService(getNacosProperties(nacosProperties));
    }

    private Properties getNacosProperties(NacosProperties nacosProperties) {
        Properties properties = new Properties();
        properties.put(SERVER_ADDR, Objects.toString(nacosProperties.serverAddr, ""));
        properties.put(USERNAME, Objects.toString(nacosProperties.username, ""));
        properties.put(PASSWORD, Objects.toString(nacosProperties.password, ""));
        properties.put(ENCODE, Objects.toString(nacosProperties.encode, ""));
        properties.put(NAMESPACE, Objects.toString(nacosProperties.namespace, ""));
        properties.put(ACCESS_KEY, Objects.toString(nacosProperties.accessKey, ""));
        properties.put(SECRET_KEY, Objects.toString(nacosProperties.secretKey, ""));
        properties.put(RAM_ROLE_NAME, Objects.toString(nacosProperties.ramRoleName, ""));
        properties.put(CLUSTER_NAME, Objects.toString(nacosProperties.clusterName, ""));
        properties.put(MAX_RETRY, Objects.toString(nacosProperties.maxRetry, ""));
        properties.put(CONFIG_LONG_POLL_TIMEOUT, Objects.toString(nacosProperties.configLongPollTimeout, ""));
        properties.put(CONFIG_RETRY_TIME, Objects.toString(nacosProperties.configRetryTime, ""));
        properties.put(ENABLE_REMOTE_SYNC_CONFIG, Objects.toString(nacosProperties.enableRemoteSyncConfig, ""));
        String endpoint = Objects.toString(nacosProperties.endpoint, "");
        if (endpoint.contains(":")) {
            int index = endpoint.indexOf(":");
            properties.put(ENDPOINT, endpoint.substring(0, index));
            properties.put(ENDPOINT_PORT, endpoint.substring(index + 1));
        } else {
            properties.put(ENDPOINT, endpoint);
        }
        return properties;
    }
}
