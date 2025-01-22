package com.sipa.boot.core.service;

/**
 * @author caszhou
 * @date 2025/1/22
 */
public interface CacheService {
    /**
     * 当前key是否在缓存中
     *
     * @param key
     *            健
     * @return 是否存在
     */
    boolean exist(String key);
}
