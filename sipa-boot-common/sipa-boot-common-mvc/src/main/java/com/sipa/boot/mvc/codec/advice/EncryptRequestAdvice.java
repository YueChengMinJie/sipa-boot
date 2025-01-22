package com.sipa.boot.mvc.codec.advice;

import java.lang.reflect.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import com.sipa.boot.core.service.CacheService;
import com.sipa.boot.mvc.codec.annotation.Decode;
import com.sipa.boot.mvc.codec.annotation.Encrypt;
import com.sipa.boot.mvc.codec.property.ApiProperty;
import com.sipa.boot.mvc.codec.util.ApiUtil;

/**
 * 请求参数到controller之前的处理
 *
 * @author caszhou
 * @date 2020/9/18
 */
@ControllerAdvice(basePackages = {"com"})
public class EncryptRequestAdvice implements RequestBodyAdvice {
    private final ApiProperty apiProperty;

    private final CacheService cacheService;

    /**
     * 是否需要解码
     */
    private boolean isDecode;

    public EncryptRequestAdvice(ApiProperty apiProperty, @Autowired(required = false) CacheService cacheService) {
        this.apiProperty = apiProperty;
        this.cacheService = cacheService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean supports(MethodParameter methodParameter, Type type,
        Class<? extends HttpMessageConverter<?>> aClass) {
        // 方法或类上有注解
        if (ApiUtil.hasMethodAnnotation(methodParameter, new Class[] {Encrypt.class, Decode.class})) {
            isDecode = true;
            // 这里返回true 才支持
            return true;
        }
        return false;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter,
        Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        if (isDecode) {
            return new DecodeInputMessage(httpInputMessage, apiProperty, cacheService);
        }
        return httpInputMessage;
    }

    @Override
    public Object afterBodyRead(Object obj, HttpInputMessage httpInputMessage, MethodParameter methodParameter,
        Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        // 这里就是已经读取到body了，obj就是
        return obj;
    }

    @Override
    public Object handleEmptyBody(Object obj, HttpInputMessage httpInputMessage, MethodParameter methodParameter,
        Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        // body 为空的时候调用
        return obj;
    }
}
