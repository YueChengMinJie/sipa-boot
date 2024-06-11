package com.sipa.boot.cache.serde;

import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

/**
 * @author caszhou
 * @date 2024/6/11
 */
public class SipaJdkSerializationRedisSerializer extends JdkSerializationRedisSerializer {
    public SipaJdkSerializationRedisSerializer(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    public Object deserialize(byte[] bytes) {
        try {
            return super.deserialize(bytes);
        } catch (Exception e) {
            return null;
        }
    }
}
