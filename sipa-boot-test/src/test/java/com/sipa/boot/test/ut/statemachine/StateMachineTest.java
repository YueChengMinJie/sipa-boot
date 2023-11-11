package com.sipa.boot.test.ut.statemachine;

import com.sipa.boot.core.exception.system.SystemRuntimeException;
import com.sipa.boot.statemachine.Action;
import com.sipa.boot.statemachine.Condition;
import com.sipa.boot.statemachine.StateMachine;
import com.sipa.boot.statemachine.builder.AlertFailCallback;
import com.sipa.boot.statemachine.builder.StateMachineBuilderFactory;
import com.sipa.boot.statemachine.builder.base.StateMachineBuilder;
import com.sipa.boot.statemachine.factory.StateMachineFactory;
import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * StateMachineTest
 *
 * @author caszhou
 * @date 2019/3/24
 */
@Slf4j
public class StateMachineTest {
    static String MACHINE_ID = "TestStateMachine";

    enum State {
        STATE1, STATE2, STATE3, STATE4
    }

    enum Event {
        EVENT1, EVENT2, EVENT3, EVENT4, INTERNAL_EVENT
    }

    static class Context {
        String operator = "caszhou";

        String entityId = "123456";
    }

    @Test
    public void testExternalNormal() {
        StateMachineBuilder<State, Event, Context> builder = StateMachineBuilderFactory.create();
        builder.externalTransition()
            .from(State.STATE1)
            .to(State.STATE2)
            .on(Event.EVENT1)
            .when(checkCondition())
            .perform(doAction());

        StateMachine<State, Event, Context> stateMachine = builder.build(MACHINE_ID);
        State target = stateMachine.fireEvent(State.STATE1, Event.EVENT1, new Context());
        Assert.assertEquals(State.STATE2, target);
    }

    @Test
    public void testFail() {
        StateMachineBuilder<State, Event, Context> builder = StateMachineBuilderFactory.create();
        builder.externalTransition()
            .from(State.STATE1)
            .to(State.STATE2)
            .on(Event.EVENT1)
            .when(checkCondition())
            .perform(doAction());

        builder.setFailCallback(new AlertFailCallback<>());

        StateMachine<State, Event, Context> stateMachine = builder.build(MACHINE_ID + "-testFail");
        Assert.assertThrows(SystemRuntimeException.class,
            () -> stateMachine.fireEvent(State.STATE2, Event.EVENT1, new Context()));
    }

    @Test
    public void testVerify() {
        StateMachineBuilder<State, Event, Context> builder = StateMachineBuilderFactory.create();
        builder.externalTransition()
            .from(State.STATE1)
            .to(State.STATE2)
            .on(Event.EVENT1)
            .when(checkCondition())
            .perform(doAction());

        StateMachine<State, Event, Context> stateMachine = builder.build(MACHINE_ID + "-testVerify");

        Assert.assertTrue(stateMachine.verify(State.STATE1, Event.EVENT1));
        Assert.assertFalse(stateMachine.verify(State.STATE1, Event.EVENT2));
    }

    @Test
    public void testExternalTransitionsNormal() {
        StateMachineBuilder<State, Event, Context> builder = StateMachineBuilderFactory.create();
        builder.externalTransitions()
            .fromAmong(State.STATE1, State.STATE2, State.STATE3)
            .to(State.STATE4)
            .on(Event.EVENT1)
            .when(checkCondition())
            .perform(doAction());

        StateMachine<State, Event, Context> stateMachine = builder.build(MACHINE_ID + "-testExternalTransitionsNormal");
        State target = stateMachine.fireEvent(State.STATE2, Event.EVENT1, new Context());
        Assert.assertEquals(State.STATE4, target);
    }

    @Test
    public void testInternalNormal() {
        StateMachineBuilder<State, Event, Context> builder = StateMachineBuilderFactory.create();
        builder.internalTransition()
            .within(State.STATE1)
            .on(Event.INTERNAL_EVENT)
            .when(checkCondition())
            .perform(doAction());
        StateMachine<State, Event, Context> stateMachine = builder.build(MACHINE_ID + "-testInternalNormal");

        stateMachine.fireEvent(State.STATE1, Event.EVENT1, new Context());
        State target = stateMachine.fireEvent(State.STATE1, Event.INTERNAL_EVENT, new Context());
        Assert.assertEquals(State.STATE1, target);
    }

    @Test
    public void testExternalInternalNormal() {
        StateMachine<State, Event, Context> stateMachine = buildStateMachine("testExternalInternalNormal");

        Context context = new Context();
        State target = stateMachine.fireEvent(State.STATE1, Event.EVENT1, context);
        Assert.assertEquals(State.STATE2, target);
        target = stateMachine.fireEvent(State.STATE2, Event.INTERNAL_EVENT, context);
        Assert.assertEquals(State.STATE2, target);
        target = stateMachine.fireEvent(State.STATE2, Event.EVENT2, context);
        Assert.assertEquals(State.STATE1, target);
        target = stateMachine.fireEvent(State.STATE1, Event.EVENT3, context);
        Assert.assertEquals(State.STATE3, target);
    }

    private StateMachine<State, Event, Context> buildStateMachine(String machineId) {
        StateMachineBuilder<State, Event, Context> builder = StateMachineBuilderFactory.create();
        builder.externalTransition()
            .from(State.STATE1)
            .to(State.STATE2)
            .on(Event.EVENT1)
            .when(checkCondition())
            .perform(doAction());

        builder.internalTransition()
            .within(State.STATE2)
            .on(Event.INTERNAL_EVENT)
            .when(checkCondition())
            .perform(doAction());

        builder.externalTransition()
            .from(State.STATE2)
            .to(State.STATE1)
            .on(Event.EVENT2)
            .when(checkCondition())
            .perform(doAction());

        builder.externalTransition()
            .from(State.STATE1)
            .to(State.STATE3)
            .on(Event.EVENT3)
            .when(checkCondition())
            .perform(doAction());

        builder.externalTransitions()
            .fromAmong(State.STATE1, State.STATE2, State.STATE3)
            .to(State.STATE4)
            .on(Event.EVENT4)
            .when(checkCondition())
            .perform(doAction());

        builder.build(machineId);

        StateMachine<State, Event, Context> stateMachine = StateMachineFactory.get(machineId);
        stateMachine.showStateMachine();
        return stateMachine;
    }

    @Test
    public void testMultiThread() {
        buildStateMachine("testMultiThread");
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                StateMachine<State, Event, Context> stateMachine = StateMachineFactory.get("testMultiThread");
                State target = stateMachine.fireEvent(State.STATE1, Event.EVENT1, new Context());
                Assert.assertEquals(State.STATE2, target);
            });
            thread.start();
        }
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                StateMachine<State, Event, Context> stateMachine = StateMachineFactory.get("testMultiThread");
                State target = stateMachine.fireEvent(State.STATE1, Event.EVENT4, new Context());
                Assert.assertEquals(State.STATE4, target);
            });
            thread.start();
        }
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                StateMachine<State, Event, Context> stateMachine = StateMachineFactory.get("testMultiThread");
                State target = stateMachine.fireEvent(State.STATE1, Event.EVENT3, new Context());
                Assert.assertEquals(State.STATE3, target);
            });
            thread.start();
        }
    }

    private Condition<Context> checkCondition() {
        return context -> {
            log.info("Check condition : " + context);
            return true;
        };
    }

    private Action<State, Event, Context> doAction() {
        return (from, to, event, ctx) -> log
            .info(ctx.operator + " is operating " + ctx.entityId + " from:" + from + " to:" + to + " on:" + event);
    }
}
