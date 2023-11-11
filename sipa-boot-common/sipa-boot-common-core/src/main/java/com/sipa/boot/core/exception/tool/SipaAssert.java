package com.sipa.boot.core.exception.tool;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.sipa.boot.core.exception.BaseRuntimeException;
import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;

/**
 * @author caszhou
 * @date 2023/4/24
 */
public class SipaAssert {
    public static void isTrueThenThrow(boolean expression, BaseRuntimeException exception) {
        if (expression) {
            throw exception;
        }
    }

    public static void isTrueThenThrow(boolean expression) {
        isTrueThenThrow(expression, SystemExceptionFactory.bizException(ESystemErrorCode.ASSERT));
    }

    public static void notNull(Object object, BaseRuntimeException exception) {
        if (Objects.isNull(object)) {
            throw exception;
        }
    }

    public static void notNull(Object object) {
        notNull(object, SystemExceptionFactory.bizException(ESystemErrorCode.ASSERT));
    }

    public static void isNull(Object object, BaseRuntimeException exception) {
        if (Objects.nonNull(object)) {
            throw exception;
        }
    }

    public static void isNull(Object object) {
        isNull(object, SystemExceptionFactory.bizException(ESystemErrorCode.ASSERT));
    }

    public static void notEmpty(Collection<?> collection, BaseRuntimeException exception) {
        if (CollectionUtils.isEmpty(collection)) {
            throw exception;
        }
    }

    public static void notEmpty(Collection<?> collection) {
        notEmpty(collection, SystemExceptionFactory.bizException(ESystemErrorCode.ASSERT));
    }

    public static void isEmpty(Collection<?> collection, BaseRuntimeException exception) {
        if (CollectionUtils.isNotEmpty(collection)) {
            throw exception;
        }
    }

    public static void isEmpty(Collection<?> collection) {
        notEmpty(collection, SystemExceptionFactory.bizException(ESystemErrorCode.ASSERT));
    }

    public static void notEmpty(Map<?, ?> map, BaseRuntimeException exception) {
        if (MapUtils.isEmpty(map)) {
            throw exception;
        }
    }

    public static void notEmpty(Map<?, ?> map) {
        notEmpty(map, SystemExceptionFactory.bizException(ESystemErrorCode.ASSERT));
    }

    public static void isEmpty(Map<?, ?> map, BaseRuntimeException exception) {
        if (MapUtils.isNotEmpty(map)) {
            throw exception;
        }
    }

    public static void isEmpty(Map<?, ?> map) {
        notEmpty(map, SystemExceptionFactory.bizException(ESystemErrorCode.ASSERT));
    }
}
