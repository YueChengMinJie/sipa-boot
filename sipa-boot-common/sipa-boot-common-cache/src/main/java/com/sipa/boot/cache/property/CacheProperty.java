package com.sipa.boot.cache.property;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.sipa.boot.core.constant.SipaBootConstant;

import lombok.Data;

/**
 * @author caszhou
 * @date 2019-01-22
 */
@Data
@ConfigurationProperties(prefix = SipaBootConstant.Cache.PREFIX)
public class CacheProperty {
    /**
     * 缓存过期时间。默认缓存永30天过期。
     */
    private Duration timeToLive = Duration.ofDays(30);

    /**
     * 允许缓存空值，默认为false.
     */
    private boolean cacheNullValues;
}
