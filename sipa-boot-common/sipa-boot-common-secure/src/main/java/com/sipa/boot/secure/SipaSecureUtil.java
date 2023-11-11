package com.sipa.boot.secure;

import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.response.ResponseWrapper;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.hutool.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/8/7
 */
@Slf4j
public class SipaSecureUtil {
    public static int getStatusCode(Throwable e) {
        if (e instanceof NotPermissionException || e instanceof NotRoleException) {
            return HttpStatus.HTTP_FORBIDDEN;
        } else {
            return HttpStatus.HTTP_UNAUTHORIZED;
        }
    }

    public static ResponseWrapper<Object> handlerException(Throwable e) {
        // 不同异常返回不同状态码
        ResponseWrapper<Object> re;
        if (e instanceof NotLoginException) {
            NotLoginException ee = (NotLoginException)e;
            String type = ee.getType();
            int code = ee.getCode();
            log.info("未登陆: {} {}", type, code);

            ESystemErrorCode sec;
            if (NotLoginException.NOT_TOKEN.equals(type)) {
                sec = ESystemErrorCode.NOT_TOKEN;
            } else if (NotLoginException.INVALID_TOKEN.equals(type)) {
                sec = ESystemErrorCode.INVALID_TOKEN;
            } else if (NotLoginException.TOKEN_TIMEOUT.equals(type)) {
                sec = ESystemErrorCode.TOKEN_TIMEOUT;
            } else if (NotLoginException.BE_REPLACED.equals(type)) {
                sec = ESystemErrorCode.BE_REPLACED;
            } else if (NotLoginException.KICK_OUT.equals(type)) {
                sec = ESystemErrorCode.KICK_OUT;
            } else {
                sec = ESystemErrorCode.NOT_LOGGED_IN;
            }
            re = ResponseWrapper.error(sec.getAllCode(), sec.getMsg());
        } else if (e instanceof NotRoleException) {
            NotRoleException ee = (NotRoleException)e;
            log.info("无此角色: " + ee.getRole());

            ESystemErrorCode sec = ESystemErrorCode.ABNORMAL_CHARACTER;
            re = ResponseWrapper.error(sec.getAllCode(), sec.getMsg());
        } else if (e instanceof NotPermissionException) {
            NotPermissionException ee = (NotPermissionException)e;
            log.info("无此权限: " + ee.getPermission());

            ESystemErrorCode sec = ESystemErrorCode.PERMISSION_EXCEPTION;
            re = ResponseWrapper.error(sec.getAllCode(), sec.getMsg());
        } else {
            log.error(StringUtils.EMPTY, e);

            ESystemErrorCode sec = ESystemErrorCode.AUTHENTICATION_ERROR;
            re = ResponseWrapper.error(sec.getAllCode(), e.getMessage());
        }
        return re;
    }
}
