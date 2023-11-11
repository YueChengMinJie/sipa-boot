package com.sipa.boot.statemachine.impl;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.statemachine.enumeration.ETransitionType;
import com.sipa.boot.statemachine.Action;
import com.sipa.boot.statemachine.Condition;
import com.sipa.boot.statemachine.State;
import com.sipa.boot.statemachine.Transition;

/**
 * This should be designed to be immutable, so that there is no thread-safe risk
 *
 * @author caszhou
 * @date 2019/3/24
 */
public class TransitionImpl<S, E, C> implements Transition<S, E, C> {
    private State<S, E, C> source;

    private State<S, E, C> target;

    private E event;

    private Condition<C> condition;

    private Action<S, E, C> action;

    private ETransitionType type = ETransitionType.EXTERNAL;

    @Override
    public State<S, E, C> getSource() {
        return source;
    }

    @Override
    public void setSource(State<S, E, C> state) {
        this.source = state;
    }

    @Override
    public E getEvent() {
        return this.event;
    }

    @Override
    public void setEvent(E event) {
        this.event = event;
    }

    @Override
    public void setType(ETransitionType type) {
        this.type = type;
    }

    @Override
    public State<S, E, C> getTarget() {
        return this.target;
    }

    @Override
    public void setTarget(State<S, E, C> target) {
        this.target = target;
    }

    @Override
    public Condition<C> getCondition() {
        return this.condition;
    }

    @Override
    public void setCondition(Condition<C> condition) {
        this.condition = condition;
    }

    @Override
    public Action<S, E, C> getAction() {
        return this.action;
    }

    @Override
    public void setAction(Action<S, E, C> action) {
        this.action = action;
    }

    @Override
    public State<S, E, C> transit(C ctx, boolean checkCondition) {
        Debugger.debug("Do transition: " + this);
        this.verify();
        if (!checkCondition || condition == null || condition.isSatisfied(ctx)) {
            if (action != null) {
                action.execute(source.getId(), target.getId(), event, ctx);
            }
            return target;
        }
        Debugger.debug("Condition is not satisfied, stay at the " + source + " state ");
        return source;
    }

    @Override
    public final String toString() {
        return source + "-[" + event.toString() + ", " + type + "]->" + target;
    }

    @Override
    public boolean equals(Object anObject) {
        if (anObject instanceof Transition) {
            Transition other = (Transition)anObject;
            return this.event.equals(other.getEvent()) && this.source.equals(other.getSource())
                && this.target.equals(other.getTarget());
        }
        return false;
    }

    @Override
    public void verify() {
        if (type == ETransitionType.INTERNAL && source != target) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.INTERNAL_STATE_MUST_SAME, source, target);
        }
    }
}
