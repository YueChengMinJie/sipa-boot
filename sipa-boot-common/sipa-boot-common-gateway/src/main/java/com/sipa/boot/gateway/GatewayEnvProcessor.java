package com.sipa.boot.gateway;

import com.sipa.boot.core.env.EnvPostProcessor;

/**
 * @author caszhou
 * @since 2022/10/19
 */
public class GatewayEnvProcessor extends EnvPostProcessor {
    @Override
    public String getName() {
        return "sipa-gateway";
    }
}
