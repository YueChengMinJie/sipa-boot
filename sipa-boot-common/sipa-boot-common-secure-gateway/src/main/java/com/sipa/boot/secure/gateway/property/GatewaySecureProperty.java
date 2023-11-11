package com.sipa.boot.secure.gateway.property;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import com.google.common.collect.Maps;
import com.sipa.boot.core.constant.SipaBootConstant;

import lombok.Data;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = SipaBootConstant.Gateway.SECURE)
public class GatewaySecureProperty {
    /**
     * 登陆认证，针对前中后台，暂时没有用到
     */
    private Map<String, Long> loginAuthentication = Maps.newHashMap();

    /**
     * 外部用户认证,只允许外部用户类型访问符合这些规则的接口
     */
    private String[] externalAccountAllowedPathPattern = {};

    /**
     * 角色认证，只针对中台
     */
    private Map<String, List<String>> roleAuthentication = Maps.newHashMap();

    /**
     * 权限认证，针对前中后台，暂时没有用到
     */
    private Map<String, Long> permissionAuthentication = Maps.newHashMap();
}
