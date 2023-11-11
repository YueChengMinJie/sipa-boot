package com.sipa.boot.cache.mapper;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Component
public class CacheMapper {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void set(String key, String value, long timeout) {
        try {
            ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
            valueOperations.set(key, value, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    public void setZSet(String key, String value, double score) {
        try {
            ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
            zSetOperations.add(key, value, score);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    public void setZSet(String key, Set<ZSetOperations.TypedTuple<String>> tuples) {
        try {
            ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
            zSetOperations.add(key, tuples);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    public Set<String> zSetObtainAll(String key) {
        return zSetRange(key, 0, -1);
    }

    public Set<String> zSetRange(String key, long start, long end) {
        ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
        return zSetOperations.range(key, start, end);
    }

    public Long zSetCard(String key) {
        ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
        return zSetOperations.zCard(key);
    }

    public Set<String> zRangeByScore(String key, double min, double max, long offset, long count) {
        ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
        return zSetOperations.rangeByScore(key, min, max, offset, count);
    }

    public Double zScore(String key, Object v) {
        ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
        return zSetOperations.score(key, v);
    }

    public Long zRank(String key, Object v) {
        ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
        return zSetOperations.rank(key, v);
    }

    public Long zCount(String key, double min, double max) {
        ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
        return zSetOperations.count(key, min, max);
    }

    public void set(String key, String value) {
        try {
            ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
            valueOperations.set(key, value);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    public void batchSet(Map<String, String> map) {
        try {
            ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
            valueOperations.multiSet(map);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    public Set<String> getKeys(String pattern) {
        try {
            ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
            return valueOperations.getOperations().keys(pattern);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
        return new HashSet<>();
    }

    public boolean setIfAbsent(String key, String value, long timeout) {
        try {
            ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
            return Boolean.TRUE.equals(valueOperations.setIfAbsent(key, value, timeout, TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
        return false;
    }

    public boolean hasKey(String key) {
        boolean existValue = false;
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
        return existValue;
    }

    public String get(String key) {
        try {
            ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
            return valueOperations.get(key);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
        return "";
    }

    public List<String> get(Collection<String> keys) {
        try {
            ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
            return valueOperations.multiGet(keys);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
        return null;
    }

    public void delete(String key) {
        try {
            ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
            valueOperations.getOperations().delete(key);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    public void deleteKeys(Set<String> keys) {
        try {
            ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
            valueOperations.getOperations().delete(keys);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    public void deleteZSetMember(String key, String value) {
        try {
            ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
            zSetOperations.remove(key, value);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    public Long increment(String key) {
        try {
            return stringRedisTemplate.boundValueOps(key).increment();
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
        return 0L;
    }

    public Long increment(String key, long value) {
        try {
            return stringRedisTemplate.boundValueOps(key).increment(value);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
        return 0L;
    }

    public Long decrement(String key) {
        try {
            return stringRedisTemplate.boundValueOps(key).decrement();
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
        return 0L;
    }

    public Long decrement(String key, long value) {
        try {
            return stringRedisTemplate.boundValueOps(key).decrement(value);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
        return 0L;
    }

    public List<String> mGet(List<String> keyList) {
        try {
            return stringRedisTemplate.opsForValue().multiGet(keyList);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
        return Collections.emptyList();
    }

    public List<Object> batchGet(List<String> keys) {
        try {
            return stringRedisTemplate.executePipelined((RedisCallback<String>)connection -> {
                StringRedisConnection src = (StringRedisConnection)connection;
                for (String k : keys) {
                    src.get(k);
                }
                return null;
            });
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
        return Collections.emptyList();
    }

    public void hPutAll(String key, Map<String, String> maps) {
        try {
            stringRedisTemplate.opsForHash().putAll(key, maps);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    public void hSet(String key, String field, Object value) {
        try {
            stringRedisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    public void hPutIfAbsent(String key, String field, Object value) {
        try {
            stringRedisTemplate.opsForHash().putIfAbsent(key, field, value);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    public String hGet(String key, String field) {
        try {
            return (String)stringRedisTemplate.opsForHash().get(key, field);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
        return null;
    }

    public void hDel(String key, Object... fields) {
        try {
            stringRedisTemplate.opsForHash().delete(key, fields);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    public Map<Object, Object> hGetAll(String key) {
        try {
            return stringRedisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
        return null;
    }

    public void addSetValue(String key, String value) {
        try {
            stringRedisTemplate.opsForSet().add(key, value);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    public void removeSetValue(String key, String value) {
        try {
            stringRedisTemplate.opsForSet().remove(key, value);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
    }

    public boolean containsSetValue(String key, String value) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
        return false;
    }

    public Set<String> getSetValues(String key) {
        try {
            return stringRedisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error(StringUtils.EMPTY, e);
        }
        return null;
    }

    public RedisConnectionFactory factory() {
        return stringRedisTemplate.getConnectionFactory();
    }
}
