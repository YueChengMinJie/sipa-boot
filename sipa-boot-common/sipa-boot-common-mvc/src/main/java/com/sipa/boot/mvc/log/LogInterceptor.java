package com.sipa.boot.mvc.log;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.tool.log.RestLogManager;

import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/5/22
 */
@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    private static final String START_TIME_ATTRIBUTE_KEY = "request_start_time";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTRIBUTE_KEY, System.currentTimeMillis());
        this.printStartLog(request);
        return true;
    }

    private void printStartLog(HttpServletRequest request) {
        try {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String url = request.getRequestURL().toString();
            String header = this.getHeader(request);
            RestLogManager.logStart(log, method, uri, url, header);
        } catch (Exception e) {
            RestLogManager.logStartFail(log, request.getRequestURI(), e);
        }
    }

    private String getHeader(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            if (!StringUtils.containsAnyIgnoreCase(key, SipaConstant.FILTER_HEADER_KEYS)) {
                String value = request.getHeader(key);
                sb.append(key).append(SipaConstant.Symbol.EQUAL).append(value).append(SipaConstant.Symbol.EMPTY);
            }
        }
        return sb.toString().trim();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) {
        this.printFinishLog(request);
    }

    private void printFinishLog(HttpServletRequest request) {
        try {
            // 计算接口耗时
            long startTime = (long)request.getAttribute(START_TIME_ATTRIBUTE_KEY);
            long now = System.currentTimeMillis();
            double duration = (now - startTime) / 1000.0;

            // 打印结束日志
            String host = request.getHeader(SipaConstant.HOST_HEADER);
            String clientIp = ServletUtil.getClientIP(request, SipaConstant.SLB_CLIENT_IP_HEADER);
            String referer =
                StringUtils.defaultString(request.getHeader(SipaConstant.REFERER_HEADER), StringUtils.EMPTY);
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String url = request.getRequestURL().toString();
            RestLogManager.logFinish(log, duration, method, uri, url);
            RestLogManager.logDigest(log, duration, host, clientIp, referer, method, uri, url);
        } catch (Exception e) {
            RestLogManager.logFinishFail(log, request.getRequestURI(), e);
        }
    }
}
