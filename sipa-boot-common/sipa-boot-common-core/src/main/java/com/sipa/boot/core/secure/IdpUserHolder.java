package com.sipa.boot.core.secure;

import java.util.Objects;

import org.slf4j.MDC;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.util.SipaUtil;

/**
 * @author caszhou
 * @date 2019-05-08
 */
public class IdpUserHolder {
    private static final ThreadLocal<IdpUser> IDP_USER_HOLDER = new TransmittableThreadLocal<>();

    public static IdpUser get() {
        return IDP_USER_HOLDER.get();
    }

    public static void set(IdpUser idpUser) {
        if (Objects.nonNull(idpUser)) {
            IDP_USER_HOLDER.set(idpUser);

            MDC.put(SipaConstant.COMPANY_ID_HEADER, SipaUtil.stringValueOf(idpUser.getCompanyId()));
            MDC.put(SipaConstant.USER_ID_HEADER, SipaUtil.stringValueOf(idpUser.getId()));
        }
    }

    public static void remove() {
        IDP_USER_HOLDER.remove();

        MDC.remove(SipaConstant.COMPANY_ID_HEADER);
        MDC.remove(SipaConstant.USER_ID_HEADER);
    }
}
