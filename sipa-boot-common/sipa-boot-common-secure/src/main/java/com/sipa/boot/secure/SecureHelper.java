package com.sipa.boot.secure;

import java.util.Objects;

import com.sipa.boot.core.secure.IdpUser;
import org.apache.commons.lang3.StringUtils;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;

/**
 * @author caszhou
 * @date 2023/5/11
 */
public class SecureHelper {
    public static IdpUser getCurrentIdpUser() {
        return Convert.convert(new TypeReference<>() {}, StpUtil.getExtra(IdpUser.EXTRA_KEY));
    }

    public static Long getApplicationId() {
        return getCurrentIdpUser().getApplicationId();
    }

    public static Long getCompanyId() {
        return getCurrentIdpUser().getCompanyId();
    }

    public static IdpUser parse(String idpUserHeader) {
        return StringUtils.isBlank(idpUserHeader) ? null
            : JSONUtil.toBean(Base64.decodeStr(idpUserHeader), IdpUser.class);
    }

    public static String convert(IdpUser idpUser) {
        return Objects.nonNull(idpUser) ? Base64.encode(JSONUtil.toJsonStr(idpUser)) : null;
    }
}
