package com.sipa.boot.core.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.sipa.boot.core.constant.SipaBootConstant;
import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.env.EnvConstant;
import com.sipa.boot.core.env.EnvProvider;

import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2019-02-20
 */
@Slf4j
public class SipaUtil {
    /**
     * object转string，过滤null
     *
     * @param obj
     *            object
     * @return string
     */
    public static String stringValueOf(Object obj) {
        return obj == null ? null : doStringValueOf(obj);
    }

    /**
     * object转string，过滤"null"
     *
     * @param obj
     *            object
     * @return string
     */
    private static String doStringValueOf(Object obj) {
        String as = String.valueOf(obj);
        return "null".equalsIgnoreCase(as) ? null : as;
    }

    /**
     * object转long
     * 
     * @param obj
     *            object
     * @return long
     */
    public static Long longValueOf(Object obj) {
        return obj == null || StringUtils.isBlank(stringValueOf(obj)) ? null : doLongValueOf(obj);
    }

    private static Long doLongValueOf(Object obj) {
        try {
            return Long.valueOf(stringValueOf(obj));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * int类型的string +1
     * 
     * @param intValueString
     *            int类型的string
     * @return +1后的string
     */
    public static String increase(String intValueString) {
        log.info("origin string value is [{}]", intValueString);
        try {
            int value = Integer.parseInt(intValueString);
            value++;
            log.info("increased string value is [{}]", value);
            return String.valueOf(value);
        } catch (NumberFormatException e) {
            log.warn("increase failed, input value is not an int value");
        }
        return intValueString;
    }

    /**
     * 转换13位时间戳
     *
     * @param timestamp
     *            时间戳
     * @return 13位时间戳
     */
    public static long convertTimestampTo13(long timestamp) {
        if (String.valueOf(timestamp).length() == SipaConstant.Number.INT_10) {
            return 1000L * timestamp;
        } else {
            return timestamp;
        }
    }

    /**
     * 转换10位时间戳
     *
     * @param timestamp
     *            时间戳
     * @return 10位时间戳
     */
    public static long convertTimestampTo10(long timestamp) {
        if (String.valueOf(timestamp).length() == SipaConstant.Number.INT_13) {
            return timestamp / 1000L;
        } else {
            return timestamp;
        }
    }

    /**
     * 校验位数
     *
     * @param args
     *            参数
     * @param len
     *            长度
     * @return 校验结果
     */
    public static boolean checkLength(@NonNull String args, int len) {
        return args.length() == len;
    }

    /**
     * object暗转转换为list<T>
     * 
     * @param src
     *            转换对象
     * @param clazz
     *            目标class
     * @return 转换后list 转换失败会返回null
     */
    public static <T> List<T> castList(Object src, Class<T> clazz) {
        if (src instanceof List<?>) {
            return ((List<?>)src).stream().map(clazz::cast).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * obj to map.
     * 
     * @param obj
     *            object
     * @return map
     */
    public static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>(16);
        if (Objects.nonNull(obj)) {
            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(obj.getClass());
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor property : propertyDescriptors) {
                    String key = property.getName();
                    if (key.compareToIgnoreCase("class") == 0) {
                        continue;
                    }
                    Method getter = property.getReadMethod();
                    Object value = getter != null ? getter.invoke(obj) : null;
                    map.put(key, value);
                }
            } catch (IntrospectionException e) {
                log.error("Utils.objectToMap -> Introspect.getBeanInfo fail", e);
                map = new HashMap<>(16);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Utils.objectToMap -> getter.invoke(obj) fail", e);
                map = new HashMap<>(16);
            }
        }
        return map;
    }

    /**
     * 获取本地ip列表
     * 
     * @return 本地ip列表
     */
    public static List<String> getLocalIps() {
        List<String> ips = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface ni = enumeration.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (ni.isLoopback() || !ni.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    String ip = inetAddresses.nextElement().getHostAddress();
                    // 排除 回环IP/ipv6 地址
                    if (ip.contains(":")) {
                        continue;
                    }
                    if (StringUtils.isNotBlank(ip)) {
                        ips.add(ip);
                    }
                }
            }
        } catch (Exception e) {
            log.info(StringUtils.EMPTY, e);
        }
        return ips;
    }

    /**
     * 获取内网ip，只用在非容器环境中。
     * 
     * @return 内网ip
     */
    public static String getLocalIntranetIp() {
        List<String> ips = getLocalIps();
        for (String ip : ips) {
            if (isIntranetIp(ip)) {
                log.info("get ip [{}]", ip);
                return ip;
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 判断是否内网ip
     *
     * @return 是否是内网ip
     */
    public static boolean isIntranetIp(String ip) {
        try {
            if (ip.startsWith("10.") || ip.startsWith("192.168.")) {
                return true;
            }

            // 172.16.x.x～172.31.x.x
            String[] ns = ip.split("\\.");
            int ipSub = Integer.parseInt(ns[0] + ns[1]);
            if (ipSub >= 17216 && ipSub <= 17231) {
                return true;
            }
        } catch (Exception e) {
            log.info(StringUtils.EMPTY, e);
        }
        return false;
    }

    /**
     * 是否是biz包
     * 
     * @return 是否是biz包
     */
    public static boolean isBiz() {
        return SipaUtil.class.getClassLoader() != Thread.currentThread().getContextClassLoader();
    }

    public static ExecutorService newNormalPool() {
        return TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(20, 50, 60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10000), new ThreadPoolExecutor.AbortPolicy()));
    }

    public static boolean isLocal() {
        return StrUtil.equals(EnvProvider.getEnv(), EnvConstant.ENV_LOCAL);
    }

    public static String getHostInContainer() {
        return SystemUtil.get(EnvConstant.HOSTNAME_NAME.toUpperCase(), SipaBootConstant.Core.UNKNOWN);
    }

    public static <T> Optional<T> first(List<T> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            return Optional.ofNullable(list.get(0));
        }
        return Optional.empty();
    }

    public static long number2longWithDefault(Number number) {
        return Optional.ofNullable(number).orElse(0).longValue();
    }
}
