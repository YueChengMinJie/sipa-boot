package com.sipa.boot.core.exception;

import com.sipa.boot.core.exception.api.IErrorCode;
import com.sipa.boot.core.exception.api.IProjectModule;

/**
 * @author caszhou
 * @date 2023/4/24
 */
public abstract class BaseException extends Exception implements IErrorCodeException {
    final ErrorInfo errorInfo;

    protected BaseException(String message) {
        super(message);
        this.errorInfo = ErrorInfo.parse(message, getDefaultErrorCode());
    }

    protected BaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorInfo = ErrorInfo.parse(message, getDefaultErrorCode());
    }

    protected BaseException(Throwable cause) {
        super(cause);
        this.errorInfo = ErrorInfo.parse(cause.getMessage(), getDefaultErrorCode());
    }

    protected BaseException(ErrorInfo errorInfo) {
        super(errorInfo.toString());
        this.errorInfo = errorInfo;
    }

    protected BaseException(IErrorCode errorCode) {
        this(ErrorInfo.parse(errorCode));
        IProjectModule.check(projectModule(), errorCode.projectModule());
    }

    protected BaseException(IErrorCode errorCode, Throwable cause) {
        super(ErrorInfo.parse(errorCode).toString(), cause);
        this.errorInfo = ErrorInfo.parse(errorCode);
        IProjectModule.check(projectModule(), errorCode.projectModule());
    }

    protected BaseException(IErrorCode errorCode, Object... args) {
        this(ErrorInfo.parse(errorCode, args));
        IProjectModule.check(projectModule(), errorCode.projectModule());
    }

    @Override
    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }
}
