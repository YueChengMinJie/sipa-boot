package com.sipa.boot.rocketmq.interceptor;

import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.sipa.boot.core.secure.IdpUser;
import com.sipa.boot.core.secure.IdpUserHolder;
import com.sipa.boot.core.util.SipaJsonUtil;

/**
 * @author caszhou
 * @date 2024/7/18
 */
@Component
@GlobalChannelInterceptor
public class IdpUserChannelInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        MessageHeaders headers = message.getHeaders();
        Object o = headers.get(IdpUser.REST_WEB_KEY);
        if (o instanceof String) {
            String json = (String)o;
            IdpUser idpUser = SipaJsonUtil.convertValue(json, IdpUser.class);
            IdpUserHolder.set(idpUser);
        }
        return ChannelInterceptor.super.preSend(message, channel);
    }
}
