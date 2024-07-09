package com.sipa.boot.core.exception.support;

import java.util.Objects;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.sipa.boot.core.exception.api.IErrorCode;

/**
 * @author caszhou
 * @date 2023/4/24
 */
@Component
public class ExceptionSpringSupport implements CommandLineRunner {
    // todo by caszhou 业务配置应该与中台框架解耦
    private static final String[] PACKAGES = {"com.sipa", "com.ycmj"};

    @Override
    public void run(String... args) throws Exception {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(IErrorCode.class));
        for (String pkg : PACKAGES) {
            for (BeanDefinition beanDefinition : scanner.findCandidateComponents(pkg)) {
                Class<?> clazz = ClassUtils.forName(Objects.requireNonNull(beanDefinition.getBeanClassName()),
                    Thread.currentThread().getContextClassLoader());
                if (clazz.isEnum()) {
                    IErrorCode[] enumConstants = (IErrorCode[])clazz.getEnumConstants();
                    for (IErrorCode enumConstant : enumConstants) {
                        enumConstant.register();
                    }
                }
            }
        }
    }
}
