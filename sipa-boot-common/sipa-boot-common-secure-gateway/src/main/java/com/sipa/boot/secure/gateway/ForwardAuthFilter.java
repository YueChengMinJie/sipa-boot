package com.sipa.boot.secure.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import cn.dev33.satoken.same.SaSameUtil;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2023/5/11
 */
public class ForwardAuthFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest.Builder builder =
            exchange.getRequest().mutate().header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
        ServerHttpRequest newRequest = builder.build();
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        return chain.filter(newExchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
