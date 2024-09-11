package com.sipa.boot.ws;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.sipa.boot.core.app.AppConstant;
import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.response.ResponseWrapper;
import com.sipa.boot.rest.util.LoadBalanceRestUtil;

import cn.dev33.satoken.same.SaSameUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2024/9/11
 */
@Slf4j
@Component
public class PortalHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    @SuppressWarnings("unchecked")
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
        Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest)request;
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            String token = httpServletRequest.getHeader("Sec-WebSocket-Protocol");
            ResponseWrapper<?> idpUser = checkToken(token);
            if (idpUser.getData() instanceof Map) {
                // success
                attributes.putAll((Map<String, Object>)idpUser.getData());
            } else {
                // fail
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return false;
            }
        }
        return true;
    }

    private ResponseWrapper<?> checkToken(String token) {
        return LoadBalanceRestUtil.get(
            // url
            "http://" + AppConstant.BCP.SSO_IDP_NAME + "/check",
            // header
            Map.of(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken(), SipaConstant.AUTH_KEY,
                SipaConstant.TOKEN_PREFIX + " " + token),
            // payload
            null,
            // rest
            ResponseWrapper.class);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
        Exception exception) {
        HttpServletRequest httpRequest = ((ServletServerHttpRequest)request).getServletRequest();
        HttpServletResponse httpResponse = ((ServletServerHttpResponse)response).getServletResponse();
        if (StringUtils.isNotEmpty(httpRequest.getHeader("Sec-WebSocket-Protocol"))) {
            httpResponse.addHeader("Sec-WebSocket-Protocol", httpRequest.getHeader("Sec-WebSocket-Protocol"));
        }
    }
}
