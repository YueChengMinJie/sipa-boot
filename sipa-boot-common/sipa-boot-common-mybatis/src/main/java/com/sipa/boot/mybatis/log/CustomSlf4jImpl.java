package com.sipa.boot.mybatis.log;

import org.apache.ibatis.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NoArgsConstructor;

/**
 * @author caszhou
 * @date 2023/6/9
 */
@NoArgsConstructor
public class CustomSlf4jImpl implements Log {
    private Logger log = LoggerFactory.getLogger(CustomSlf4jImpl.class);

    public CustomSlf4jImpl(String name) {
        this.log = LoggerFactory.getLogger(name);
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(String s, Throwable throwable) {
        log.error(s, throwable);
    }

    @Override
    public void error(String s) {
        log.error(s);
    }

    @Override
    public void debug(String s) {
        log.info(s);
    }

    @Override
    public void trace(String s) {
        log.info(s);
    }

    @Override
    public void warn(String s) {
        log.warn(s);
    }
}
