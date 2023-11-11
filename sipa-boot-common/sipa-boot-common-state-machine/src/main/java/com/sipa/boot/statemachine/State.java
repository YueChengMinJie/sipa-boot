package com.sipa.boot.statemachine;

import java.util.Collection;
import java.util.List;

import com.sipa.boot.statemachine.enumeration.ETransitionType;

/**
 * @param <S>
 *            the type of state
 * @param <E>
 *            the type of event
 * @author caszhou
 * @date 2019/3/24
 */
public interface State<S, E, C> extends Visitable {
    /**
     * Gets the state identifier.
     *
     * @return the state identifiers
     */
    S getId();

    /**
     * Add transition to the state
     *
     * @param event
     *            the event of the Transition
     * @param target
     *            the target of the transition
     */
    Transition<S, E, C> addTransition(E event, State<S, E, C> target, ETransitionType transitionType);

    List<Transition<S, E, C>> getEventTransitions(E event);

    Collection<Transition<S, E, C>> getAllTransitions();
}
