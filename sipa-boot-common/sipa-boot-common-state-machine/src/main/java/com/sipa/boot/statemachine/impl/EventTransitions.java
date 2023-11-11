package com.sipa.boot.statemachine.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.statemachine.Transition;

/**
 * 同一个Event可以触发多个Transitions，<a href="https://github.com/alibaba/COLA/pull/158">...</a>
 *
 * @author caszhou
 * @date 2019/3/24
 */
public class EventTransitions<S, E, C> {
    private final HashMap<E, List<Transition<S, E, C>>> eventTransitions;

    public EventTransitions() {
        eventTransitions = new HashMap<>(16);
    }

    public void put(E event, Transition<S, E, C> transition) {
        if (eventTransitions.get(event) == null) {
            List<Transition<S, E, C>> transitions = new ArrayList<>();
            transitions.add(transition);
            eventTransitions.put(event, transitions);
        } else {
            List<Transition<S, E, C>> existingTransitions = eventTransitions.get(event);
            verify(existingTransitions, transition);
            existingTransitions.add(transition);
        }
    }

    /**
     * Per one source and target state, there is only one transition is allowed
     */
    private void verify(List<Transition<S, E, C>> existingTransitions, Transition<S, E, C> newTransition) {
        for (Transition<S, E, C> transition : existingTransitions) {
            if (transition.equals(newTransition)) {
                throw SystemExceptionFactory.bizException(ESystemErrorCode.TRANSITION_ALREADY_EXIST,
                    transition.toString());
            }
        }
    }

    public List<Transition<S, E, C>> get(E event) {
        return eventTransitions.get(event);
    }

    public List<Transition<S, E, C>> allTransitions() {
        List<Transition<S, E, C>> allTransitions = new ArrayList<>();
        for (List<Transition<S, E, C>> transitions : eventTransitions.values()) {
            allTransitions.addAll(transitions);
        }
        return allTransitions;
    }
}
