package com.sipa.boot.secure.server;

import org.apache.commons.lang3.StringUtils;

import cn.dev33.satoken.same.SaSameUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author caszhou
 * @date 2023/5/11
 */
public class SameTokenInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String sameToken = SaSameUtil.getToken();
        if (StringUtils.isNotBlank(sameToken)) {
            template.header(SaSameUtil.SAME_TOKEN, sameToken);
        }
    }
}
