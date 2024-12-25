package com.sipa.boot.mvc.exception;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.exception.BaseException;
import com.sipa.boot.core.exception.BaseRuntimeException;
import com.sipa.boot.core.exception.ErrorInfo;
import com.sipa.boot.core.exception.IErrorCodeException;
import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.response.ResponseWrapper;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/2/14
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {
    /**
     * 自定义异常处理
     *
     * @param ece
     *            exception
     * @return ResponseWrapper
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BaseException.class, BaseRuntimeException.class})
    public ResponseWrapper<Object> exception(IErrorCodeException ece, HttpServletRequest request) {
        ErrorInfo errorInfo = ece.getErrorInfo();
        Exception e = (Exception)ece;
        log.error("[自定义异常]: [{}] [{}] [{}]", request.getRequestURI(), errorInfo.getCode(), errorInfo.getMsg(), e);
        return ResponseWrapper.error(errorInfo.getCode(), errorInfo.getMsg());
    }

    /**
     * feign异常处理
     *
     * @param e
     *            exception
     * @param request
     *            request
     * @return ResponseWrapper
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FeignException.class)
    public ResponseWrapper<Object> feignException(FeignException e, HttpServletRequest request) {
        log.error("[feign异常]: [{}]", request.getRequestURI(), e);
        if (e.responseBody().isPresent()) {
            return JSONUtil.toBean(new String(e.responseBody().get().array(), StandardCharsets.UTF_8),
                new TypeReference<>() {}, true);
        }
        return ResponseWrapper.error(ESystemErrorCode.INTERFACE_INNER_INVOKE_ERROR.getAllCode(), getMsg(e));
    }

    /**
     * 字段验证异常处理
     *
     * @param request
     *            request
     * @param e
     *            异常信息
     * @return ResponseWrapper
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseWrapper<Object> methodArgumentNotValidException(MethodArgumentNotValidException e,
        HttpServletRequest request) {
        String msg = getMsg(e);
        log.error("[字段验证异常拦截]: [{}] [{}]", request.getRequestURI(), msg, e);
        return ResponseWrapper.error(ESystemErrorCode.ASSERT.getAllCode(), msg);
    }

    private static String getMsg(MethodArgumentNotValidException e) {
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError err : errors) {
            errorMsg.append(err.getDefaultMessage()).append(", ");
        }
        String msg = errorMsg.toString();
        return StringUtils.isBlank(msg) ? msg : msg.substring(0, msg.length() - 2);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseWrapper<?> handleNoHandlerFoundException(HttpServletRequest request, NoHandlerFoundException e) {
        log.error("[Servlet异常]: [{}]", request.getRequestURI(), e);
        ESystemErrorCode notFound = ESystemErrorCode.NOT_FOUND;
        return ResponseWrapper.error(notFound.getAllCode(), notFound.getMsg());
    }

    /**
     * 运行时异常处理
     *
     * @param e
     *            exception
     * @param request
     *            request
     * @return ResponseWrapper
     */
    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseWrapper<Object> runtimeException(RuntimeException e, HttpServletRequest request) {
        log.error("[运行时异常]: [{}]", request.getRequestURI(), e);
        return ResponseWrapper.error(ESystemErrorCode.SERVER_ERROR.getAllCode(), getMsg(e));
    }

    private static String getMsg(Exception e) {
        String msg = null;
        Throwable cause = e.getCause();
        if (Objects.nonNull(cause)) {
            msg = cause.getMessage();
        }
        if (StringUtils.isBlank(msg)) {
            msg = e.getMessage();
        }
        return StringUtils.isBlank(msg) ? SipaConstant.GLOBAL_MSG : msg;
    }

    /**
     * 异常处理
     *
     * @param e
     *            exception
     * @param request
     *            request
     * @return ResponseWrapper
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseWrapper<Object> exception(Exception e, HttpServletRequest request) {
        log.error("[编译时异常]: [{}]", request.getRequestURI(), e);
        return ResponseWrapper.error(ESystemErrorCode.SERVER_ERROR.getAllCode(), getMsg(e));
    }
}
