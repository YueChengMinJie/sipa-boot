package com.sipa.boot.statemachine.builder.base;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public interface ExternalTransitionBuilder<S, E, C> {
    /**
     * Build transition source state.
     *
     * @param stateId
     *            id of state
     * @return from clause builder
     */
    From<S, E, C> from(S stateId);
}
