package com.sipa.boot.core.thread;

import java.util.Objects;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author caszhou
 * @date 2019-05-08
 */
public class DataHolder<T> {
    private final ThreadLocal<T> HOLDER = new TransmittableThreadLocal<>();

    public T get() {
        return HOLDER.get();
    }

    public void set(T t) {
        if (Objects.nonNull(t)) {
            HOLDER.set(t);
        }
    }

    public void remove() {
        HOLDER.remove();
    }
}
