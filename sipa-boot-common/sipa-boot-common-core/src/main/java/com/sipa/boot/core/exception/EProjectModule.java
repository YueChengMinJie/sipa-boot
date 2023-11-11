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
     * 业务中台 流程中台
     */
    BCP_PROCESS("01", "01", "业务中台", "流程中台"),

    /**
     * 业务中台 用户中台
     */
    BCP_IDP("01", "02", "业务中台", "用户中台"),

    /**
     * 业务中台 消息中台
     */
    BCP_MESSAGE("01", "03", "业务中台", "消息中台"),

    /**
     * 业务中台 基础中台
     */
    BCP_BASE("01", "04", "业务中台", "基础中台"),

    /**
     * 业务中台 订单中台
     */
    BCP_ORDER("01", "05", "业务中台", "订单中台"),

    /**
     * 业务中台 钱包
     */
    BCP_WALLET("01", "06", "业务中台", "钱包"),

    /**
     * 业务中台 商户中台
     */
    BCP_MERCHANT("01", "07", "业务中台", "商户中台"),

    /**
     * 榆钱荚 云钱包
     */
    ELMPOD_WALLET("02", "01", "榆钱荚", "云钱包"),

    /**
     * 大后台 商家中心
     */
    MOCHA_MERCHANT("03", "01", "大后台", "商家中心"),

    /**
     * ERP 流程
     */
    ERP_PROCESS("04", "01", "ERP", "流程"),

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
