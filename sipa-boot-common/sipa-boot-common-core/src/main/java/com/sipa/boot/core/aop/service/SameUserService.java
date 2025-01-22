package com.sipa.boot.core.aop.service;

/**
 * @author 甘华根
 * @since 2020/7/31 15:01
 */
public interface SameUserService {
    /**
     * 是否为同一用户在当前模块.
     * 
     * @param module
     *            模块名
     * @param moduleId
     *            模块id
     * @return 是否是同一用户
     */
    boolean checkAuth(String module, String moduleId);
}
