package com.sipa.boot.core.env;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author caszhou
 * @date 2022-09-14
 */
public class EnvCache {
    private static final Map<String, Boolean> ENV_INITIALIZATION_CACHE = new ConcurrentHashMap<>();

    public static Map<String, Boolean> getEnvInitializationCache() {
        return ENV_INITIALIZATION_CACHE;
    }
}
