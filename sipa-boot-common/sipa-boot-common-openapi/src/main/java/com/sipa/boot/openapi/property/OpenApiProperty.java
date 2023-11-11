package com.sipa.boot.openapi.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.sipa.boot.core.constant.SipaBootConstant;

import lombok.Data;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Data
@ConfigurationProperties(prefix = SipaBootConstant.OpenApi.PREFIX)
public class OpenApiProperty {
    /**
     * 描述
     */
    private String desc;

    /**
     * 版本
     */
    private String version;
}
