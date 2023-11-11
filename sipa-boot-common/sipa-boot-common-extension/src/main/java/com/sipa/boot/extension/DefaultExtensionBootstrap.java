package com.sipa.boot.extension;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * @author caszhou
 * @date 2019/4/24
 */
public class DefaultExtensionBootstrap implements ExtensionBootstrap {
    @Resource
    protected ExtensionRegister extensionRegister;

    protected ApplicationContext applicationContext;

    @Override
    @PostConstruct
    public void init() {
        // handle @Extension annotation
        Map<String, Object> extensionBeans = applicationContext.getBeansWithAnnotation(Extension.class);
        extensionBeans.values().forEach(extension -> extensionRegister.doRegistration((ExtensionPoint)extension));

        // handle @Extensions annotation
        Map<String, Object> extensionsBeans = applicationContext.getBeansWithAnnotation(Extensions.class);
        extensionsBeans.values()
            .forEach(extension -> extensionRegister.doRegistrationExtensions((ExtensionPoint)extension));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
