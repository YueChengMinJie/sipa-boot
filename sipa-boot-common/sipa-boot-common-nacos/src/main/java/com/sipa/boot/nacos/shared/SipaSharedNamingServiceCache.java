package com.sipa.boot.nacos.shared;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;

/**
 * @author caszhou
 * @date 2023/4/4
 */
public class SipaSharedNamingServiceCache {
    private static final Map<String, NamingService> CACHE = new ConcurrentHashMap<>();

    public synchronized static NamingService getNamingService(SipaNacosDiscoveryProperties sipaNacosDiscoveryProperties,
        SipaNacosDiscoveryProperties.Namespace sharedNamespace) throws NacosException {
        String namespaceId = sharedNamespace.getNamespaceId();
        NamingService namingService = CACHE.get(namespaceId);
        if (Objects.isNull(namingService)) {
            namingService =
                NamingFactory.createNamingService(sipaNacosDiscoveryProperties.getNacosProperties(sharedNamespace));
            CACHE.put(namespaceId, namingService);
        }
        return namingService;
    }
}
