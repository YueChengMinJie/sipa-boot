package com.sipa.boot.wxmp;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.sipa.boot.wxmp.property.WxMpProperty;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.mp.config.impl.WxMpRedisConfigImpl;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Configuration
@AllArgsConstructor
@ConditionalOnClass(WxMpProperty.class)
@ComponentScan("com.sipa.boot.cloud.wxmp.**")
@EnableConfigurationProperties(WxMpProperty.class)
public class WxMpAutoConfiguration {
    private final StringRedisTemplate redisTemplate;

    private final WxMpProperty wxMpProperty;

    @PostConstruct
    public void init() {
        List<WxMpProperty.MpConfig> configs = this.wxMpProperty.getConfigs();
        if (CollectionUtils.isEmpty(configs)) {
            log.error("微信公众号配置信息未配置正确");
        }
        if (CollectionUtils.isNotEmpty(configs)) {
            for (WxMpProperty.MpConfig config : configs) {
                WxMpDefaultConfigImpl configStorage = new WxMpRedisConfigImpl(
                    new RedisTemplateWxRedisOps(this.redisTemplate), "sipa-boot:mp_" + config.getApplicationId());
                configStorage.setAppId(config.getAppId());
                configStorage.setSecret(config.getSecret());

                WxMpService service = new WxMpServiceImpl();
                service.setWxMpConfigStorage(configStorage);

                WxMpManager.WX_MP_SERVICE_MAP.put(config.getApplicationId(), service);
                WxMpManager.WX_MP_PROPERTY_MAP.put(config.getApplicationId(), config);
            }
        }
    }
}
