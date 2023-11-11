package com.sipa.boot.core.exception;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sipa.boot.core.exception.api.IErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author caszhou
 * @date 2023/4/24
 */
@ToString
@AllArgsConstructor
public class ErrorInfo {
    static final Map<String, ErrorInfo> ERROR_MSG_CODES_MAP = new ConcurrentHashMap<>();

    static final Map<String, ErrorInfo> NO_PARAM_CODES_MAP = new ConcurrentHashMap<>();

    /**
     * 错误码
     */
    @Getter
    private final String code;

    /**
     * 返回错误信息
     */
    @Getter
    private final String msg;

    public static ErrorInfo parse(String message, IErrorCode errorCode) {
        return ERROR_MSG_CODES_MAP.computeIfAbsent(message, it -> new ErrorInfo(errorCode.getAllCode(), message));
    }

    public static ErrorInfo parse(IErrorCode errorCode) {
        return NO_PARAM_CODES_MAP.computeIfAbsent(errorCode.getAllCode(), it -> new ErrorInfo(it, errorCode.getMsg()));
    }

    public static ErrorInfo parse(IErrorCode errorCode, Object... args) {
        return new ErrorInfo(errorCode.getAllCode(), MessageFormat.format(errorCode.getMsg(), args));
    }
}
