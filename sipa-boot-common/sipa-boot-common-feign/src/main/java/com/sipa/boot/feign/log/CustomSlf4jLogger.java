package com.sipa.boot.feign.log;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feign.Request;
import feign.Response;

/**
 * @author caszhou
 * @date 2023/6/9
 */
public class CustomSlf4jLogger extends feign.Logger {
    private final Logger logger;

    public CustomSlf4jLogger() {
        this(feign.Logger.class);
    }

    public CustomSlf4jLogger(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    public CustomSlf4jLogger(String name) {
        this(LoggerFactory.getLogger(name));
    }

    CustomSlf4jLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        super.logRequest(configKey, logLevel, request);
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime)
        throws IOException {
        return super.logAndRebufferResponse(configKey, logLevel, response, elapsedTime);
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        logger.info(String.format(methodTag(configKey) + format, args));
    }
}
