package com.sipa.boot.nacos.env;

import com.sipa.boot.core.env.EnvPostProcessor;

/**
 * @author caszhou
 * @date 2022-09-14
 */
public class NacosEnvProcessor extends EnvPostProcessor {
    @Override
    public String getName() {
        return "sipa-nacos";
    }
}
