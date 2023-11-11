package com.sipa.boot.statemachine;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public interface Condition<C> {
    /**
     * @param context
     *            context object
     * @return whether the context satisfied current condition
     */
    boolean isSatisfied(C context);

    default String name() {
        return this.getClass().getSimpleName();
    }
}
