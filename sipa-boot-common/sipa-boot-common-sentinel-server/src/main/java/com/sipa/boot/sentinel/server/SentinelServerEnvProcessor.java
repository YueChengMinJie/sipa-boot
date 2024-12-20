package com.sipa.boot.sentinel.server;

import com.sipa.boot.core.env.EnvPostProcessor;

/**
 * @author caszhou
 * @date 2022-09-14
 */
public class SentinelServerEnvProcessor extends EnvPostProcessor {
    @Override
    public String getName() {
        return "sipa-sentinel-server";
    }
}
