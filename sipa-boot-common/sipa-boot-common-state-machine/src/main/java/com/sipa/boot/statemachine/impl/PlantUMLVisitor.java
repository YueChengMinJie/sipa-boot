package com.sipa.boot.statemachine.impl;

import com.sipa.boot.statemachine.State;
import com.sipa.boot.statemachine.StateMachine;
import com.sipa.boot.statemachine.Transition;
import com.sipa.boot.statemachine.Visitor;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public class PlantUMLVisitor implements Visitor {
    /**
     * Since the state machine is stateless, there is no initial state.
     * <p>
     * You have to add "[*] -> initialState" to mark it as a state machine diagram. otherwise it will be recognized as a
     * sequence diagram.
     *
     * @param visitable
     *            the element to be visited.
     */
    @Override
    public String visitOnEntry(StateMachine<?, ?, ?> visitable) {
        return "@startuml" + LF;
    }

    @Override
    public String visitOnExit(StateMachine<?, ?, ?> visitable) {
        return "@enduml";
    }

    @Override
    public String visitOnEntry(State<?, ?, ?> state) {
        StringBuilder sb = new StringBuilder();
        for (Transition<?, ?, ?> transition : state.getAllTransitions()) {
            sb.append(transition.getSource().getId())
                .append(" --> ")
                .append(transition.getTarget().getId())
                .append(" : ")
                .append(transition.getEvent())
                .append(LF);
        }
        return sb.toString();
    }

    @Override
    public String visitOnExit(State<?, ?, ?> state) {
        return "";
    }
}
