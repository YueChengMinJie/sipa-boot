package com.sipa.boot.geetest.service;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.core.pojo.vo.geetest.GeetestClientRegisterVo;
import com.sipa.boot.core.util.SipaUtil;
import com.sipa.boot.geetest.constant.GeetestConstant;
import com.sipa.boot.geetest.property.GeetestProperty;
import com.sipa.boot.geetest.server.form.GeetestServerRegisterForm;
import com.sipa.boot.geetest.server.form.GeetestServerValidateForm;
import com.sipa.boot.geetest.server.vo.GeetestServerValidateVo;
import com.sipa.boot.rest.util.RestUtil;

import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeetestService {
    private final GeetestProperty geetestProperty;

    /**
     * bypass降级模式，检测到极验云状态异常，走本地初始化
     */
    public GeetestClientRegisterVo localInit() {
        log.info("localInit(): 开始本地初始化, 后续流程走宕机模式.");
        GeetestClientRegisterVo geetestClientRegisterVo = this.buildRegisterResult(null);
        log.info("localInit(): 本地初始化, lib包返回信息={}.", JSONUtil.toJsonStr(geetestClientRegisterVo));
        return geetestClientRegisterVo;
    }

    /**
     * 构建验证初始化接口返回数据
     */
    private GeetestClientRegisterVo buildRegisterResult(String originChallenge) {
        GeetestClientRegisterVo vo;
        // origin_challenge为空或者值为0代表失败
        if (StringUtils.isBlank(originChallenge) || SipaConstant.StringValue.STRING_VALUE_0.equals(originChallenge)) {
            vo = GeetestClientRegisterVo.builder()
                .success(GeetestConstant.FAIL)
                .gt(this.geetestProperty.getGeetestId())
                .challenge(UUID.randomUUID().toString())
                .newCaptcha(GeetestConstant.NEW_CAPTCHA)
                .build();
        } else {
            vo = GeetestClientRegisterVo.builder()
                .success(GeetestConstant.SUCCESS)
                .gt(this.geetestProperty.getGeetestId())
                .challenge(DigestUtil.md5Hex(originChallenge + this.geetestProperty.getGeetestKey()))
                .newCaptcha(GeetestConstant.NEW_CAPTCHA)
                .build();
        }
        return vo;
    }

    /**
     * 验证初始化
     */
    public GeetestClientRegisterVo register() {
        String originChallenge = this.requestRegister();
        GeetestClientRegisterVo geetestClientRegisterVo = this.buildRegisterResult(originChallenge);
        log.info("register(): 验证初始化, lib包返回信息={}.", JSONUtil.toJsonStr(geetestClientRegisterVo));
        return geetestClientRegisterVo;
    }

    /**
     * 向极验发送验证初始化的请求，GET方式
     */
    private String requestRegister() {
        GeetestServerRegisterForm form = GeetestServerRegisterForm.builder()
            .digestmod(GeetestConstant.DIGESTMOD)
            .gt(this.geetestProperty.getGeetestId())
            .jsonFormat(GeetestConstant.JSON_FORMAT)
            .sdk(GeetestConstant.SDK)
            .build();
        String registerUrl = this.geetestProperty.getApiUrl() + this.geetestProperty.getRegisterUrl();
        log.info("requestRegister(): 验证初始化, 向极验发送请求, url={}, params={}.", registerUrl, JSONUtil.toJsonStr(form));
        String originChallenge = null;
        try {
            Map<String, Object> rs = RestUtil.get(registerUrl, form.toMap());
            log.info("requestRegister(): 验证初始化, 与极验网络交互正常, 返回body={}.", JSONUtil.toJsonStr(rs));
            originChallenge = SipaUtil.stringValueOf(rs.get("challenge"));
        } catch (Exception e) {
            log.error("requestRegister(): 验证初始化, 请求异常，后续流程走宕机模式", e);
        }
        return originChallenge;
    }

    /**
     * 正常流程下（即验证初始化成功），二次验证
     */
    public boolean successValidate(GeetestServerValidateForm form, String validate) {
        log.info("successValidate(): 开始二次验证 正常模式, data=%{}.", JSONUtil.toJsonStr(form));
        if (Objects.isNull(form) || form.validateFail(validate)) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.GEETEST_VALIDATION_FAILED);
        } else {
            String responseSeccode = this.requestValidate(form);
            if (StringUtils.isBlank(responseSeccode)) {
                throw SystemExceptionFactory.bizException(ESystemErrorCode.GEETEST_VALIDATION_FAILED);
            } else if (GeetestConstant.VALIDATE_FAILED.equals(responseSeccode)) {
                throw SystemExceptionFactory.bizException(ESystemErrorCode.GEETEST_VALIDATION_FAILED);
            }
        }
        return true;
    }

    /**
     * 向极验发送二次验证的请求，POST方式
     */
    private String requestValidate(GeetestServerValidateForm form) {
        form.setJsonFormat(GeetestConstant.JSON_FORMAT);
        form.setSdk(GeetestConstant.SDK);
        form.setCaptchaId(this.geetestProperty.getGeetestId());

        String validateUrl = this.geetestProperty.getApiUrl() + this.geetestProperty.getValidateUrl();
        log.info("requestValidate(): 二次验证 正常模式, 向极验发送请求, url={}, params={}.", validateUrl, JSONUtil.toJsonStr(form));
        String responseSeccode = null;
        try {
            GeetestServerValidateVo vo =
                RestUtil.postForm(validateUrl, null, form.toMap(), GeetestServerValidateVo.class);
            log.info("requestValidate(): 二次验证 正常模式, 与极验网络交互正常, 返回body={}.", JSONUtil.toJsonStr(vo));
            responseSeccode = SipaUtil.stringValueOf(vo.getSeccode());
        } catch (Exception e) {
            log.info("requestValidate(): 二次验证 正常模式, 请求异常", e);
        }
        return responseSeccode;
    }

    /**
     * 异常流程下（即验证初始化失败，宕机模式），二次验证 注意：由于是宕机模式，初衷是保证验证业务不会中断正常业务，所以此处只作简单的参数校验，可自行设计逻辑。
     */
    public boolean failValidate(GeetestServerValidateForm form, String validate) {
        log.info("failValidate(): 开始二次验证 宕机模式, data={}.", JSONUtil.toJsonStr(form));
        if (Objects.isNull(form) || form.validateFail(validate)) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.GEETEST_VALIDATION_FAILED);
        }
        return true;
    }
}
