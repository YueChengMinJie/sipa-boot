package com.sipa.boot.seata.env;

import com.sipa.boot.core.env.EnvPostProcessor;

/**
 * @author caszhou
 * @date 2022-09-14
 */
public class SeataEnvProcessor extends EnvPostProcessor {
    @Override
    public String getName() {
        return "sipa-seata";
    }
}
