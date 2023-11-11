package com.sipa.boot.nacos.shared;

import static com.alibaba.nacos.api.PropertyKeyConst.*;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.client.naming.utils.UtilAndComs;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author caszhou
 * @date 2023/4/4
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SipaNacosDiscoveryProperties extends NacosDiscoveryProperties {
    private List<Namespace> sharedNamespaces;

    /**
     * 可以指定group，查询的时候
     */
    @Data
    public static class Namespace {
        private String namespaceId;

        private String group = "DEFAULT_GROUP";
    }

    public Properties getNacosProperties(Namespace namespace) {
        Properties properties = new Properties();
        properties.put(SERVER_ADDR, this.getServerAddr());
        properties.put(USERNAME, Objects.toString(this.getUsername(), ""));
        properties.put(PASSWORD, Objects.toString(this.getPassword(), ""));
        properties.put(NAMESPACE, namespace.getNamespaceId());
        properties.put(UtilAndComs.NACOS_NAMING_LOG_NAME, this.getLogName());

        String endpoint = this.getEndpoint();
        if (endpoint.contains(":")) {
            int index = endpoint.indexOf(":");
            properties.put(ENDPOINT, endpoint.substring(0, index));
            properties.put(ENDPOINT_PORT, endpoint.substring(index + 1));
        } else {
            properties.put(ENDPOINT, endpoint);
        }
        properties.put(ACCESS_KEY, this.getAccessKey());
        properties.put(SECRET_KEY, this.getSecretKey());
        properties.put(CLUSTER_NAME, this.getClusterName());
        properties.put(NAMING_LOAD_CACHE_AT_START, this.getNamingLoadCacheAtStart());
        return properties;
    }
}
