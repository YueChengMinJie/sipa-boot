package com.sipa.boot.wxma;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sipa.boot.wxma.property.WxMaProperty;

import cn.binarywang.wx.miniapp.api.WxMaService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/5/6
 */
@Slf4j
public class WxMaManager {
    static final Map<Long, WxMaService> WX_MA_SERVICE_MAP = new ConcurrentHashMap<>();

    static final Map<Long, WxMaProperty.MaConfig> WX_MA_PROPERTY_MAP = new ConcurrentHashMap<>();

    /**
     * 获取微信bean实例
     */
    public static WxMaService getMaService(Long applicationId) {
        WxMaService wxService = WX_MA_SERVICE_MAP.get(applicationId);
        if (wxService == null) {
            log.error("根据applicationId：{} 获取wx service报错", applicationId);
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", applicationId));
        }
        return wxService;
    }

    /**
     * 获取微信配置信息
     */
    public static WxMaProperty.MaConfig getWxConfig(Long applicationId) {
        WxMaProperty.MaConfig config = WX_MA_PROPERTY_MAP.get(applicationId);
        if (config == null) {
            log.error("根据applicationId：{} 获取获取微信配置信息", applicationId);
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", applicationId));
        }
        return config;
    }
}
