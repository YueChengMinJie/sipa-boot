package com.sipa.boot.statemachine.builder.base;

/**
 * This builder is for multiple transitions, currently only support multiple sources <----> one target
 *
 * @author caszhou
 * @date 2019/3/24
 */
public interface ExternalTransitionsBuilder<S, E, C> {
    From<S, E, C> fromAmong(S... stateIds);
}
