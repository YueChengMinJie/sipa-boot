package com.sipa.boot.wxmp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sipa.boot.wxmp.property.WxMpProperty;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;

/**
 * @author caszhou
 * @date 2023/5/6
 */
@Slf4j
public class WxMpManager {
    static final Map<Long, WxMpService> WX_MP_SERVICE_MAP = new ConcurrentHashMap<>();

    static final Map<Long, WxMpProperty.MpConfig> WX_MP_PROPERTY_MAP = new ConcurrentHashMap<>();

    /**
     * 获取微信bean实例
     */
    public static WxMpService getMpService(Long applicationId) {
        WxMpService wxService = WX_MP_SERVICE_MAP.get(applicationId);
        if (wxService == null) {
            log.error("根据applicationId：{} 获取wx service报错", applicationId);
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", applicationId));
        }
        return wxService;
    }

    /**
     * 获取微信配置信息
     */
    public static WxMpProperty.MpConfig getWpConfig(Long applicationId) {
        WxMpProperty.MpConfig config = WX_MP_PROPERTY_MAP.get(applicationId);
        if (config == null) {
            log.error("根据applicationId：{} 获取获取微信配置信息", applicationId);
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", applicationId));
        }
        return config;
    }
}
