package com.sipa.boot.statemachine.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sipa.boot.statemachine.builder.base.ExternalTransitionsBuilder;
import com.sipa.boot.statemachine.builder.base.From;
import com.sipa.boot.statemachine.builder.base.On;
import com.sipa.boot.statemachine.builder.base.When;
import com.sipa.boot.statemachine.Action;
import com.sipa.boot.statemachine.Condition;
import com.sipa.boot.statemachine.State;
import com.sipa.boot.statemachine.Transition;
import com.sipa.boot.statemachine.enumeration.ETransitionType;
import com.sipa.boot.statemachine.impl.StateHelper;

/**
 * @author caszhou
 * @date 2019/3/24
 */
@SuppressWarnings("unchecked")
public class TransitionsBuilderImpl<S, E, C> extends TransitionBuilderImpl<S, E, C>
    implements ExternalTransitionsBuilder<S, E, C> {
    /**
     * This is for fromAmong where multiple sources can be configured to point to one target
     */
    private final List<State<S, E, C>> sources = new ArrayList<>();

    private final List<Transition<S, E, C>> transitions = new ArrayList<>();

    public TransitionsBuilderImpl(Map<S, State<S, E, C>> stateMap, ETransitionType transitionType) {
        super(stateMap, transitionType);
    }

    @Override
    public From<S, E, C> fromAmong(S... stateIds) {
        for (S stateId : stateIds) {
            sources.add(StateHelper.getState(super.stateMap, stateId));
        }
        return this;
    }

    @Override
    public On<S, E, C> on(E event) {
        for (State<S, E, C> source : sources) {
            Transition<S, E, C> transition = source.addTransition(event, super.target, super.transitionType);
            transitions.add(transition);
        }
        return this;
    }

    @Override
    public When<S, E, C> when(Condition<C> condition) {
        for (Transition<S, E, C> transition : transitions) {
            transition.setCondition(condition);
        }
        return this;
    }

    @Override
    public void perform(Action<S, E, C> action) {
        for (Transition<S, E, C> transition : transitions) {
            transition.setAction(action);
        }
    }
}
