package com.sipa.boot.statemachine.builder.base;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public interface To<S, E, C> {
    /**
     * Build transition event
     *
     * @param event
     *            transition event
     * @return On clause builder
     */
    On<S, E, C> on(E event);
}
