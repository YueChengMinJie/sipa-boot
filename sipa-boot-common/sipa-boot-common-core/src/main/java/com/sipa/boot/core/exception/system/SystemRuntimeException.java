package com.sipa.boot.core.exception.system;

import com.sipa.boot.core.exception.BaseRuntimeException;
import com.sipa.boot.core.exception.EProjectModule;
import com.sipa.boot.core.exception.ErrorInfo;
import com.sipa.boot.core.exception.api.IErrorCode;
import com.sipa.boot.core.exception.api.IProjectModule;

/**
 * @author caszhou
 * @date 2023/4/24
 */
public class SystemRuntimeException extends BaseRuntimeException {
    SystemRuntimeException(String message) {
        super(message);
    }

    SystemRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    SystemRuntimeException(Throwable cause) {
        super(cause);
    }

    SystemRuntimeException(ErrorInfo errorInfo) {
        super(errorInfo);
    }

    SystemRuntimeException(IErrorCode errorCode) {
        super(errorCode);
    }

    SystemRuntimeException(IErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    SystemRuntimeException(IErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    @Override
    public IErrorCode getDefaultErrorCode() {
        return ESystemErrorCode.DEFAULT_ERROR;
    }

    @Override
    public IProjectModule projectModule() {
        return EProjectModule.SYSTEM;
    }
}
