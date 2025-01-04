package com.sipa.boot.influxdb.env;

import com.sipa.boot.core.env.EnvPostProcessor;

/**
 * @author caszhou
 * @date 2022-09-14
 */
public class InfluxdbEnvProcessor extends EnvPostProcessor {
    @Override
    public String getName() {
        return "sipa-influxdb";
    }
}
