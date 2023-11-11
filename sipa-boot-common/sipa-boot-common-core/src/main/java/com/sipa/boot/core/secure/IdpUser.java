package com.sipa.boot.core.secure;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author caszhou
 * @date 2019-05-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdpUser {
    public static final String EXTRA_KEY = "user";

    public static final String REST_WEB_KEY = "X-Idp-User";

    private Long id;

    private String name;

    private String phoneNo;

    private Long companyId;

    private String companyName;

    /**
     * 外部用户:1 , 内部用户:0
     */
    private Integer accountType = AccountType.DEFAULT_LOGIN_TYPE;

    /**
     * 应用id, 也是设备id
     */
    private Long applicationId;

    /**
     * 拓展信息对象 此对象保证不为空 但是不一定有内容
     */
    private ExtendInfo extendInfo = new ExtendInfo();

    private List<String> roles;

    private List<IdpAuth> auths;

    public static IdpUser empty() {
        return new IdpUser();
    }

    public static interface AccountType {
        /**
         * 内部用户(员工)
         */
        Integer INTERNAL = 0;

        /**
         * 外部用户(消费者)
         */
        Integer EXTERNAL = 1;

        Integer DEFAULT_LOGIN_TYPE = INTERNAL;
    }

    @Data
    public static class ExtendInfo {
        /**
         * 外部用户通过微信登录才有
         */
        private String openId;

        /**
         * member center的member表id 外部用户才可能有 未授权手机号的话是没有的
         */
        private Long memberId;
    }
}
