package com.sipa.boot.feign.apm;

import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.util.SipaHttpUtil;

import cn.hutool.extra.spring.SpringUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author caszhou
 * @date 2023/5/23
 */
public class ApmRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String requestId = SipaHttpUtil.getHeader(SipaConstant.REQUEST_ID_HEADER);
        if (StringUtils.isNotBlank(requestId)) {
            template.header(SipaConstant.REQUEST_ID_HEADER, requestId);
            template.header(SipaConstant.REQUEST_FROM_HEADER, SpringUtil.getApplicationName());
        }
    }
}
