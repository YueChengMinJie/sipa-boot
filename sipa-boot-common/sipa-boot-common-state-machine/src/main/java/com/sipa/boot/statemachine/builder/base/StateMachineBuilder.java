package com.sipa.boot.statemachine.builder.base;

import com.sipa.boot.statemachine.StateMachine;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public interface StateMachineBuilder<S, E, C> {
    /**
     * Builder for one transition
     *
     * @return External transition builder
     */
    ExternalTransitionBuilder<S, E, C> externalTransition();

    /**
     * Builder for multiple transitions
     *
     * @return External transition builder
     */
    ExternalTransitionsBuilder<S, E, C> externalTransitions();

    /**
     * Start to build internal transition
     *
     * @return Internal transition builder
     */
    InternalTransitionBuilder<S, E, C> internalTransition();

    /**
     * set up fail callback, default do nothing {@code NumbFailCallbackImpl}
     */
    void setFailCallback(FailCallback<S, E, C> callback);

    StateMachine<S, E, C> build(String machineId);
}
