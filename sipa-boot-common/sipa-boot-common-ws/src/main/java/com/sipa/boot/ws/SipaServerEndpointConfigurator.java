package com.sipa.boot.ws;

import java.util.List;
import java.util.Map;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.apache.commons.collections4.CollectionUtils;

import com.sipa.boot.core.app.AppConstant;
import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.response.ResponseWrapper;
import com.sipa.boot.rest.util.LoadBalanceRestUtil;

import cn.dev33.satoken.same.SaSameUtil;

/**
 * @author caszhou
 * @date 2024/9/11
 */
public class SipaServerEndpointConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    @SuppressWarnings("unchecked")
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        List<String> tokens = request.getHeaders().get("Sec-WebSocket-Protocol");
        if (CollectionUtils.isNotEmpty(tokens)) {
            String token = tokens.get(0);
            ResponseWrapper<?> idpUser = checkToken(token);
            if (idpUser.getData() instanceof Map) {
                // success
                sec.getUserProperties().put(WsConstants.IDP_USER_KEY, idpUser.getData());
                response.getHeaders().put("Sec-WebSocket-Protocol", List.of(token));
                return;
            }
        }
        throw new RuntimeException("Forbidden.");
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
}
