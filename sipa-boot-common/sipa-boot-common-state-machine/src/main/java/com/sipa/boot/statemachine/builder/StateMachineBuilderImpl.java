package com.sipa.boot.statemachine.builder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sipa.boot.statemachine.State;
import com.sipa.boot.statemachine.StateMachine;
import com.sipa.boot.statemachine.builder.base.*;
import com.sipa.boot.statemachine.enumeration.ETransitionType;
import com.sipa.boot.statemachine.factory.StateMachineFactory;
import com.sipa.boot.statemachine.impl.StateMachineImpl;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public class StateMachineBuilderImpl<S, E, C> implements StateMachineBuilder<S, E, C> {
    /**
     * StateMap is the same with stateMachine, as the core of state machine is holding reference to states.
     */
    private final Map<S, State<S, E, C>> stateMap = new ConcurrentHashMap<>();

    private final StateMachineImpl<S, E, C> stateMachine = new StateMachineImpl<>(this.stateMap);

    private FailCallback<S, E, C> failCallback = new NumbFailCallback<>();

    @Override
    public ExternalTransitionBuilder<S, E, C> externalTransition() {
        return new TransitionBuilderImpl<>(this.stateMap, ETransitionType.EXTERNAL);
    }

    @Override
    public ExternalTransitionsBuilder<S, E, C> externalTransitions() {
        return new TransitionsBuilderImpl<>(this.stateMap, ETransitionType.EXTERNAL);
    }

    @Override
    public InternalTransitionBuilder<S, E, C> internalTransition() {
        return new TransitionBuilderImpl<>(this.stateMap, ETransitionType.INTERNAL);
    }

    @Override
    public void setFailCallback(FailCallback<S, E, C> callback) {
        this.failCallback = callback;
    }

    @Override
    public StateMachine<S, E, C> build(String machineId) {
        this.stateMachine.setMachineId(machineId);
        this.stateMachine.setReady(true);
        this.stateMachine.setFailCallback(this.failCallback);
        StateMachineFactory.register(this.stateMachine);
        return this.stateMachine;
    }
}
