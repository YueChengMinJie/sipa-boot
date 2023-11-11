package com.sipa.boot.statemachine.builder;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.statemachine.builder.base.FailCallback;

/**
 * Alert fail callback, throw an {@code TransitionFailException}
 *
 * @author caszhou
 * @date 2019/3/24
 */
public class AlertFailCallback<S, E, C> implements FailCallback<S, E, C> {
    @Override
    public void onFail(S sourceState, E event, C context) {
        throw SystemExceptionFactory.bizException(ESystemErrorCode.FAIL_CALLBACK, event.toString(),
            sourceState.toString(), context.toString());
    }
}
