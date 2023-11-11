package com.sipa.boot.test.ut.statemachine;

import com.sipa.boot.statemachine.Action;
import com.sipa.boot.statemachine.Condition;
import com.sipa.boot.statemachine.StateMachine;
import com.sipa.boot.statemachine.builder.StateMachineBuilderFactory;
import com.sipa.boot.statemachine.builder.base.StateMachineBuilder;
import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2019/3/24
 */
@Slf4j
public class StateMachineChoiceTest {
    static class Context {
        private final String condition;

        public Context(String condition) {
            this.condition = condition;
        }

        public String getCondition() {
            return condition;
        }
    }

    /**
     * 测试选择分支，针对同一个事件：EVENT1 if condition == "1", STATE1 --> STATE1 if condition == "2" , STATE1 --> STATE2 if condition
     * == "3" , STATE1 --> STATE3
     */
    @Test
    public void testChoice() {
        StateMachineBuilder<StateMachineTest.State, StateMachineTest.Event, Context> builder =
            StateMachineBuilderFactory.create();
        builder.internalTransition()
            .within(StateMachineTest.State.STATE1)
            .on(StateMachineTest.Event.EVENT1)
            .when(checkCondition1())
            .perform(doAction());
        builder.externalTransition()
            .from(StateMachineTest.State.STATE1)
            .to(StateMachineTest.State.STATE2)
            .on(StateMachineTest.Event.EVENT1)
            .when(checkCondition2())
            .perform(doAction());
        builder.externalTransition()
            .from(StateMachineTest.State.STATE1)
            .to(StateMachineTest.State.STATE3)
            .on(StateMachineTest.Event.EVENT1)
            .when(checkCondition3())
            .perform(doAction());

        StateMachine<StateMachineTest.State, StateMachineTest.Event, Context> stateMachine =
            builder.build("ChoiceConditionMachine");
        StateMachineTest.State target1 =
            stateMachine.fireEvent(StateMachineTest.State.STATE1, StateMachineTest.Event.EVENT1, new Context("1"));
        Assert.assertEquals(StateMachineTest.State.STATE1, target1);
        StateMachineTest.State target2 =
            stateMachine.fireEvent(StateMachineTest.State.STATE1, StateMachineTest.Event.EVENT1, new Context("2"));
        Assert.assertEquals(StateMachineTest.State.STATE2, target2);
        StateMachineTest.State target3 =
            stateMachine.fireEvent(StateMachineTest.State.STATE1, StateMachineTest.Event.EVENT1, new Context("3"));
        Assert.assertEquals(StateMachineTest.State.STATE3, target3);
    }

    private Condition<Context> checkCondition1() {
        return (ctx) -> "1".equals(ctx.getCondition());
    }

    private Condition<Context> checkCondition2() {
        return (ctx) -> "2".equals(ctx.getCondition());
    }

    private Condition<Context> checkCondition3() {
        return (ctx) -> "3".equals(ctx.getCondition());
    }

    private Action<StateMachineTest.State, StateMachineTest.Event, Context> doAction() {
        return (from, to, event, ctx) -> log
            .info("from:" + from + " to:" + to + " on:" + event + " condition:" + ctx.getCondition());
    }
}
