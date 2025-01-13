package com.sipa.boot.es;

import com.sipa.boot.core.env.EnvPostProcessor;

/**
 * @author caszhou
 * @date 2022-09-14
 */
public class ElasticsearchEnvProcessor extends EnvPostProcessor {
    @Override
    public String getName() {
        return "sipa-elasticsearch";
    }
}
