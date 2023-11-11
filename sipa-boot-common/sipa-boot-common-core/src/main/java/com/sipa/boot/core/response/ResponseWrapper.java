package com.sipa.boot.core.response;

import static com.sipa.boot.core.constant.SipaConstant.GLOBAL_MSG;

import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.core.exception.system.ESystemErrorCode;

import lombok.Data;

/**
 * @author caszhou
 * @date 2016/4/13
 */
@Data
public class ResponseWrapper<T> {
    private static final int SUCCESS = 0;

    private static final int ERROR = -1;

    // 老
    private int code;

    // 新
    private String errorCode;

    private String msg;

    private T data;

    public static <T> ResponseWrapper<T> success(T t) {
        ResponseWrapper<T> rw = new ResponseWrapper<>();
        rw.setCode(SUCCESS);
        rw.setErrorCode(ESystemErrorCode.SUCCESS.getAllCode());
        rw.setData(t);
        return rw;
    }

    public static <T> ResponseWrapper<T> error() {
        ResponseWrapper<T> rw = new ResponseWrapper<>();
        rw.setCode(ERROR);
        rw.setErrorCode(ESystemErrorCode.DEFAULT_ERROR.getAllCode());
        rw.setMsg(GLOBAL_MSG);
        return rw;
    }

    public static <T> ResponseWrapper<T> error(String errorCode, String msg) {
        ResponseWrapper<T> rw = new ResponseWrapper<>();
        rw.setCode(ERROR);
        rw.setErrorCode(errorCode);
        rw.setMsg(msg);
        return rw;
    }

    public static <T> ResponseWrapper<T> error(String msg) {
        ResponseWrapper<T> rw = new ResponseWrapper<>();
        rw.setCode(ERROR);
        rw.setErrorCode(ESystemErrorCode.DEFAULT_ERROR.getAllCode());
        rw.setMsg(StringUtils.isBlank(msg) ? GLOBAL_MSG : msg);
        return rw;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
