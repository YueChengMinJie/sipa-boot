package com.sipa.boot.lb;

import org.springframework.cloud.client.loadbalancer.DefaultRequestContext;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.http.HttpHeaders;

/**
 * @author caszhou
 * @date 2023/8/16
 */
public class CanaryUtil {
    public static String getHeader(Request request, String key) {
        DefaultRequestContext context = (DefaultRequestContext)request.getContext();
        RequestData requestData = (RequestData)context.getClientRequest();
        HttpHeaders headers = requestData.getHeaders();
        return headers.getFirst(key);
    }
}
