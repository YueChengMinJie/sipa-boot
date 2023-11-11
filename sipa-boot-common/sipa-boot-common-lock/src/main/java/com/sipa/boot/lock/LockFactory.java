package com.sipa.boot.lock;

import com.sipa.boot.lock.lock.etcd.EtcdSolution;
import com.sipa.boot.lock.lock.jedis.RedisJedisSolution;
import com.sipa.boot.lock.lock.redisson.RedisRedissonSolution;

/**
 * 锁工厂
 * 
 * @author caszhou
 * @date 2023/4/20
 */
public class LockFactory {
    /**
     * 获取etcd锁方案对象
     */
    public EtcdSolution etcdSolution() {
        return new EtcdSolution();
    }

    /**
     * 获取redis锁方案对象
     */
    public RedisJedisSolution redisJedisSolution() {
        return new RedisJedisSolution();
    }

    /**
     * 获取redisson锁方案对象
     */
    public RedisRedissonSolution redisRedissonSolution() {
        return new RedisRedissonSolution();
    }
}
