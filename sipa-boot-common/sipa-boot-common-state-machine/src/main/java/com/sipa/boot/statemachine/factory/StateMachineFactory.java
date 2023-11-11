package com.sipa.boot.statemachine.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.statemachine.StateMachine;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public class StateMachineFactory {
    /**
     * machineId
     */
    private static final Map<String, StateMachine<?, ?, ?>> STATE_MACHINE_MAP = new ConcurrentHashMap<>();

    public static <S, E, C> void register(StateMachine<S, E, C> stateMachine) {
        String machineId = stateMachine.getMachineId();
        if (STATE_MACHINE_MAP.get(machineId) != null) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.DUPLICATE_MACHINE_ID, machineId);
        }
        STATE_MACHINE_MAP.put(stateMachine.getMachineId(), stateMachine);
    }

    @SuppressWarnings("unchecked")
    public static <S, E, C> StateMachine<S, E, C> get(String machineId) {
        StateMachine<S, E, C> stateMachine = (StateMachine<S, E, C>)STATE_MACHINE_MAP.get(machineId);
        if (stateMachine == null) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.NO_STATE_MACHINE_INSTANCE, machineId);
        }
        return stateMachine;
    }
}
