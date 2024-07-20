package com.sipa.boot.secure.server.payload;

import java.lang.reflect.Field;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.sipa.boot.core.payload.InjectUserInfo;
import com.sipa.boot.core.secure.IdpUserUtil;

/**
 * @author caszhou
 * @date 2024/7/20
 */
@Aspect
public class UserInfoInjectorAspect {
    @Before("@annotation(com.sipa.boot.core.allinone.SipaRequest.PostMapping)")
    public void beforePost(JoinPoint joinPoint) throws IllegalAccessException {
        doBefore(joinPoint);
    }

    private void doBefore(JoinPoint joinPoint) throws IllegalAccessException {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg != null && arg.getClass().isAnnotationPresent(InjectUserInfo.class)) {
                injectUserInfo(arg);
            }
        }
    }

    @Before("@annotation(com.sipa.boot.core.allinone.SipaRequest.PutMapping)")
    public void beforePut(JoinPoint joinPoint) throws IllegalAccessException {
        doBefore(joinPoint);
    }

    private void injectUserInfo(Object requestBody) throws IllegalAccessException {
        // 检查请求体类是否被 @InjectUserInfo 注解标记
        if (requestBody.getClass().isAnnotationPresent(InjectUserInfo.class)) {
            try {
                Field accountId = requestBody.getClass().getDeclaredField("accountId");
                accountId.setAccessible(true);
                accountId.set(requestBody, IdpUserUtil.getId());
            } catch (NoSuchFieldException ignored) {
            }

            try {
                Field companyId = requestBody.getClass().getDeclaredField("companyId");
                companyId.setAccessible(true);
                companyId.set(requestBody, IdpUserUtil.getCompanyId());
            } catch (NoSuchFieldException ignored) {
            }

            try {
                Field applicationId = requestBody.getClass().getDeclaredField("applicationId");
                applicationId.setAccessible(true);
                applicationId.set(requestBody, IdpUserUtil.getApplicationId());
            } catch (NoSuchFieldException ignored) {
            }
        }
    }
}
