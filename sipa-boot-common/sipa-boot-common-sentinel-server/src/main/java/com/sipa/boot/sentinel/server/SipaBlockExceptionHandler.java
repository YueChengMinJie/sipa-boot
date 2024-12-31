package com.sipa.boot.sentinel.server;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
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
        AbstractRule rule = e.getRule();
        ESystemErrorCode errorCode;
        if (rule instanceof FlowRule || rule instanceof ParamFlowRule || rule instanceof SystemRule) {
            errorCode = ESystemErrorCode.TOO_MANY_REQUEST;
        } else if (rule instanceof DegradeRule) {
            errorCode = ESystemErrorCode.DEGRADE;
        } else if (rule instanceof AuthorityRule) {
            errorCode = ESystemErrorCode.AUTHORITY;
        } else {
            errorCode = ESystemErrorCode.DEFAULT_ERROR;
        }
        out.print(SipaJsonUtil.writeValueAsString(ResponseWrapper.error(errorCode.getAllCode(), errorCode.getMsg())));
        out.flush();
        out.close();
    }
}
