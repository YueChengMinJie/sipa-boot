package com.sipa.boot.mqttv3.env;

import com.sipa.boot.core.env.EnvPostProcessor;

/**
 * @author caszhou
 * @date 2022-09-14
 */
public class Mqttv3EnvProcessor extends EnvPostProcessor {
    @Override
    public String getName() {
        return "sipa-mqtt";
    }
}
