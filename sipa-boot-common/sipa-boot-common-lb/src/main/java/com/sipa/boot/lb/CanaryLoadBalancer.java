package com.sipa.boot.lb;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.constant.TcpCloudConstant;
import com.sipa.boot.core.env.EnvConstant;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/8/10
 */
@Slf4j
public class CanaryLoadBalancer implements ReactorServiceInstanceLoadBalancer {
    private final AtomicInteger position;

    private final String serviceId;

    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    public CanaryLoadBalancer(int position, String serviceId,
        ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider) {
        this.position = new AtomicInteger(position);
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }

    public CanaryLoadBalancer(String serviceId,
        ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider) {
        this(new Random().nextInt(1000), serviceId, serviceInstanceListSupplierProvider);
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier =
            this.serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request)
            .next()
            .map(serviceInstances -> this.processInstanceResponse(supplier, serviceInstances, request));
    }

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier,
        List<ServiceInstance> serviceInstances, Request request) {
        Response<ServiceInstance> serviceInstanceResponse = this.getInstanceResponse(serviceInstances, request);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback)supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, Request request) {
        if (instances.isEmpty()) {
            return this.getEmptyResponse();
        }

        String canaryHeader = CanaryUtil.getHeader(request, SipaConstant.CANARY_HEADER);
        List<ServiceInstance> filterInstances;
        if (StringUtils.isBlank(canaryHeader)) {
            filterInstances = instances.stream()
                .filter(serviceInstance -> StringUtils.isBlank(serviceInstance.getMetadata()
                    .get(TcpCloudConstant.Core.SIPA_NACOS_PREFIX + EnvConstant.CANARY_NAME)))
                .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(filterInstances)) {
                return this.getEmptyResponse();
            } else if (filterInstances.size() == 1) {
                return new DefaultResponse(filterInstances.get(0));
            } else {
                return this.getNormalResponse(filterInstances);
            }
        } else if (instances.size() == 1) {
            return new DefaultResponse(instances.get(0));
        } else {
            filterInstances = instances.stream()
                .filter(serviceInstance -> StringUtils.isNotBlank(serviceInstance.getMetadata()
                    .get(TcpCloudConstant.Core.SIPA_NACOS_PREFIX + EnvConstant.CANARY_NAME)))
                .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(filterInstances)) {
                return this.getNormalResponse(instances);
            } else if (filterInstances.size() == 1) {
                return new DefaultResponse(filterInstances.get(0));
            } else {
                return this.getNormalResponse(filterInstances);
            }
        }
    }

    private DefaultResponse getNormalResponse(List<ServiceInstance> instances) {
        int pos = this.position.incrementAndGet() & Integer.MAX_VALUE;
        ServiceInstance instance = instances.get(pos % instances.size());
        return new DefaultResponse(instance);
    }

    private EmptyResponse getEmptyResponse() {
        if (log.isWarnEnabled()) {
            log.warn("No servers available for service: " + this.serviceId);
        }
        return new EmptyResponse();
    }
}
