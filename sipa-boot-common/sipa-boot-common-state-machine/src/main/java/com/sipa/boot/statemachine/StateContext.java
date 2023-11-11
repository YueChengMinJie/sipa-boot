package com.sipa.boot.statemachine;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public interface StateContext<S, E, C> {
    /**
     * Gets the transition.
     *
     * @return the transition
     */
    Transition<S, E, C> getTransition();

    /**
     * Gets the state machine.
     *
     * @return the state machine
     */
    StateMachine<S, E, C> getStateMachine();
}
