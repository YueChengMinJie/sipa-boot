package com.sipa.boot.statemachine.builder.base;

import com.sipa.boot.statemachine.Action;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public interface When<S, E, C> {
    /**
     * Define action to be performed during transition
     *
     * @param action
     *            performed action
     */
    void perform(Action<S, E, C> action);
}
