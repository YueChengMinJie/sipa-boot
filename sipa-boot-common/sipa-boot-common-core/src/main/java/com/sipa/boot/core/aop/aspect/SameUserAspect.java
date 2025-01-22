package com.sipa.boot.core.aop.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sipa.boot.core.aop.annotation.SameUser;
import com.sipa.boot.core.aop.service.SameUserService;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 甘华根
 * @since 2020/7/31 14:09
 */
@Slf4j
@Aspect
@Component
public class SameUserAspect {
    private final SameUserService sameUser;

    public SameUserAspect(@Autowired(required = false) SameUserService sameUser) {
        this.sameUser = sameUser;
    }

    @Pointcut("@annotation(com.sipa.boot.core.aop.annotation.SameUser)")
    public void pointcut() {
        //
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();

        String module = "";
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if (annotation instanceof SameUser) {
                module = ((SameUser)annotation).module();
            }
        }

        Object[] ary = joinPoint.getArgs();

        if (ary.length > 0) {
            Object moduleId = ary[0];

            if (moduleId instanceof String) {
                if (Objects.nonNull(sameUser)) {
                    if (!sameUser.checkAuth(module, (String)moduleId)) {
                        throw SystemExceptionFactory.bizException("不是同一用户");
                    }
                } else {
                    log.info("系统无SameUserService实现类, 无法判断是否是同一用户.");
                }
            } else {
                log.info("moduleId必须为string.");
            }
        } else {
            log.info("无法判断是否是同一用户，无moduleId.");
        }

        return joinPoint.proceed();
    }
}
