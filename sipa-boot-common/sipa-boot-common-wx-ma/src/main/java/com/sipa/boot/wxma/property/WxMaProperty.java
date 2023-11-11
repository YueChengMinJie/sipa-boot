package com.sipa.boot.wxma.property;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 多个微信配置信息
 *
 * @author caszhou
 * @since 2021-07-23
 */

@Getter
@Setter
@ConfigurationProperties(prefix = "sipa-boot.wx.ma")
public class WxMaProperty {
    /**
     * 多个小程序配置信息
     */
    private List<MaConfig> configs;

    @Getter
    @Setter
    public static class MaConfig {
        /**
         * 应用id
         */
        private Long applicationId;

        /**
         * 微信小程序的app id
         */
        private String appid;

        /**
         * 微信小程序的app secret
         */
        private String secret;
    }
}
