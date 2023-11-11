package com.sipa.boot.core.apm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sipa.boot.core.constant.SipaConstant;

/**
 * @author caszhou
 * @date 2023/5/22
 */
public class ApmHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        MDC.put(SipaConstant.REQUEST_ID_HEADER, request.getHeader(SipaConstant.REQUEST_ID_HEADER));
        MDC.put(SipaConstant.REQUEST_FROM_HEADER, request.getHeader(SipaConstant.REQUEST_FROM_HEADER));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) {
        // 清除MDC中的请求头值
        MDC.remove(SipaConstant.REQUEST_ID_HEADER);
        MDC.remove(SipaConstant.REQUEST_FROM_HEADER);
    }
}
