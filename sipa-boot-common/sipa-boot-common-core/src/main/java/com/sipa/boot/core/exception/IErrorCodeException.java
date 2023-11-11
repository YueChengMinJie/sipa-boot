package com.sipa.boot.core.exception;

import com.sipa.boot.core.exception.api.IErrorCode;
import com.sipa.boot.core.exception.api.IProjectModule;

/**
 * @author caszhou
 * @date 2023/4/24
 */
public interface IErrorCodeException {
    /**
     * 错误信息，获取异常的错误信息
     */
    ErrorInfo getErrorInfo();

    /**
     * 错误信息，获取异常的错误信息
     */
    IErrorCode getDefaultErrorCode();

    /**
     * 模块信息，获取异常属于哪个模块的
     */
    IProjectModule projectModule();
}
