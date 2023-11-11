package com.sipa.boot.statemachine;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public interface Visitor {
    char LF = '\n';

    /**
     * @param visitable
     *            the element to be visited.
     */
    String visitOnEntry(StateMachine<?, ?, ?> visitable);

    /**
     * @param visitable
     *            the element to be visited.
     */
    String visitOnExit(StateMachine<?, ?, ?> visitable);

    /**
     * @param visitable
     *            the element to be visited.
     */
    String visitOnEntry(State<?, ?, ?> visitable);

    /**
     * @param visitable
     *            the element to be visited.
     */
    String visitOnExit(State<?, ?, ?> visitable);
}
