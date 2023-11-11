package com.sipa.boot.wxmp.property;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author caszhou
 * @since 2021-07-23
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "sipa-boot.wx.mp")
public class WxMpProperty {
    /**
     * 多个公众号配置信息
     */
    private List<MpConfig> configs;

    @Getter
    @Setter
    public static class MpConfig {
        /**
         * 应用id
         */
        private Long applicationId;

        /**
         * 微信公众号的app id
         */
        private String appId;

        /**
         * 微信公众号的app secret
         */
        private String secret;
    }
}
