package com.sipa.boot.cache;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.google.common.base.Charsets;
import com.sipa.boot.cache.property.CacheProperty;
import com.sipa.boot.core.constant.TcpCloudConstant;
import com.sipa.boot.core.property.YamlPropertySourceFactory;

import lombok.AllArgsConstructor;

/**
 * @author caszhou
 * @date 2019-01-22
 */
@EnableCaching
@Configuration
@AllArgsConstructor
@ConditionalOnClass(CacheProperty.class)
@EnableConfigurationProperties(CacheProperty.class)
@PropertySource(value = "classpath:cache.yml", factory = YamlPropertySourceFactory.class)
@ConditionalOnProperty(prefix = TcpCloudConstant.Cache.PREFIX, value = TcpCloudConstant.Cache.ENABLED_KEY,
    havingValue = TcpCloudConstant.Cache.ENABLED_VALUE)
public class CacheAutoConfiguration {
    private final CacheProperty cacheProperty;

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder.cacheDefaults(this.cacheConfiguration());
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheName -> "sipa_cache_" + cacheName + ":")
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer(Charsets.UTF_8)))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new JdkSerializationRedisSerializer(CacheAutoConfiguration.class.getClassLoader())));
        if (this.cacheProperty.getTimeToLive() != null) {
            config = config.entryTtl(this.cacheProperty.getTimeToLive());
        }
        if (!this.cacheProperty.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        return config;
    }
}
