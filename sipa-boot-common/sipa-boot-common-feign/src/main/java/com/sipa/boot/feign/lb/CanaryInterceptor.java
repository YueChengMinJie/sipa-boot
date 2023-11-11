package com.sipa.boot.feign.lb;

import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.feign.util.FeignUtil;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author caszhou
 * @date 2023/5/11
 */
public class CanaryInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String canaryHeader = FeignUtil.getHeader(SipaConstant.CANARY_HEADER);
        if (StringUtils.isNotBlank(canaryHeader)) {
            template.header(SipaConstant.CANARY_HEADER, canaryHeader);
        }
    }
}
