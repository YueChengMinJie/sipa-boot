package com.sipa.boot.core.exception;

import com.sipa.boot.core.exception.api.IErrorCode;
import com.sipa.boot.core.exception.api.IProjectModule;

import lombok.Getter;

/**
 * @author caszhou
 * @date 2023/4/24
 */
@Getter
public abstract class BaseRuntimeException extends RuntimeException implements IErrorCodeException {
    final ErrorInfo errorInfo;

    protected BaseRuntimeException(String message) {
        super(message);
        this.errorInfo = ErrorInfo.parse(message, getDefaultErrorCode());
    }

    protected BaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
        this.errorInfo = ErrorInfo.parse(message, getDefaultErrorCode());
    }

    protected BaseRuntimeException(Throwable cause) {
        super(cause);
        this.errorInfo = ErrorInfo.parse(cause.getMessage(), getDefaultErrorCode());
    }

    protected BaseRuntimeException(ErrorInfo errorInfo) {
        super(errorInfo.toString());
        this.errorInfo = errorInfo;
    }

    protected BaseRuntimeException(IErrorCode errorCode) {
        this(ErrorInfo.parse(errorCode));
        IProjectModule.check(projectModule(), errorCode.projectModule());
    }

    protected BaseRuntimeException(IErrorCode errorCode, Throwable cause) {
        super(ErrorInfo.parse(errorCode).toString(), cause);
        this.errorInfo = ErrorInfo.parse(errorCode);
        IProjectModule.check(projectModule(), errorCode.projectModule());
    }

    protected BaseRuntimeException(IErrorCode errorCode, Object... args) {
        this(ErrorInfo.parse(errorCode, args));
        IProjectModule.check(projectModule(), errorCode.projectModule());
    }

    @Override
    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }
}
