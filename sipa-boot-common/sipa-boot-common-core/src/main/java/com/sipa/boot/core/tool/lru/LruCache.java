package com.sipa.boot.core.tool.lru;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author caszhou
 * @date 2020/12/14
 */
public class LruCache<K, V> {
    private int capacity;

    private LinkedHashMap<K, V> cache;

    public LruCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > capacity;
            }
        };
    }

    public synchronized V get(K key) {
        return cache.get(key);
    }

    public synchronized void put(K key, V value) {
        cache.put(key, value);
    }

    public synchronized void remove(K key) {
        cache.remove(key);
    }

    public synchronized Set<Map.Entry<K, V>> entrySet() {
        return new HashSet<>(cache.entrySet());
    }

    public synchronized void clear() {
        cache.clear();
    }

    public synchronized int size() {
        return cache.size();
    }
}
