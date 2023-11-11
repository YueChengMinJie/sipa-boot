package com.sipa.boot.statemachine.builder.base;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public interface From<S, E, C> {
    /**
     * Build transition target state and return to clause builder
     *
     * @param stateId
     *            id of state
     * @return To clause builder
     */
    To<S, E, C> to(S stateId);
}
