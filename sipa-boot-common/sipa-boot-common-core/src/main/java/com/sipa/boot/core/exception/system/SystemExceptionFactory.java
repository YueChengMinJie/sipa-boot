package com.sipa.boot.core.exception.system;

import com.sipa.boot.core.exception.api.IErrorCode;

/**
 * @author caszhou
 * @date 2023/2/14
 */
public class SystemExceptionFactory {
    private SystemExceptionFactory() {
        //
    }

    public static SystemRuntimeException bizException(String errorMessage) {
        return new SystemRuntimeException(errorMessage);
    }

    public static SystemRuntimeException bizException(IErrorCode errorCode) {
        return new SystemRuntimeException(errorCode);
    }

    public static SystemRuntimeException bizException(IErrorCode errorCode, Object... args) {
        return new SystemRuntimeException(errorCode, args);
    }

    public static SystemRuntimeException bizException(String errorMessage, Throwable e) {
        return new SystemRuntimeException(errorMessage, e);
    }

    public static SystemRuntimeException bizException(IErrorCode errorCode, Throwable e) {
        return new SystemRuntimeException(errorCode, e);
    }

    public static SystemException sysException(String errorMessage) {
        return new SystemException(errorMessage);
    }

    public static SystemException sysException(IErrorCode errorCode) {
        return new SystemException(errorCode);
    }

    public static SystemRuntimeException sysException(IErrorCode errorCode, Object... args) {
        return new SystemRuntimeException(errorCode, args);
    }

    public static SystemException sysException(String errorMessage, Throwable e) {
        return new SystemException(errorMessage, e);
    }

    public static SystemException sysException(IErrorCode errorCode, Throwable e) {
        return new SystemException(errorCode, e);
    }
}
