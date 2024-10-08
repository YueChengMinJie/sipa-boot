package com.sipa.boot.gateway.exception;

import java.util.Map;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.response.ResponseWrapper;

import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2023/5/24
 */
@Slf4j
public class GatewayErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {
    public GatewayErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources,
        ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resources, errorProperties, applicationContext);
    }

    @Override
    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Map<String, Object> error =
            this.getErrorAttributes(request, this.getErrorAttributeOptions(request, MediaType.ALL));
        int status = this.getHttpStatus(error);
        return getServerResponseMono(status, error);
    }

    private static Mono<ServerResponse> getServerResponseMono(int status, Map<String, Object> error) {
        if (status == HttpStatus.HTTP_NOT_FOUND) {
            ESystemErrorCode code = ESystemErrorCode.NOT_FOUND;
            return doGetServerResponseMono(status, code, error);
        } else if (status == HttpStatus.HTTP_INTERNAL_ERROR) {
            ESystemErrorCode code = ESystemErrorCode.GATEWAY_ERROR;
            return doGetServerResponseMono(status, code, error);
        } else if (status == HttpStatus.HTTP_BAD_GATEWAY) {
            ESystemErrorCode code = ESystemErrorCode.BAD_GATEWAY;
            return doGetServerResponseMono(status, code, error);
        } else if (status == HttpStatus.HTTP_UNAVAILABLE) {
            ESystemErrorCode code = ESystemErrorCode.UNAVAILABLE;
            return doGetServerResponseMono(status, code, error);
        } else if (status == HttpStatus.HTTP_GATEWAY_TIMEOUT) {
            ESystemErrorCode code = ESystemErrorCode.GATEWAY_TIMEOUT;
            return doGetServerResponseMono(status, code, error);
        }
        return ServerResponse.status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(error));
    }

    private static Mono<ServerResponse> doGetServerResponseMono(int status, ESystemErrorCode code,
        Map<String, Object> error) {
        log.info(JSONUtil.toJsonStr(error));
        return ServerResponse.status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(ResponseWrapper.error(code.getAllCode(), code.getMsg())));
    }
}
