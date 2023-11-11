package com.sipa.boot.rocketmq.env;

import com.sipa.boot.core.env.EnvPostProcessor;

/**
 * @author caszhou
 * @date 2022-09-14
 */
public class RocketMQEnvProcessor extends EnvPostProcessor {
    @Override
    public String getName() {
        return "sipa-rmq";
    }
}
