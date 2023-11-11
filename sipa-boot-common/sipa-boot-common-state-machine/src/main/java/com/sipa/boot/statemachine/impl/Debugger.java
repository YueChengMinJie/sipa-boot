package com.sipa.boot.statemachine.impl;

import lombok.extern.slf4j.Slf4j;

/**
 * This is used to decouple Logging framework dependency
 *
 * @author caszhou
 * @date 2019/3/24
 */
@Slf4j
public class Debugger {
    private static boolean isDebugOn = false;

    public static void debug(String message) {
        if (isDebugOn) {
            log.info(message);
        }
    }

    public static void enableDebug() {
        isDebugOn = true;
    }
}
