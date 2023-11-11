package com.sipa.boot.test.it.extension.materials.customer.app;

import com.sipa.boot.test.it.extension.materials.customer.client.Response;

/**
 * Response with single record to return
 * <p/>
 * 
 * @author caszhou
 * @date 2019/4/24
 */
public class SingleResponse<T> extends Response {
    private static final long serialVersionUID = 1L;

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static SingleResponse buildSuccess() {
        SingleResponse response = new SingleResponse();
        response.setSuccess(true);
        return response;
    }

    public static SingleResponse buildFailure(String errCode, String errMessage) {
        SingleResponse response = new SingleResponse();
        response.setSuccess(false);
        response.setErrCode(errCode);
        response.setErrMessage(errMessage);
        return response;
    }

    public static <T> SingleResponse<T> of(T data) {
        SingleResponse<T> response = new SingleResponse<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }
}
