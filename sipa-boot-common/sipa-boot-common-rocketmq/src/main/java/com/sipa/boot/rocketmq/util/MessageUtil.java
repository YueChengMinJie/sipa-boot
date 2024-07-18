package com.sipa.boot.rocketmq.util;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import com.sipa.boot.core.secure.IdpUser;
import com.sipa.boot.core.secure.IdpUserUtil;
import com.sipa.boot.core.util.SipaJsonUtil;

/**
 * @author caszhou
 * @date 2024/7/18
 */
public class MessageUtil {
    public static <T> Message<T> build(T t) {
        return MessageBuilder.withPayload(t)
            .setHeader(IdpUser.REST_WEB_KEY, SipaJsonUtil.writeValueAsString(IdpUserUtil.get()))
            .build();
    }
}
