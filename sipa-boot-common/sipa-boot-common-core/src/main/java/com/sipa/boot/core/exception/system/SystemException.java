package com.sipa.boot.core.exception.system;

import com.sipa.boot.core.exception.api.IErrorCode;
import com.sipa.boot.core.exception.BaseException;
import com.sipa.boot.core.exception.EProjectModule;
import com.sipa.boot.core.exception.ErrorInfo;
import com.sipa.boot.core.exception.api.IProjectModule;

/**
 * @author caszhou
 * @date 2023/4/24
 */
public class SystemException extends BaseException {
    SystemException(String message) {
        super(message);
    }

    SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    SystemException(Throwable cause) {
        super(cause);
    }

    SystemException(ErrorInfo errorInfo) {
        super(errorInfo);
    }

    SystemException(IErrorCode errorCode) {
        super(errorCode);
    }

    SystemException(IErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    SystemException(IErrorCode errorCode, Object... args) {
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
