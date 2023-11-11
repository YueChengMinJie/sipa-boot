package com.sipa.boot.nacos.shared;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.alibaba.nacos.shaded.com.google.common.collect.Sets;

/**
 * @author caszhou
 * @date 2023/4/4
 */
public class SipaNacosServiceDiscovery extends NacosServiceDiscovery {
    private final SipaNacosDiscoveryProperties sipaNacosDiscoveryProperties;

    private final NacosServiceManager nacosServiceManager;

    private final NacosDiscoveryProperties discoveryProperties;

    public SipaNacosServiceDiscovery(SipaNacosDiscoveryProperties discoveryProperties,
        NacosServiceManager nacosServiceManager) {
        super(discoveryProperties, nacosServiceManager);
        this.sipaNacosDiscoveryProperties = discoveryProperties;
        this.nacosServiceManager = nacosServiceManager;
        this.discoveryProperties = discoveryProperties;
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) throws NacosException {
        // 多个命名空间
        List<Instance> all = Lists.newArrayList();
        // 当前的group
        String group = this.sipaNacosDiscoveryProperties.getGroup();
        // 当前namespace
        String namespace = this.sipaNacosDiscoveryProperties.getNamespace();
        // 默认的naming service
        NamingService namingService = this.nacosServiceManager.getNamingService();
        // 当前group的instance 列表
        List<Instance> instances = namingService.selectInstances(serviceId, group, true);
        // 当前namespace和group没有查询到
        if (CollectionUtils.isEmpty(instances)) {
            // 查询共享空间
            List<SipaNacosDiscoveryProperties.Namespace> sharedNamespaces =
                this.sipaNacosDiscoveryProperties.getSharedNamespaces();
            // 如果有
            if (CollectionUtils.isNotEmpty(sharedNamespaces)) {
                for (SipaNacosDiscoveryProperties.Namespace sharedNamespace : sharedNamespaces) {
                    // 过滤一样的
                    if (!StringUtils.equals(namespace, sharedNamespace.getNamespaceId())) {
                        // 共享空间的分组
                        String sharedNamespaceGroup = sharedNamespace.getGroup();
                        List<Instance> sharedInstances = SipaSharedNamingServiceCache
                            .getNamingService(this.sipaNacosDiscoveryProperties, sharedNamespace)
                            .selectInstances(serviceId, sharedNamespaceGroup, true);
                        if (CollectionUtils.isNotEmpty(sharedInstances)) {
                            all.addAll(sharedInstances);
                        }
                    }
                }
            }
        } else {
            all.addAll(instances);
        }
        return hostToServiceInstanceList(all, serviceId);
    }

    @Override
    public List<String> getServices() throws NacosException {
        List<String> currentGroupServices = super.getServices();
        Set<String> sharedServices = this.getSharedServices();
        sharedServices.addAll(currentGroupServices);
        return Lists.newArrayList(sharedServices);
    }

    public Set<String> getSharedServices() throws NacosException {
        Set<String> sharedServices = Sets.newHashSet();
        List<SipaNacosDiscoveryProperties.Namespace> sharedNamespaces =
            this.sipaNacosDiscoveryProperties.getSharedNamespaces();
        if (CollectionUtils.isNotEmpty(sharedNamespaces)) {
            for (SipaNacosDiscoveryProperties.Namespace sharedNamespace : sharedNamespaces) {
                String namespace = this.sipaNacosDiscoveryProperties.getNamespace();
                if (!StringUtils.equals(namespace, sharedNamespace.getNamespaceId())) {
                    String group = this.discoveryProperties.getGroup();
                    ListView<String> services = SipaSharedNamingServiceCache
                        .getNamingService(this.sipaNacosDiscoveryProperties, sharedNamespace)
                        .getServicesOfServer(1, Integer.MAX_VALUE, group);
                    List<String> data = services.getData();
                    if (CollectionUtils.isNotEmpty(data)) {
                        sharedServices.addAll(data);
                    }
                }
            }
        }
        return sharedServices;
    }
}
