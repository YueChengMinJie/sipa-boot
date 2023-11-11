package com.sipa.boot.secure.server;

import java.util.Objects;

import com.sipa.boot.core.secure.IdpUser;
import com.sipa.boot.core.secure.IdpUserUtil;
import com.sipa.boot.secure.SecureHelper;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author caszhou
 * @date 2023/5/11
 */
public class IdpUserInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        IdpUser idpUser = IdpUserUtil.get();
        if (Objects.nonNull(idpUser)) {
            template.header(IdpUser.REST_WEB_KEY, SecureHelper.convert(idpUser));
        }
    }
}
