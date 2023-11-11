package com.sipa.boot.statemachine.builder;

import com.sipa.boot.statemachine.builder.base.FailCallback;

/**
 * Default fail callback, do nothing.
 *
 * @author caszhou
 * @date 2019/3/24
 */
public class NumbFailCallback<S, E, C> implements FailCallback<S, E, C> {
    @Override
    public void onFail(S sourceState, E event, C context) {
        // do nothing
    }
}
