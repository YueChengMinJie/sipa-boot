package com.sipa.boot.core.exception;

import com.sipa.boot.core.exception.api.IProjectModule;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author caszhou
 * @date 2023/4/24
 */
@Getter
@AllArgsConstructor
public enum EProjectModule implements IProjectModule {
    /**
     * 系统
     */
    SYSTEM("00", "00", "default", "default"),

    /**
     * 物联网中台
     */
    IOT("02", "01", "业务中台", "物联网中台"),

    /**
     * 物联网中台-边缘网关
     */
    IOT_GATEWAY("02", "02", "业务中台", "物联网中台-边缘网关"),

    /**
     * 物联网中台-站控
     */
    IOT_STATION("02", "03", "业务中台", "物联网中台-站控"),

    /**
     * 用户中台
     */
    SSO_IDP("02", "03", "业务中台", "用户中台"),

    /**
     * 基础中台
     */
    BASE("02", "04", "业务中台", "基础中台"),

    /**
     * 管理后台
     */
    AMS("03", "01", "后台", "大后台"),

    /**
     * 示例业务 示例实例
     */
    BIZ_USERCASE("ZZ", "ZZ", "示例业务", "示例实例"),

    ;

    private final String projectCode;

    private final String moduleCode;

    private final String projectName;

    private final String moduleName;
}
