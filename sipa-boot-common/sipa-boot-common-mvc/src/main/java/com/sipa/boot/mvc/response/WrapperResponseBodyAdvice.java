package com.sipa.boot.mvc.response;

import java.util.Objects;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.sipa.boot.core.response.ResponseWrapper;

import cn.hutool.json.JSONUtil;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@RestControllerAdvice(basePackages = {"com.sipa", "com.hm", "com.hmev"}) // todo by caszhou 业务配置应该与中台框架解耦
public class WrapperResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> clazz) {
        if (methodParameter.getDeclaringClass().isAnnotationPresent(NoPackage.class)) {
            return false;
        }
        return Objects.isNull(methodParameter.getMethod())
            || !methodParameter.getMethod().isAnnotationPresent(NoPackage.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
        Class<? extends HttpMessageConverter<?>> clazz, ServerHttpRequest serverHttpRequest,
        ServerHttpResponse serverHttpResponse) {
        if (body instanceof Resource || body instanceof ResponseWrapper<?>) {
            return body;
        }
        if (body instanceof String) {
            return JSONUtil.toJsonStr(ResponseWrapper.success(body));
        }
        if (Objects.nonNull(methodParameter.getMethod())
            && String.class.equals(methodParameter.getMethod().getReturnType()) && Objects.isNull(body)) {
            return JSONUtil.toJsonStr(ResponseWrapper.success(null));
        }
        return ResponseWrapper.success(body);
    }
}
