package com.sipa.boot.lock.property;

import java.time.Duration;
import java.util.List;

import org.apache.commons.pool2.impl.DefaultEvictionPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.sipa.boot.core.constant.TcpCloudConstant;

import cn.hutool.extra.spring.SpringUtil;
import lombok.Data;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Data
@ConfigurationProperties(prefix = TcpCloudConstant.Lock.PREFIX)
public class LockProperty {
    /**
     * etcd配置
     */
    private EtcdLockProperty etcd;

    /**
     * redis配置
     */
    private RedisLockProperty redis;

    public static LockProperty self() {
        return SpringUtil.getBean(LockProperty.class);
    }

    @Data
    public static class EtcdLockProperty {
        /**
         * etcd集群url
         */
        private List<String> urls;

        /**
         * etcd集群名
         */
        private String clusterName;
    }

    @Data
    public static class RedisLockProperty {
        /**
         * 第几个库
         */
        private Integer database;

        /**
         * 主机名
         */
        private String host;

        /**
         * 端口
         */
        private Integer port;

        /**
         * 密码
         */
        private String password;

        /**
         * 是否集群，默认false
         */
        private Boolean cluster = Boolean.FALSE;

        /**
         * 集群类型，sharded or cluster
         */
        private String mode;

        /**
         * 集群ip
         */
        private String clusterIps;

        /**
         * 连接池中最小的空闲连接数，默认为0
         */
        private Integer minIdle = 0;

        /**
         * 连接池中最大的空闲连接数，默认为10
         */
        private Integer maxIdle = 10;

        /**
         * 连接池中最大的连接数，默认为200
         */
        private Integer maxTotal = 200;

        /**
         * 当连接池中连接数量达到上限且所有连接都在使用时，应用程序从连接池获取连接的最大等待时间，默认60秒
         */
        private Duration maxWait = Duration.ofSeconds(60);

        /**
         * 当连接池中没有可用连接时，获取连接的行为方式，默认为false
         */
        private Boolean block = Boolean.FALSE;

        /**
         * jmx配置
         */
        private RedisJmxLockProperty jmx = new RedisJmxLockProperty();

        /**
         * 设置当连接池中有空闲连接时，从连接池中获取连接的方式，默认为true
         */
        private Boolean lifo = Boolean.TRUE;

        /**
         * 驱逐策略配置
         */
        private RedisEvictionLockProperty eviction = new RedisEvictionLockProperty();

        /**
         * 每次从连接池中获取连接时，检测连接是否可用的方式，默认为true
         */
        private Boolean testBorrow = Boolean.TRUE;

        /**
         * 在空闲时检查有效性，默认为true
         */
        private Boolean testIdle = Boolean.TRUE;

        @Data
        public static class RedisJmxLockProperty {
            /**
             * 是否开启jmx，默认为false
             */
            private Boolean enabled = Boolean.FALSE;

            /**
             * jmx前缀
             */
            private String prefix;
        }

        @Data
        public static class RedisEvictionLockProperty {
            /**
             * 逐出扫描的时间间隔，默认为1分钟
             */
            private Duration runTime = Duration.ofMinutes(1);

            /**
             * 驱逐class，默认为默认的class
             */
            private String clazz = DefaultEvictionPolicy.class.getName();

            /**
             * 驱逐的最小空闲时间，默认为30分钟
             */
            private Duration minIdleTime = Duration.ofMinutes(30);

            /**
             * 对象空闲多久后逐出，默认为15分钟
             */
            private Duration softMinIdleTime = Duration.ofMinutes(15);

            /**
             * 每次逐出的最大数目，默认为3
             */
            private Integer num = 3;
        }
    }
}
