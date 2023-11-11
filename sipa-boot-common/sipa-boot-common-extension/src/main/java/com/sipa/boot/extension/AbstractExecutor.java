package com.sipa.boot.extension;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fulan.zjf
 * @date 2017/12/21
 */
@Slf4j
public abstract class AbstractExecutor {
    /**
     * Execute extension with Response
     */
    public <I extends ExtensionPoint, R> R execute(Class<I> targetClz, BizScenario bizScenario,
        Function<I, R> exeFunction) {
        I component = locateComponent(targetClz, bizScenario, false);
        try {
            return exeFunction.apply(component);
        } catch (Exception e) {
            log.error("Execute extension fail", e);
            if (isNotDefault(component)) {
                component = locateComponent(targetClz, bizScenario, true);
                if (Objects.nonNull(component)) {
                    log.info("Execute extension fallback to default.");
                    return exeFunction.apply(component);
                }
            }
            return null;
        }
    }

    public <I extends ExtensionPoint, R> R execute(ExtensionCoordinate<I> extensionCoordinate,
        Function<I, R> exeFunction) {
        return execute(extensionCoordinate.getExtensionPointClass(), extensionCoordinate.getBizScenario(), exeFunction);
    }

    /**
     * Execute extension without Response
     */
    public <I extends ExtensionPoint> void executeVoid(Class<I> targetClz, BizScenario bizScenario,
        Consumer<I> exeFunction) {
        I component = locateComponent(targetClz, bizScenario, false);
        try {
            exeFunction.accept(component);
        } catch (Exception e) {
            log.error("Execute extension fail", e);
            if (isNotDefault(component)) {
                component = locateComponent(targetClz, bizScenario, true);
                if (Objects.nonNull(component)) {
                    log.info("Execute extension fallback to default.");
                    exeFunction.accept(component);
                }
            }
        }
    }

    private static <I extends ExtensionPoint> boolean isNotDefault(I component) {
        try {
            return !component.isDefault();
        } catch (Exception e) {
            return true;
        }
    }

    public <I extends ExtensionPoint> void executeVoid(ExtensionCoordinate<I> extensionCoordinate,
        Consumer<I> exeFunction) {
        executeVoid(extensionCoordinate.getExtensionPointClass(), extensionCoordinate.getBizScenario(), exeFunction);
    }

    /**
     * locate component, if find default, will be null.
     */
    protected abstract <T extends ExtensionPoint> T locateComponent(Class<T> targetClz, BizScenario bizScenario,
        boolean findDefault);
}
