package com.sipa.boot.test.ut.statemachine;

import com.sipa.boot.core.exception.system.SystemRuntimeException;
import com.sipa.boot.statemachine.Action;
import com.sipa.boot.statemachine.Condition;
import com.sipa.boot.statemachine.StateMachine;
import com.sipa.boot.statemachine.builder.StateMachineBuilderFactory;
import com.sipa.boot.statemachine.builder.base.StateMachineBuilder;
import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * StateMachineUnNormalTest
 *
 * @author caszhou
 * @date 2019/3/24
 */
@Slf4j
public class StateMachineUnNormalTest {
    @Test
    public void testConditionNotMeet() {
        StateMachineBuilder<StateMachineTest.State, StateMachineTest.Event, StateMachineTest.Context> builder =
            StateMachineBuilderFactory.create();
        builder.externalTransition()
            .from(StateMachineTest.State.STATE1)
            .to(StateMachineTest.State.STATE2)
            .on(StateMachineTest.Event.EVENT1)
            .when(checkConditionFalse())
            .perform(doAction());

        StateMachine<StateMachineTest.State, StateMachineTest.Event, StateMachineTest.Context> stateMachine =
            builder.build("NotMeetConditionMachine");
        StateMachineTest.State target = stateMachine.fireEvent(StateMachineTest.State.STATE1,
            StateMachineTest.Event.EVENT1, new StateMachineTest.Context());
        Assert.assertEquals(StateMachineTest.State.STATE1, target);
    }

    @Test(expected = SystemRuntimeException.class)
    public void testDuplicatedTransition() {
        StateMachineBuilder<StateMachineTest.State, StateMachineTest.Event, StateMachineTest.Context> builder =
            StateMachineBuilderFactory.create();
        builder.externalTransition()
            .from(StateMachineTest.State.STATE1)
            .to(StateMachineTest.State.STATE2)
            .on(StateMachineTest.Event.EVENT1)
            .when(checkCondition())
            .perform(doAction());

        builder.externalTransition()
            .from(StateMachineTest.State.STATE1)
            .to(StateMachineTest.State.STATE2)
            .on(StateMachineTest.Event.EVENT1)
            .when(checkCondition())
            .perform(doAction());
    }

    @Test(expected = SystemRuntimeException.class)
    public void testDuplicateMachine() {
        StateMachineBuilder<StateMachineTest.State, StateMachineTest.Event, StateMachineTest.Context> builder =
            StateMachineBuilderFactory.create();
        builder.externalTransition()
            .from(StateMachineTest.State.STATE1)
            .to(StateMachineTest.State.STATE2)
            .on(StateMachineTest.Event.EVENT1)
            .when(checkCondition())
            .perform(doAction());

        builder.build("DuplicatedMachine");
        builder.build("DuplicatedMachine");
    }

    private Condition<StateMachineTest.Context> checkCondition() {
        return (ctx) -> true;
    }

    private Condition<StateMachineTest.Context> checkConditionFalse() {
        return (ctx) -> false;
    }

    private Action<StateMachineTest.State, StateMachineTest.Event, StateMachineTest.Context> doAction() {
        return (from, to, event, ctx) -> log
            .info(ctx.operator + " is operating " + ctx.entityId + "from:" + from + " to:" + to + " on:" + event);
    }
}
