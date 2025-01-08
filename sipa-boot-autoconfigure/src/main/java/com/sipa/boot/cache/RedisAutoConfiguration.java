package com.sipa.boot.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.sipa.boot.cache.util.RedisUtil;

/**
 * @author caszhou
 * @date 2019-01-22
 */
@Configuration
@ConditionalOnClass(RedisUtil.class)
@ComponentScan("com.sipa.boot.cache.**")
public class RedisAutoConfiguration {
    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    @Primary
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        if (redisConnectionFactory instanceof LettuceConnectionFactory) {
            LettuceConnectionFactory lcf = (LettuceConnectionFactory)redisConnectionFactory;
            lcf.setValidateConnection(true);
        }
        return new StringRedisTemplate(redisConnectionFactory);
    }
}
