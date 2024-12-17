package com.sipa.boot.sentinel;

import com.sipa.boot.core.env.EnvPostProcessor;

/**
 * @author caszhou
 * @date 2022-09-14
 */
public class SentinelEnvProcessor extends EnvPostProcessor {
    @Override
    public String getName() {
        return "sipa-sentinel";
    }
}
