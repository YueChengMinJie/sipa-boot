package com.sipa.boot.feign.header;

import org.springframework.http.HttpHeaders;

import com.sipa.boot.core.constant.SipaConstant;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author caszhou
 * @date 2023/5/23
 */
public class HeaderRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        if (!template.headers().containsKey(HttpHeaders.CONTENT_TYPE)) {
            template.header(HttpHeaders.CONTENT_TYPE, SipaConstant.HttpHeaderValue.JSON);
        }
        if (!template.headers().containsKey(HttpHeaders.ACCEPT)) {
            template.header(HttpHeaders.ACCEPT, SipaConstant.HttpHeaderValue.JSON);
        }
    }
}
