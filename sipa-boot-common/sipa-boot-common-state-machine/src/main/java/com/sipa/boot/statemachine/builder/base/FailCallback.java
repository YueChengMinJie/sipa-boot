package com.sipa.boot.statemachine.builder.base;

/**
 * @author caszhou
 * @date 2019/3/24
 */
@FunctionalInterface
public interface FailCallback<S, E, C> {
    /**
     * Callback function to execute if failed to trigger an Event
     */
    void onFail(S sourceState, E event, C context);
}
