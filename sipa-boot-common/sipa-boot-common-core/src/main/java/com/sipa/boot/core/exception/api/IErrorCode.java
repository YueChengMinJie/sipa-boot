package com.sipa.boot.core.exception.api;

import com.sipa.boot.core.exception.manager.ErrorManager;

/**
 * @author caszhou
 * @date 2023/4/24
 */
public interface IErrorCode {
    /**
     * 最细粒度code,不包含project、module信息
     */
    String getCode();

    /**
     * 异常信息 英文
     */
    String getMsg();

    /**
     * 系统模块
     * 
     * @return 模块
     */
    IProjectModule getProjectModule();

    /**
     * 拼接project、module、node后的完整的错误码
     */
    default String getAllCode() {
        return ErrorManager.getAllCode(this);
    }

    /**
     * 校验使用
     * 
     * @return 系统模块
     */
    default IProjectModule projectModule() {
        return ErrorManager.projectModule(this);
    }

    /**
     * spring启动时候扫描的时候注册调用
     */
    default void register() {
        ErrorManager.register(getProjectModule(), this);
    }
}
