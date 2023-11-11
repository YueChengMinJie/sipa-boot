package com.sipa.boot.core.exception.api;

import java.util.Objects;

import com.google.common.base.Preconditions;
import com.sipa.boot.core.exception.EProjectModule;

/**
 * @author caszhou
 * @date 2023/4/24
 */
public interface IProjectModule {
    static void check(IProjectModule required, IProjectModule input) {
        Preconditions.checkNotNull(required);
        if (Objects.nonNull(input) && input != EProjectModule.SYSTEM) {
            Preconditions.checkState(required == input,
                "module not match, need: " + required.getProjectName() + "-" + required.getModuleName() + "("
                    + required.getProjectCode() + "-" + required.getModuleCode() + ")" + " but input: "
                    + input.getProjectName() + "-" + input.getModuleName() + "(" + input.getProjectCode() + "-"
                    + input.getModuleCode() + ")");
        }
    }

    /**
     * 项目编码
     */
    String getProjectCode();

    /**
     * 模块编码
     */
    String getModuleCode();

    /**
     * 项目名称
     */
    String getProjectName();

    /**
     * 模块名称
     */
    String getModuleName();
}
