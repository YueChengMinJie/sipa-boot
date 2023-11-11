package com.sipa.boot.statemachine.builder.base;

import com.sipa.boot.statemachine.Condition;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public interface On<S, E, C> extends When<S, E, C> {
    /**
     * Add condition for the transition
     *
     * @param condition
     *            transition condition
     * @return When clause builder
     */
    When<S, E, C> when(Condition<C> condition);
}
