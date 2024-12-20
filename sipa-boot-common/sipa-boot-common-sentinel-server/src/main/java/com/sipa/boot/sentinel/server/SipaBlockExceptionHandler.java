package com.sipa.boot.sentinel.server;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.response.ResponseWrapper;
import com.sipa.boot.core.util.SipaJsonUtil;

/**
 * @author caszhou
 * @date 2024/12/20
 */
public class SipaBlockExceptionHandler implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        response.setStatus(429);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        ESystemErrorCode errorCode = ESystemErrorCode.TOO_MANY_REQUEST;
        out.print(SipaJsonUtil.writeValueAsString(ResponseWrapper.error(errorCode.getAllCode(), errorCode.getMsg())));
        out.flush();
        out.close();
    }
}
