package com.sipa.boot.sentinel.server;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import com.sipa.boot.core.constant.SipaConstant;

/**
 * @author caszhou
 * @date 2024/12/31
 */
public class SipaRequestOriginParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest request) {
        return request.getHeader(SipaConstant.REQUEST_FROM_HEADER);
    }
}
