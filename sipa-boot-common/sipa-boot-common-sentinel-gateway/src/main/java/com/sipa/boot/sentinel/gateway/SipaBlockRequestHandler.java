package com.sipa.boot.sentinel.gateway;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.response.ResponseWrapper;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2024/12/31
 */
public class SipaBlockRequestHandler implements BlockRequestHandler {
    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(fromObject(buildErrorResult()));
    }

    private ResponseWrapper<?> buildErrorResult() {
        ESystemErrorCode errorCode = ESystemErrorCode.TOO_MANY_REQUEST;
        return ResponseWrapper.error(errorCode.getAllCode(), errorCode.getMsg());
    }
}
