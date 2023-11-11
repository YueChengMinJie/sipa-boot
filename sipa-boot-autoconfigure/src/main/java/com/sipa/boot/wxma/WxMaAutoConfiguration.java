package com.sipa.boot.wxma;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.sipa.boot.wxma.property.WxMaProperty;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaRedisBetterConfigImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Configuration
@AllArgsConstructor
@ConditionalOnClass(WxMaProperty.class)
@ComponentScan("com.sipa.boot.wxma.**")
@EnableConfigurationProperties(WxMaProperty.class)
public class WxMaAutoConfiguration {
    private final StringRedisTemplate redisTemplate;

    private final WxMaProperty wxMaProperty;

    @PostConstruct
    public void init() {
        List<WxMaProperty.MaConfig> configs = this.wxMaProperty.getConfigs();
        if (CollectionUtils.isEmpty(configs)) {
            log.error("微信小程序配置信息未配置正确");
        }
        if (CollectionUtils.isNotEmpty(configs)) {
            for (WxMaProperty.MaConfig config : configs) {
                // 设置实例bean
                WxRedisOps wxRedisOps = new RedisTemplateWxRedisOps(this.redisTemplate);
                WxMaRedisBetterConfigImpl wxMaRedisBetterConfig =
                    new WxMaRedisBetterConfigImpl(wxRedisOps, "sipa-boot:ma_" + config.getApplicationId());
                wxMaRedisBetterConfig.setAppid(config.getAppid());
                wxMaRedisBetterConfig.setSecret(config.getSecret());

                WxMaService service = new WxMaServiceImpl();
                service.setWxMaConfig(wxMaRedisBetterConfig);

                WxMaManager.WX_MA_SERVICE_MAP.put(config.getApplicationId(), service);
                WxMaManager.WX_MA_PROPERTY_MAP.put(config.getApplicationId(), config);
            }
        }
    }
}
