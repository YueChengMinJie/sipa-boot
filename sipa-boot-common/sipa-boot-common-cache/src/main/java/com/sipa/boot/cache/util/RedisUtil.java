package com.sipa.boot.cache.util;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sipa.boot.core.util.SipaUtil;

/**
 * redis 工具类
 *
 * @author caszhou
 * @date 2019/2/24
 */
@Component
public class RedisUtil {
    private static StringRedisTemplate stringRedisTemplate;

    private static StringRedisSerializer redisSerializer;

    @Autowired
    public RedisUtil(StringRedisTemplate stringRedisTemplate, StringRedisSerializer redisSerializer) {
        RedisUtil.stringRedisTemplate = stringRedisTemplate;
        RedisUtil.redisSerializer = redisSerializer;
    }

    /**
     * 批量获取redis.
     *
     * @param key
     *            main key of redis
     * @return detail key-value map.
     */
    public static Map<Object, Object> entries(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * 批量获取redis.
     *
     * @param key
     *            main key of redis
     * @param hashKeySet
     *            detail key of redis
     * @return detail key-value map.
     */
    public static Map<String, String> hashBatchGet(String key, Set<String> hashKeySet) {
        Map<String, String> result = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(hashKeySet)) {
            hashKeySet.forEach(hashKey -> result.put(hashKey, hGet(key, hashKey)));
        }
        return result;
    }

    /**
     * 批量更新redis.
     *
     * @param key
     *            main key of redis
     * @param hashMap
     *            detail key-value map.
     */
    public static void hashBatchUpdate(String key, Map<String, String> hashMap) {
        if (MapUtils.isNotEmpty(hashMap)) {
            Map<String, String> addUpdateMap = new HashMap<>(16);
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (Objects.nonNull(v)) {
                    addUpdateMap.put(k, v);
                }
            }
            if (MapUtils.isNotEmpty(addUpdateMap)) {
                stringRedisTemplate.opsForHash().putAll(key, addUpdateMap);
            }
        }
    }

    /**
     * key是否存在.
     *
     * @param key
     *            key of redis
     * @return is exists.
     */
    public static Boolean isExists(String key) {
        return stringRedisTemplate.hasKey(Objects.requireNonNull(key));
    }

    /**
     * 获取value.
     *
     * @param key
     *            key of redis
     * @return string value.
     */
    public static String get(String key) {
        return SipaUtil.stringValueOf(stringRedisTemplate.opsForValue().get(key));
    }

    /**
     * 获取value.
     *
     * @param key
     *            key of redis
     * @return list
     */
    public static List<String> getList(String key) {
        return stringRedisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 根据vin 扫表
     *
     * @param key
     *            vin
     * @return String
     */
    public static Set<String> scan(String key) {
        Set<String> ks = new HashSet<>();
        Set<String> keys = stringRedisTemplate.keys(key + "*");
        Set<String> keys2 = stringRedisTemplate.keys("*" + key);
        if (CollectionUtils.isNotEmpty(keys)) {
            ks.addAll(keys);
        }
        if (CollectionUtils.isNotEmpty(keys2)) {
            ks.addAll(keys2);
        }
        return ks;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getKeysByPattern(String pattern) {
        String luaScript = "local keys = redis.call('keys', ARGV[1]) return keys";
        return getStringRedisTemplate().execute((connection) -> {
            Object o = connection.eval(luaScript.getBytes(), ReturnType.VALUE, 0, pattern.getBytes());

            if (o instanceof List) {
                List<byte[]> list = (List<byte[]>)o;
                return getKeys(list);
            } else if (o instanceof byte[]) {
                byte[] bytes = (byte[])o;
                return getKeys(List.of(bytes));
            }

            return Collections.emptyList();
        }, true);
    }

    private static List<String> getKeys(List<byte[]> list) {
        List<String> keys = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (byte[] bytes : list) {
                keys.add(new String(bytes));
            }
        }
        return keys;
    }

    public static List<String> getValuesByKeys(List<String> keys) {
        ValueOperations<String, String> valueOps = getStringRedisTemplate().opsForValue();
        return valueOps.multiGet(keys);
    }

    public static boolean expire(String key, long seconds) {
        return Boolean.TRUE.equals(getStringRedisTemplate().expire(key, seconds, TimeUnit.SECONDS));
    }

    /**
     * 获取hash的keys
     * 
     * @param key
     *            key
     * @return hkeys
     */
    public static Set<Object> hashKeys(String key) {
        return stringRedisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取hash value.
     *
     * @param key
     *            key of redis
     * @param hashKey
     *            hash key of redis
     * @return value of hash key.
     */
    public static String hGet(String key, String hashKey) {
        return SipaUtil.stringValueOf(stringRedisTemplate.opsForHash().get(key, hashKey));
    }

    /**
     * 设值hash value.
     *
     * @param key
     *            key of redis
     * @param hashKey
     *            hash key of redis
     * @param hashValue
     *            hash value of redis.
     */
    public static void hSet(String key, String hashKey, String hashValue) {
        hSet(key, hashKey, hashValue, -1L);
    }

    /**
     * 设值hash value.
     *
     * @param key
     *            key of redis
     * @param hashKey
     *            hash key of redis
     * @param hashValue
     *            hash value of redis
     * @param timeoutSec
     *            hash key time out.
     */
    public static void hSet(String key, String hashKey, String hashValue, Long timeoutSec) {
        stringRedisTemplate.opsForHash().put(key, hashKey, hashValue);
        if (timeoutSec > 0L) {
            stringRedisTemplate.expire(key, timeoutSec, TimeUnit.SECONDS);
        }
    }

    /**
     * hash set All
     * 
     * @param key
     *            key
     * @param map
     *            map
     */
    public static void hSetAll(String key, Map<String, String> map) {
        stringRedisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 赋值key于value.
     *
     * @param key
     *            key of redis
     * @param value
     *            value of key.
     */
    public static void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * batch set
     * 
     * @param keyAndValue
     *            key value map
     */
    public static void batchSet(Map<String, String> keyAndValue) {
        stringRedisTemplate.opsForValue().multiSet(keyAndValue);
    }

    /**
     * 赋值key于value.
     *
     * @param key
     *            key of redis
     * @param value
     *            value of key.
     */
    public static void set(String key, String value, Long time) {
        stringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 删除key.
     *
     * @param key
     *            key of redis.
     */
    public static void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 批量删除.
     *
     * @param keys
     *            key of redis.
     */
    public static void deleteKeys(Set<String> keys) {
        stringRedisTemplate.delete(keys);
    }

    /**
     * 删除hset里面的key
     *
     * @param key
     *            key
     * @param objects
     *            hashKey.
     */
    public static void deleteHashKey(String key, String[] objects) {
        stringRedisTemplate.opsForHash().delete(key, objects);
    }

    /**
     * 升1.
     *
     * @param key
     *            key of redis.
     */
    public static Long increase(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 降1.
     *
     * @param key
     *            key of redis.
     */
    public static void decrement(String key) {
        stringRedisTemplate.opsForValue().decrement(key);
    }

    /**
     * 获取hash.
     *
     * @param key
     *            key of redis
     * @return hash entries.
     */
    public static Map<String, Object> hGetAll(String key) {
        Map<String, Object> hash = new HashMap<>(128);

        Cursor<Map.Entry<Object, Object>> cursor = null;
        try {
            cursor = stringRedisTemplate.opsForHash().scan(key, ScanOptions.NONE);
            while (cursor.hasNext()) {
                Map.Entry<Object, Object> entry = cursor.next();
                if (entry.getKey() != null) {
                    hash.put(SipaUtil.stringValueOf(entry.getKey()), SipaUtil.stringValueOf(entry.getValue()));
                }
            }
        } finally {
            closeQuietly(cursor);
        }
        return hash;
    }

    private static void closeQuietly(Cursor<?> cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * set加个value.
     *
     * @param k
     *            key of redis
     * @param v
     *            value of key.
     */
    public static void sAdd(String k, String v) {
        stringRedisTemplate.opsForSet().add(k, v);
    }

    /**
     * 升固定值
     *
     * @param k
     *            key of redis
     * @param v
     *            value of key.
     */
    public static void increase(String k, long v) {
        stringRedisTemplate.opsForValue().increment(k, v);
    }

    /**
     * 降固定值
     *
     * @param k
     *            key of redis
     * @param v
     *            value of key.
     */
    public static void decrement(String k, long v) {
        stringRedisTemplate.opsForValue().decrement(k, v);
    }

    /**
     * The multi get, usually used to get strings.
     * 
     * @param keys
     *            key list
     * @return list of template value type.
     */
    public static List<String> multiGet(List<String> keys) {
        if (CollectionUtils.isNotEmpty(keys)) {
            return stringRedisTemplate.opsForValue().multiGet(keys);
        }
        return null;
    }

    public static void scanAndDeleteAll(String pattern) {
        RedisUtil.deleteKeys(scanKeys(pattern));
    }

    public static Set<String> scanKeys(String pattern) {
        return stringRedisTemplate.execute((RedisCallback<Set<String>>)connection -> {
            Set<String> keysTmp = new HashSet<>();
            Cursor<byte[]> cursor = null;
            try {
                cursor = connection.scan(ScanOptions.scanOptions().match(pattern).count(100).build());
                while (cursor.hasNext()) {
                    keysTmp.add(new String(cursor.next()));
                }
            } finally {
                closeQuietly(cursor);
            }
            return keysTmp;
        });
    }

    /**
     * 添加元素,有序集合是按照元素的score值由小到大排列
     *
     * @param key
     *            key
     * @param value
     *            值
     * @param score
     *            权值
     * @return 是否成功
     */
    public Boolean zAdd(String key, String value, double score) {
        return stringRedisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * @param key
     *            key
     * @param values
     *            值数组
     * @return 数量
     */
    public Long zRemove(String key, String... values) {
        return stringRedisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 获取集合大小
     *
     * @param key
     *            key
     * @return 长度
     */
    public Long zSize(String key) {
        return stringRedisTemplate.opsForZSet().size(key);
    }

    /**
     * 获取集合的元素, 从小到大排序
     *
     * @param key
     *            key
     * @param start
     *            开始位置
     * @param end
     *            结束位置, -1查询所有
     * @return 值集合
     */
    public Set<String> zRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 移除指定索引位置的成员
     *
     * @param key
     *            key
     * @param start
     *            开始位置
     * @param end
     *            结束位置
     * @return 剩余数量
     */
    public Long zRemoveRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * 根据指定的score值的范围来移除成员
     *
     * @param key
     *            key
     * @param min
     *            最小值
     * @param max
     *            最大值
     * @return 剩余数量
     */
    public Long zRemoveRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    public static void addListValue(String key, String o) {
        stringRedisTemplate.opsForList().rightPush(key, o);
    }

    public static void addListFirstValue(String key, String o) {
        stringRedisTemplate.opsForList().leftPush(key, o);
    }

    public static String removeListLastItem(String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    public static byte[] serialize(String kv) {
        return Objects.requireNonNull(redisSerializer.serialize(kv));
    }

    public static StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    // ***************************************************************************************************************
    // *********************************************** lock and unlock ***********************************************
    // ***************************************************************************************************************

    public static boolean tryGetDistributedLock(String lockKey, String requestId, int expireTimeSeconds) {
        return Optional
            .ofNullable(
                stringRedisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTimeSeconds, TimeUnit.SECONDS))
            .orElse(Boolean.FALSE);
    }

    public static boolean releaseDistributedLock(String lockKey, String requestId) {
        String script =
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);

        return Optional.ofNullable(stringRedisTemplate.execute(redisScript, Lists.newArrayList(lockKey), requestId))
            .orElse(Boolean.FALSE);
    }
}
