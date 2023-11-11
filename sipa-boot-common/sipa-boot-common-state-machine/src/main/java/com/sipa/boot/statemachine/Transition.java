package com.sipa.boot.statemachine;

import com.sipa.boot.statemachine.enumeration.ETransitionType;

/**
 * {@code Transition} is something what a state machine associates with a state changes.
 *
 * @param <S>
 *            the type of state
 * @param <E>
 *            the type of event
 * @param <C>
 *            the type of user defined context, which is used to hold application data
 * @author caszhou
 * @date 2019/3/24
 */
public interface Transition<S, E, C> {
    /**
     * Gets the source state of this transition.
     *
     * @return the source state
     */
    State<S, E, C> getSource();

    void setSource(State<S, E, C> state);

    E getEvent();

    void setEvent(E event);

    void setType(ETransitionType type);

    /**
     * Gets the target state of this transition.
     *
     * @return the target state
     */
    State<S, E, C> getTarget();

    void setTarget(State<S, E, C> state);

    /**
     * Gets the guard of this transition.
     *
     * @return the guard
     */
    Condition<C> getCondition();

    void setCondition(Condition<C> condition);

    Action<S, E, C> getAction();

    void setAction(Action<S, E, C> action);

    /**
     * Do transition from source state to target state.
     *
     * @return the target state
     */

    State<S, E, C> transit(C ctx, boolean checkCondition);

    /**
     * Verify transition correctness
     */
    void verify();
}
