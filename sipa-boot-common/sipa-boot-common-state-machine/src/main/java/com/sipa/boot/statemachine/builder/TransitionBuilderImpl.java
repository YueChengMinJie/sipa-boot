package com.sipa.boot.statemachine.builder;

import java.util.Map;

import com.sipa.boot.statemachine.Action;
import com.sipa.boot.statemachine.Condition;
import com.sipa.boot.statemachine.State;
import com.sipa.boot.statemachine.Transition;
import com.sipa.boot.statemachine.builder.base.*;
import com.sipa.boot.statemachine.enumeration.ETransitionType;
import com.sipa.boot.statemachine.impl.StateHelper;

/**
 * @author caszhou
 * @date 2019/3/24
 */
class TransitionBuilderImpl<S, E, C> implements ExternalTransitionBuilder<S, E, C>, InternalTransitionBuilder<S, E, C>,
    From<S, E, C>, On<S, E, C>, To<S, E, C> {
    final Map<S, State<S, E, C>> stateMap;

    private State<S, E, C> source;

    protected State<S, E, C> target;

    private Transition<S, E, C> transition;

    final ETransitionType transitionType;

    public TransitionBuilderImpl(Map<S, State<S, E, C>> stateMap, ETransitionType transitionType) {
        this.stateMap = stateMap;
        this.transitionType = transitionType;
    }

    @Override
    public From<S, E, C> from(S stateId) {
        this.source = StateHelper.getState(this.stateMap, stateId);
        return this;
    }

    @Override
    public To<S, E, C> to(S stateId) {
        this.target = StateHelper.getState(this.stateMap, stateId);
        return this;
    }

    @Override
    public To<S, E, C> within(S stateId) {
        this.source = this.target = StateHelper.getState(this.stateMap, stateId);
        return this;
    }

    @Override
    public When<S, E, C> when(Condition<C> condition) {
        this.transition.setCondition(condition);
        return this;
    }

    @Override
    public On<S, E, C> on(E event) {
        this.transition = this.source.addTransition(event, this.target, this.transitionType);
        return this;
    }

    @Override
    public void perform(Action<S, E, C> action) {
        this.transition.setAction(action);
    }
}
