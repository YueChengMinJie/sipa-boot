package com.sipa.boot.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.sipa.boot.cache.util.RedisUtil;

/**
 * @author caszhou
 * @date 2019-01-22
 */
@Configuration
@ConditionalOnClass(RedisUtil.class)
@ComponentScan(value = {"com.sipa.boot.cache.**"})
public class RedisAutoConfiguration {
    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }
}
