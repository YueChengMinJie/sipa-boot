package com.sipa.boot.gateway.filter;

import static com.sipa.boot.core.constant.SipaConstant.FILTER_HEADER_KEYS;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2023/3/11
 */
@Slf4j
@Component
public class RequestLogFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpRequest request = exchange.getRequest();
            // 打印请求路径
            String path = request.getPath().pathWithinApplication().value();
            // 打印请求url
            String requestUrl = this.getOriginalRequestUrl(exchange);

            // **构建成一条长 日志，避免并发下日志错乱**
            StringBuilder reqLog = new StringBuilder(200);
            // 日志参数
            List<Object> reqArgs = new ArrayList<>();
            reqLog.append("\n\n================ Gateway Request Start  ================\n");
            // 打印路由添加占位符
            reqLog.append("===> {}: {}\n");
            // 参数
            String requestMethod = request.getMethodValue();
            reqArgs.add(requestMethod);
            reqArgs.add(requestUrl);

            // 打印请求头
            HttpHeaders headers = request.getHeaders();
            headers.forEach((headerName, headerValue) -> {
                if (!StringUtils.containsAnyIgnoreCase(headerName, FILTER_HEADER_KEYS)) {
                    reqLog.append("===Headers===  {}: {}\n");
                    reqArgs.add(headerName);
                    reqArgs.add(StringUtils.join(headerValue.toArray(), ", "));
                }
            });

            reqLog.append("================  Gateway Request End  =================\n");
            // 打印执行时间
            log.info(reqLog.toString(), reqArgs.toArray());
        } catch (Exception ignored) {
        }
        return chain.filter(exchange);
    }

    private String getOriginalRequestUrl(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        LinkedHashSet<URI> uris =
            exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        URI requestUri = uris.stream().findFirst().orElse(request.getURI());
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        return UriComponentsBuilder.fromPath(requestUri.getRawPath()).queryParams(queryParams).build().toUriString();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
