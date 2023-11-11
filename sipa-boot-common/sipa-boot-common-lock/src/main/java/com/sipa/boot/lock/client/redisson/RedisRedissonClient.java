package com.sipa.boot.lock.client.redisson;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import com.sipa.boot.lock.property.LockProperty;

/**
 * @author caszhou
 * @date 2023/5/27
 */
public class RedisRedissonClient {
    private static RedissonClient redissonClient = null;

    private volatile static RedisRedissonClient client;

    private RedisRedissonClient() {
        init();
    }

    private void init() {
        String host = LockProperty.self().getRedis().getHost();
        Integer port = LockProperty.self().getRedis().getPort();
        String password = LockProperty.self().getRedis().getPassword();
        Integer database = LockProperty.self().getRedis().getDatabase();

        Config conf = new Config();
        SingleServerConfig singleServerConfig = conf.useSingleServer();
        String address = String.format("redis://%s:%s", host, port);
        conf.useSingleServer().setAddress(address);
        if (StringUtils.isNotBlank(password)) {
            singleServerConfig.setPassword(password);
        }
        singleServerConfig.setDatabase(database);
        Codec codec = new JsonJacksonCodec();
        conf.setCodec(codec);
        redissonClient = Redisson.create(conf);
    }

    /**
     * 双锁单例
     */
    public static RedisRedissonClient getInstance() {
        if (client == null) {
            synchronized (RedisRedissonClient.class) {
                if (client == null) {
                    client = new RedisRedissonClient();
                }
            }
        }
        return client;
    }

    public RLock getLock(String key) {
        return redissonClient.getLock(key);
    }
}
