package com.sipa.boot.statemachine;

/**
 * Generic strategy interface used by a state machine to respond events by executing an {@code Action} with a
 * {@link StateContext}.
 *
 * @author caszhou
 * @date 2019/3/24
 */
public interface Action<S, E, C> {
    void execute(S from, S to, E event, C context);
}
