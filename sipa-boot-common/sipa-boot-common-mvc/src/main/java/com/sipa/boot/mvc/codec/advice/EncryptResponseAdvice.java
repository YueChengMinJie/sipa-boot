package com.sipa.boot.mvc.codec.advice;

import java.util.Objects;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.sipa.boot.core.response.ResponseWrapper;
import com.sipa.boot.core.util.SipaJsonUtil;
import com.sipa.boot.mvc.codec.annotation.Encode;
import com.sipa.boot.mvc.codec.annotation.Encrypt;
import com.sipa.boot.mvc.codec.property.ApiProperty;
import com.sipa.boot.mvc.codec.util.ApiUtil;
import com.sipa.boot.mvc.response.NoPackage;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 接口返回对象加密
 *
 * @author caszhou
 * @date 2020/9/18
 */
@Slf4j
@Order
@ControllerAdvice(basePackages = {"com"})
public class EncryptResponseAdvice implements ResponseBodyAdvice<Object> {
    private final ApiProperty apiProperty;

    public EncryptResponseAdvice(ApiProperty apiProperty) {
        this.apiProperty = apiProperty;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        if (methodParameter.getDeclaringClass().isAnnotationPresent(NoPackage.class)) {
            return false;
        }
        return ApiUtil.hasMethodAnnotation(methodParameter, new Class[] {Encrypt.class, Encode.class});
    }

    /**
     * 返回结果加密
     * 
     * @param body
     *            接口返回的对象
     * @param methodParameter
     *            method
     * @param mediaType
     *            mediaType
     * @param aClass
     *            HttpMessageConverter class
     * @param serverHttpRequest
     *            request
     * @param serverHttpResponse
     *            response
     * @return obj
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
        Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest,
        ServerHttpResponse serverHttpResponse) {
        if (body instanceof Resource) {
            return body;
        }
        if (body instanceof String) {
            ResponseWrapper<Object> wrapper = JSONUtil.toBean((String)body, new TypeReference<>() {}, true);
            injectKey(wrapper);
            if (Objects.nonNull(wrapper.getData())) {
                return JSONUtil.toJsonStr(wrapper);
            } else {
                return body;
            }
        }
        if (body instanceof ResponseWrapper) {
            try {
                ResponseWrapper<Object> wrapper = (ResponseWrapper<Object>)body;
                injectKey(wrapper);
                return wrapper;
            } catch (Exception e) {
                log.error("加密失败", e);
            }
        }
        return body;
    }

    private void injectKey(ResponseWrapper<Object> wrapper) {
        // 1、随机aes密钥
        byte[] randomAesKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), 128).getEncoded();
        // 2、数据体
        Object data = wrapper.getData();
        if (Objects.nonNull(data)) {
            // 3、转json字符串
            String jsonString = SipaJsonUtil.writeValueAsString(data);
            // 4、aes加密数据体
            String encryptString = SecureUtil.aes(randomAesKey).encryptBase64(jsonString);
            // 5、重新设置数据体
            wrapper.setData(encryptString);
            // 6、使用前端的rsa公钥加密 aes密钥 返回给前端
            RSA rsa = new RSA(null, ApiUtil.cleanKey(apiProperty.getFrontRsaPublicKey()));
            wrapper.setKey(rsa.encryptBase64(HexUtil.encodeHexStr(randomAesKey), KeyType.PublicKey));
        }
    }
}
