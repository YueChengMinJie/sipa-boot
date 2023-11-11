package com.sipa.boot.feign.log;

import org.springframework.cloud.openfeign.FeignLoggerFactory;

import feign.Logger;

/**
 * @author caszhou
 * @date 2023/6/9
 */
public class CustomFeignLoggerFactory implements FeignLoggerFactory {
    private final Logger logger;

    public CustomFeignLoggerFactory(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Logger create(Class<?> type) {
        return this.logger != null ? this.logger : new CustomSlf4jLogger(type);
    }
}
