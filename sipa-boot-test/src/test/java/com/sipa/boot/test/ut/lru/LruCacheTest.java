package com.sipa.boot.test.ut.lru;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.sipa.boot.core.tool.lru.LruCache;
import org.junit.Test;

/**
 * @author caszhou
 * @date 2023/4/25
 */
public class LruCacheTest {
    @Test
    public void testIt() {
        LruCache<String, Integer> cache = new LruCache<>(3);

        assertNull(cache.get("a"));

        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        assertEquals(1, cache.get("a").intValue());

        cache.put("d", 4);

        assertNull(cache.get("b"));
        assertEquals(4, cache.get("d").intValue());

        cache.put("e", 5);

        assertNull(cache.get("c"));
        assertEquals(4, cache.get("d").intValue());
        assertEquals(5, cache.get("e").intValue());
    }
}
