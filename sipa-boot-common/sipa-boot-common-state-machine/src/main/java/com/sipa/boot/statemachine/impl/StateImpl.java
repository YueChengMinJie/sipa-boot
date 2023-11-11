package com.sipa.boot.statemachine.impl;

import java.util.Collection;
import java.util.List;

import com.sipa.boot.statemachine.enumeration.ETransitionType;
import com.sipa.boot.statemachine.State;
import com.sipa.boot.statemachine.Transition;
import com.sipa.boot.statemachine.Visitor;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public class StateImpl<S, E, C> implements State<S, E, C> {
    protected final S stateId;

    private final EventTransitions<S, E, C> eventTransitions = new EventTransitions<>();

    StateImpl(S stateId) {
        this.stateId = stateId;
    }

    @Override
    public Transition<S, E, C> addTransition(E event, State<S, E, C> target, ETransitionType transitionType) {
        Transition<S, E, C> newTransition = new TransitionImpl<>();
        newTransition.setSource(this);
        newTransition.setTarget(target);
        newTransition.setEvent(event);
        newTransition.setType(transitionType);

        Debugger.debug("Begin to add new transition: " + newTransition);

        eventTransitions.put(event, newTransition);
        return newTransition;
    }

    @Override
    public List<Transition<S, E, C>> getEventTransitions(E event) {
        return eventTransitions.get(event);
    }

    @Override
    public Collection<Transition<S, E, C>> getAllTransitions() {
        return eventTransitions.allTransitions();
    }

    @Override
    public S getId() {
        return stateId;
    }

    @Override
    public String accept(Visitor visitor) {
        String entry = visitor.visitOnEntry(this);
        String exit = visitor.visitOnExit(this);
        return entry + exit;
    }

    @Override
    public boolean equals(Object anObject) {
        if (anObject instanceof State) {
            State other = (State)anObject;
            return this.stateId.equals(other.getId());
        }
        return false;
    }

    @Override
    public String toString() {
        return stateId.toString();
    }
}
