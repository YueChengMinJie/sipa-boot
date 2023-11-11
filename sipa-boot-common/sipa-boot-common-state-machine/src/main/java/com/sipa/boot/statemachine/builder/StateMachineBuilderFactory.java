package com.sipa.boot.statemachine.builder;

import com.sipa.boot.statemachine.builder.base.StateMachineBuilder;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public class StateMachineBuilderFactory {
    public static <S, E, C> StateMachineBuilder<S, E, C> create() {
        return new StateMachineBuilderImpl<>();
    }
}
