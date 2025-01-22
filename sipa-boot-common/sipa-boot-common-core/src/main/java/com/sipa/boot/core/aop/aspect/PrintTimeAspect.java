package com.sipa.boot.core.aop.aspect;

import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhouxiajie
 */
@Slf4j
@Aspect
@Component
public class PrintTimeAspect {
    @Pointcut("@annotation(com.sipa.boot.core.aop.annotation.PrintTime)")
    public void pointcut() {
        //
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object object = joinPoint.proceed();

        long end = System.currentTimeMillis();

        log.info("class [{}], method [{}], runTime [{}]", getClassName(joinPoint), getMethodName(joinPoint),
            (end - start) / 1000.0);

        return object;
    }

    private String getMethodName(ProceedingJoinPoint joinPoint) {
        return Objects.requireNonNull(joinPoint).getSignature().getName();
    }

    private String getClassName(ProceedingJoinPoint joinPoint) {
        return Objects.requireNonNull(joinPoint).getTarget().getClass().getName();
    }
}
