package com.sipa.boot.statemachine.impl;

import java.util.Map;

import com.sipa.boot.statemachine.State;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public class StateHelper {
    public static <S, E, C> State<S, E, C> getState(Map<S, State<S, E, C>> stateMap, S stateId) {
        State<S, E, C> state = stateMap.get(stateId);
        if (state == null) {
            state = new StateImpl<>(stateId);
            stateMap.put(stateId, state);
        }
        return state;
    }
}
