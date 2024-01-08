package com.sipa.boot.lock.client.jedis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.sipa.boot.lock.property.LockProperty;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.*;
import redis.clients.jedis.commands.JedisClusterCommands;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.params.SetParams;

/**
 * redis 客户端：居于jedis实现
 *
 * @author caszhou
 * @date 2023/4/20
 */
@Slf4j
public class RedisJedisClient {
    /**
     * 集群模式-分布式集群
     */
    private final static String MODE_CLUSTER = "cluster";

    /**
     * 集群模式-分片集群
     */
    private final static String MODE_SHARDED = "sharded";

    /**
     * 链接池配置信息
     */
    private static final GenericObjectPoolConfig CONFIG = new GenericObjectPoolConfig();

    /**
     * 单机链接池
     */
    private static JedisPool jedisPool = null;

    /**
     * 分片链接池
     */
    private static ShardedJedisPool shardedJedisPool = null;

    /**
     * 集群链接池
     */
    private JedisCluster jedisCluster = null;

    static {
        // 最大空闲连接数
        CONFIG.setMaxIdle(LockProperty.self().getRedis().getMaxIdle());
        // 最大连接数
        CONFIG.setMaxTotal(LockProperty.self().getRedis().getMaxTotal());
        // 最小空闲连接数
        CONFIG.setMinIdle(LockProperty.self().getRedis().getMinIdle());
        // 最大等待毫秒数
        CONFIG.setMaxWait(LockProperty.self().getRedis().getMaxWait());
        // 连接耗尽时是否阻塞
        CONFIG.setBlockWhenExhausted(LockProperty.self().getRedis().getBlock());

        // 是否启用pool的jmx管理功能
        CONFIG.setJmxEnabled(LockProperty.self().getRedis().getJmx().getEnabled());
        CONFIG.setJmxNamePrefix(LockProperty.self().getRedis().getJmx().getPrefix());

        // 是否启用后进先出
        CONFIG.setLifo(LockProperty.self().getRedis().getLifo());

        // 逐出策略类名
        CONFIG.setEvictionPolicyClassName(LockProperty.self().getRedis().getEviction().getClazz());
        // 逐出连接的最小空闲时间
        CONFIG.setMinEvictableIdleTime(LockProperty.self().getRedis().getEviction().getMinIdleTime());
        // 每次逐出的最大数目
        CONFIG.setNumTestsPerEvictionRun(LockProperty.self().getRedis().getEviction().getNum());
        // 对象空闲多久后逐出
        CONFIG.setSoftMinEvictableIdleTime(LockProperty.self().getRedis().getEviction().getSoftMinIdleTime());
        // 逐出扫描的时间间隔
        CONFIG.setTimeBetweenEvictionRuns(LockProperty.self().getRedis().getEviction().getRunTime());

        // 在获取连接的时候检查有效性
        CONFIG.setTestOnBorrow(LockProperty.self().getRedis().getTestBorrow());
        // 在空闲时检查有效性
        CONFIG.setTestWhileIdle(LockProperty.self().getRedis().getTestIdle());
    }

    private volatile static RedisJedisClient client;

    private RedisJedisClient() {
        init();
    }

    private void init() {
        String host = LockProperty.self().getRedis().getHost();
        int port = LockProperty.self().getRedis().getPort();
        String pwd = LockProperty.self().getRedis().getPassword();
        pwd = null == pwd || "".equals(pwd.trim()) ? null : pwd;
        int db = LockProperty.self().getRedis().getDatabase();
        if (LockProperty.self().getRedis().getCluster()) {
            String mode = LockProperty.self().getRedis().getMode();
            String[] ips = LockProperty.self().getRedis().getClusterIps().split(",");
            if (MODE_SHARDED.equals(mode)) {
                // 分片 集群
                List<JedisShardInfo> shards = new ArrayList<>();
                JedisShardInfo master = new JedisShardInfo(host, port, "master");
                if (null != pwd) {
                    master.setPassword(pwd);
                }
                shards.add(master);
                for (String ip : ips) {
                    if (!"".equals(ip.trim())) {
                        JedisShardInfo shardInfo = new JedisShardInfo(ip, port, "slave_" + ip);
                        if (null != pwd) {
                            shardInfo.setPassword(pwd);
                        }
                        shards.add(shardInfo);
                    }
                }
                shardedJedisPool = new ShardedJedisPool(CONFIG, shards);
            } else if (MODE_CLUSTER.equals(mode)) {
                // 分布式 集群
                Set<HostAndPort> jedisClusterNodes = new HashSet<>();
                for (String ip : ips) {
                    if (!"".equals(ip.trim())) {
                        jedisClusterNodes.add(new HostAndPort(ip, port));
                    }
                }
                jedisCluster = new JedisCluster(jedisClusterNodes, 2000, 2000, 5, pwd, CONFIG);
            }
        } else {
            jedisPool = new JedisPool(CONFIG, host, port, 30000, pwd, db);
        }
    }

    /**
     * 双锁单例
     */
    public static RedisJedisClient getInstance() {
        if (client == null) {
            synchronized (RedisJedisClient.class) {
                if (client == null) {
                    client = new RedisJedisClient();
                }
            }
        }
        return client;
    }

    /**
     * Jedis 命令对象
     */
    protected abstract class BaseCommand {
        /**
         * 运行生命周期
         */
        @SuppressWarnings("unchecked")
        public <T> T exeLife() {
            T obj = null;
            if (null != jedisPool) {
                try (Jedis jedis = jedisPool.getResource()) {
                    obj = (T)exe(jedis);
                } catch (Exception e) {
                    log.error(StringUtils.EMPTY, e);
                }
            } else if (null != shardedJedisPool) {
                try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
                    obj = (T)exe(shardedJedis);
                } catch (Exception e) {
                    log.error(StringUtils.EMPTY, e);
                }
            } else if (null != jedisCluster) {
                obj = (T)exe(jedisCluster);
            }
            return obj;
        }

        /**
         * 执行命令
         */
        public abstract Object exe(JedisCommands commands);

        /**
         * 执行命令-cluster
         */
        public abstract Object exe(JedisClusterCommands commands);
    }

    /**
     * set NX PX
     */
    public String setNxPx(String key, String value, long second) {
        return new BaseCommand() {
            @Override
            public String exe(JedisCommands commands) {
                String realKey = getRealKey(key);
                return commands.set(realKey, value, SetParams.setParams().nx().ex(second));
            }

            @Override
            public Object exe(JedisClusterCommands commands) {
                String realKey = getRealKey(key);
                return commands.set(realKey, value, SetParams.setParams().nx().ex(second));
            }
        }.exeLife();
    }

    /**
     * 设置过期时间
     */
    public Long expire(String key, long second) {
        return new BaseCommand() {
            @Override
            public Long exe(JedisCommands commands) {
                return commands.expire(key, second);
            }

            @Override
            public Object exe(JedisClusterCommands commands) {
                return commands.expire(key, second);
            }
        }.exeLife();
    }

    /**
     * 删除缓存项(校验value)
     */
    public Long delete(String key, String value) {
        // TODO: 此处需要用lua才能保证原子性，否则有一定几率误删锁，增加AP Redis多重获锁的几率。 Jedis框架的ShardedJedis不支持Lua，改造成本较大，偷个懒。坐等夏老板修复
        return new BaseCommand() {
            @Override
            public Long exe(JedisCommands commands) {
                if (commands.exists(key) && commands.get(key).equals(value)) {
                    return commands.del(key);
                }
                return 0L;
            }

            @Override
            public Object exe(JedisClusterCommands commands) {
                if (commands.exists(key) && commands.get(key).equals(value)) {
                    return commands.del(key);
                }
                return 0L;
            }
        }.exeLife();
    }

    public static String getRealKey(String key) {
        return key;
    }
}
