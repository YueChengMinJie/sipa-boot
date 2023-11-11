package com.sipa.boot.statemachine.impl;

import com.sipa.boot.statemachine.State;
import com.sipa.boot.statemachine.StateMachine;
import com.sipa.boot.statemachine.Transition;
import com.sipa.boot.statemachine.Visitor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2019/3/24
 */
@Slf4j
public class Slf4jVisitor implements Visitor {
    @Override
    public String visitOnEntry(StateMachine<?, ?, ?> stateMachine) {
        String entry = "-----StateMachine:" + stateMachine.getMachineId() + "-------";
        log.info(entry);
        return entry;
    }

    @Override
    public String visitOnExit(StateMachine<?, ?, ?> stateMachine) {
        String exit = "------------------------";
        log.info(exit);
        return exit;
    }

    @Override
    public String visitOnEntry(State<?, ?, ?> state) {
        StringBuilder sb = new StringBuilder();
        String stateStr = "State:" + state.getId();
        sb.append(stateStr).append(LF);
        log.info(stateStr);
        for (Transition<?, ?, ?> transition : state.getAllTransitions()) {
            String transitionStr = "    Transition:" + transition;
            sb.append(transitionStr).append(LF);
            log.info(transitionStr);
        }
        return sb.toString();
    }

    @Override
    public String visitOnExit(State<?, ?, ?> visitable) {
        return "";
    }
}
