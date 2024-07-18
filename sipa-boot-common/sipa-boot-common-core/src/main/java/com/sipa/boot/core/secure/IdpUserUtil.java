package com.sipa.boot.core.secure;

import java.util.List;
import java.util.Optional;

/**
 * @author caszhou
 * @date 2019-05-08
 */
public class IdpUserUtil {
    /**
     * 用户账户id
     */
    public static Long getId() {
        return getNoneNullIdpUser().getId();
    }

    private static IdpUser getNoneNullIdpUser() {
        return Optional.ofNullable(IdpUserUtil.get()).orElse(IdpUser.empty());
    }

    /**
     * 用户账户姓名
     */
    public static String getName() {
        return getNoneNullIdpUser().getName();
    }

    /**
     * 用户账户手机号
     */
    public static String getPhoneNo() {
        return getNoneNullIdpUser().getPhoneNo();
    }

    /**
     * 主体id
     */
    public static Long getCompanyId() {
        return getNoneNullIdpUser().getCompanyId();
    }

    /**
     * 主体名
     */
    public static String getCompanyName() {
        return getNoneNullIdpUser().getCompanyName();
    }

    /**
     * 应用id
     */
    public static Long getApplicationId() {
        return getNoneNullIdpUser().getApplicationId();
    }

    /**
     * 角色列表
     */
    public static List<String> getRoles() {
        return getNoneNullIdpUser().getRoles();
    }

    /**
     * 权限列表
     */
    public static List<IdpAuth> getAuths() {
        return getNoneNullIdpUser().getAuths();
    }

    /**
     * IdpUser
     */
    public static IdpUser get() {
        return IdpUserHolder.get();
    }

    /**
     * IdpUser
     */
    public static void set(IdpUser user) {
        IdpUserHolder.set(user);
    }

    /**
     * IdpUser
     */
    public static void remove() {
        IdpUserHolder.remove();
    }
}
