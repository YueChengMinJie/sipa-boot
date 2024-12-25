package com.sipa.boot.feign.fallback;

import java.util.Objects;

import org.springframework.cloud.openfeign.FallbackFactory;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * @author caszhou
 * @date 2024/12/25
 */
public abstract class BaseFallbackFactory<T> implements FallbackFactory<T> {
    private volatile T t = null;

    @Override
    public T create(Throwable cause) {
        if (cause instanceof BlockException) {
            if (Objects.isNull(t)) {
                synchronized (BaseFallbackFactory.class) {
                    if (Objects.isNull(t)) {
                        t = this.doCreate(cause);
                    }
                }
            }
            return t;
        }
        throw new RuntimeException(cause);
    }

    public abstract T doCreate(Throwable cause);
}
