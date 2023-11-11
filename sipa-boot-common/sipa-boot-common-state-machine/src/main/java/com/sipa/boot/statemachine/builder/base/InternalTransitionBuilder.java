package com.sipa.boot.statemachine.builder.base;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public interface InternalTransitionBuilder<S, E, C> {
    /**
     * Build an internal transition
     *
     * @param stateId
     *            id of transition
     * @return To clause builder
     */
    To<S, E, C> within(S stateId);
}
