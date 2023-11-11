package com.sipa.boot.ark.property;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.sipa.boot.core.constant.SipaBootConstant;

import lombok.Data;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Data
@ConfigurationProperties(prefix = SipaBootConstant.ArkHost.PREFIX)
public class ArkHostProperty {
    /**
     * 扩展点列表
     */
    private List<ExtensionProperty> extensions;

    @Data
    public static class ExtensionProperty {
        /**
         * 业务
         */
        private String bizId;

        /**
         * 案例
         */
        private String useCase;

        /**
         * 场景
         */
        private String scenario;

        /**
         * 接口类型
         */
        private Class<Object> interfaceType;

        /**
         * 接口实现唯一id
         */
        private String uniqueId;
    }
}
