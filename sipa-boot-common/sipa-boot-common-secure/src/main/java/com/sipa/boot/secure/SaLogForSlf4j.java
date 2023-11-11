package com.sipa.boot.secure;

import cn.dev33.satoken.log.SaLog;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/5/9
 */
@Slf4j
public class SaLogForSlf4j implements SaLog {
    @Override
    public void trace(String str, Object... args) {
        log.trace(str, args);
    }

    @Override
    public void debug(String str, Object... args) {
        log.debug(str, args);
    }

    @Override
    public void info(String str, Object... args) {
        log.info(str, args);
    }

    @Override
    public void warn(String str, Object... args) {
        log.warn(str, args);
    }

    @Override
    public void error(String str, Object... args) {
        log.error(str, args);
    }

    @Override
    public void fatal(String str, Object... args) {
        log.error(str, args);
    }
}
