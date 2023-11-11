package com.sipa.boot.statemachine;

/**
 * @param <S>
 *            the type of state
 * @param <E>
 *            the type of event
 * @param <C>
 *            the user defined context
 * @author caszhou
 * @date 2019/3/24
 */
public interface StateMachine<S, E, C> extends Visitable {
    /**
     * Verify if an event {@code E} can be fired from current state {@code S}
     */
    boolean verify(S sourceStateId, E event);

    /**
     * Send an event {@code E} to the state machine.
     *
     * @param sourceState
     *            the source state
     * @param event
     *            the event to send
     * @param ctx
     *            the user defined context
     * @return the target state
     */
    S fireEvent(S sourceState, E event, C ctx);

    /**
     * MachineId is the identifier for a State Machine
     */
    String getMachineId();

    /**
     * Use visitor pattern to display the structure of the state machine
     */
    void showStateMachine();

    /**
     * generate plantuml
     */
    String generatePlantUml();
}
