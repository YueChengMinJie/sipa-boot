package com.sipa.boot.statemachine.impl;

import java.util.List;
import java.util.Map;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.statemachine.builder.base.FailCallback;
import com.sipa.boot.statemachine.State;
import com.sipa.boot.statemachine.StateMachine;
import com.sipa.boot.statemachine.Transition;
import com.sipa.boot.statemachine.Visitor;

/**
 * For performance consideration, The state machine is made "stateless" on purpose. Once it's built, it can be shared by
 * multi-thread
 * <p>
 * One side effect is since the state machine is stateless, we can not get current state from State Machine.
 *
 * @author caszhou
 * @date 2019/3/24
 */
public class StateMachineImpl<S, E, C> implements StateMachine<S, E, C> {
    private String machineId;

    private final Map<S, State<S, E, C>> stateMap;

    private boolean ready;

    private FailCallback<S, E, C> failCallback;

    public StateMachineImpl(Map<S, State<S, E, C>> stateMap) {
        this.stateMap = stateMap;
    }

    @Override
    public boolean verify(S sourceStateId, E event) {
        isReady();

        State<S, E, C> sourceState = getState(sourceStateId);

        List<Transition<S, E, C>> transitions = sourceState.getEventTransitions(event);

        return transitions != null && transitions.size() != 0;
    }

    @Override
    public S fireEvent(S sourceStateId, E event, C ctx) {
        isReady();
        Transition<S, E, C> transition = routeTransition(sourceStateId, event, ctx);
        if (transition == null) {
            Debugger.debug("There is no Transition for " + event);
            failCallback.onFail(sourceStateId, event, ctx);
            return sourceStateId;
        }
        return transition.transit(ctx, false).getId();
    }

    private Transition<S, E, C> routeTransition(S sourceStateId, E event, C ctx) {
        State<S, E, C> sourceState = getState(sourceStateId);

        List<Transition<S, E, C>> transitions = sourceState.getEventTransitions(event);
        if (transitions == null || transitions.size() == 0) {
            return null;
        }
        Transition<S, E, C> transit = null;
        for (Transition<S, E, C> transition : transitions) {
            if (transition.getCondition() == null) {
                transit = transition;
            } else if (transition.getCondition().isSatisfied(ctx)) {
                transit = transition;
                break;
            }
        }
        return transit;
    }

    private State<S, E, C> getState(S currentStateId) {
        return StateHelper.getState(stateMap, currentStateId);
    }

    private void isReady() {
        if (!ready) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.STATE_MACHINE_NOT_READY);
        }
    }

    @Override
    public String accept(Visitor visitor) {
        StringBuilder sb = new StringBuilder();
        sb.append(visitor.visitOnEntry(this));
        for (State<S, E, C> state : stateMap.values()) {
            sb.append(state.accept(visitor));
        }
        sb.append(visitor.visitOnExit(this));
        return sb.toString();
    }

    @Override
    public void showStateMachine() {
        Slf4jVisitor slf4jVisitor = new Slf4jVisitor();
        accept(slf4jVisitor);
    }

    @Override
    public String generatePlantUml() {
        PlantUMLVisitor umlVisitor = new PlantUMLVisitor();
        return accept(umlVisitor);
    }

    @Override
    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void setFailCallback(FailCallback<S, E, C> failCallback) {
        this.failCallback = failCallback;
    }
}
