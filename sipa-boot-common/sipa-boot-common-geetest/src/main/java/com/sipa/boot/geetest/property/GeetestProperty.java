package com.sipa.boot.geetest.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.sipa.boot.core.constant.TcpCloudConstant;

import lombok.Data;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Data
@ConfigurationProperties(prefix = TcpCloudConstant.Geetest.PREFIX)
public class GeetestProperty {
    /**
     * 极验公钥
     */
    private String geetestId;

    /**
     * 极验私钥
     */
    private String geetestKey;

    /**
     * api地址，默认为http://api.geetest.com
     */
    private String apiUrl = "http://api.geetest.com";

    /**
     * 注册endpoint，默认为/register.php
     */
    private String registerUrl = "/register.php";

    /**
     * 二次验证endpoint，默认为/validate.php
     */
    private String validateUrl = "/validate.php";
}
