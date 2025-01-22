package com.sipa.boot.mvc.codec.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.sipa.boot.core.constant.SipaBootConstant;

import lombok.Data;

/**
 * 密钥配置
 *
 * @author caszhou
 * @date 2020/9/18
 */
@Data
@ConfigurationProperties(prefix = SipaBootConstant.Core.SIPA_API_KEY)
public class ApiProperty {
    /**
     * rsa 公钥
     */
    private String rsaPublicKey;

    /**
     * rsa 私钥
     */
    private String rsaPrivateKey;

    /**
     * 前端rsa 公钥
     */
    private String frontRsaPublicKey;
}
