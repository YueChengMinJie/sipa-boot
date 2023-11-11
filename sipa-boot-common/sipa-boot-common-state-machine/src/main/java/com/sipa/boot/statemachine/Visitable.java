package com.sipa.boot.statemachine;

/**
 * @author caszhou
 * @date 2019/3/24
 */
public interface Visitable {
    String accept(final Visitor visitor);
}
