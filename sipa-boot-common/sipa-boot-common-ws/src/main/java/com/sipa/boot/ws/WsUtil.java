package com.sipa.boot.ws;

import java.util.Map;

import javax.websocket.Session;

import com.sipa.boot.core.secure.IdpUser;

import cn.hutool.core.bean.BeanUtil;

/**
 * @author caszhou
 * @date 2024/9/11
 */
public class WsUtil {
    public static IdpUser getIdpUser(Session session) {
        Object o = session.getUserProperties().get(WsConstants.IDP_USER_KEY);
        if (o instanceof Map) {
            return BeanUtil.fillBeanWithMap((Map)o, new IdpUser(), false);
        }
        return new IdpUser();
    }
}
