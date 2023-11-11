package com.sipa.boot.extension;

import java.util.Objects;

import javax.annotation.Resource;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * @author caszhou
 * @date 2019/4/24
 */
public class ExtensionRegister {
    @Resource
    private ExtensionRepository extensionRepository;

    public final static String EXTENSION_EXT_PT_NAMING = "ExtPt";

    public void doRegistration(ExtensionPoint extensionObject) {
        Class<?> extensionClz = extensionObject.getClass();
        if (AopUtils.isAopProxy(extensionObject)) {
            extensionClz = ClassUtils.getUserClass(extensionObject);
        }
        Extension extensionAnn = AnnotationUtils.findAnnotation(extensionClz, Extension.class);
        BizScenario bizScenario = BizScenario.valueOf(Objects.requireNonNull(extensionAnn).bizId(),
            extensionAnn.useCase(), extensionAnn.scenario());
        ExtensionCoordinate<?> extensionCoordinate =
            new ExtensionCoordinate<>(calculateExtensionPoint(extensionClz), bizScenario.getUniqueIdentity());
        ExtensionPoint preVal = extensionRepository.getExtensionRepo().put(extensionCoordinate, extensionObject);
        if (preVal != null) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.EXTENSION_DEFINE_DUPLICATE,
                extensionCoordinate.toString());
        }
    }

    public void doRegistrationExtensions(ExtensionPoint extensionObject) {
        Class<?> extensionClz = extensionObject.getClass();
        if (AopUtils.isAopProxy(extensionObject)) {
            extensionClz = ClassUtils.getUserClass(extensionObject);
        }
        Extensions extensionsAnnotation = AnnotationUtils.findAnnotation(extensionClz, Extensions.class);
        Extension[] extensions = Objects.requireNonNull(extensionsAnnotation).value();
        if (!ObjectUtils.isEmpty(extensions)) {
            for (Extension extensionAnn : extensions) {
                BizScenario bizScenario =
                    BizScenario.valueOf(extensionAnn.bizId(), extensionAnn.useCase(), extensionAnn.scenario());
                ExtensionCoordinate<?> extensionCoordinate =
                    new ExtensionCoordinate<>(calculateExtensionPoint(extensionClz), bizScenario.getUniqueIdentity());
                ExtensionPoint preVal =
                    extensionRepository.getExtensionRepo().put(extensionCoordinate, extensionObject);
                if (preVal != null) {
                    throw SystemExceptionFactory.bizException(ESystemErrorCode.EXTENSION_DEFINE_DUPLICATE,
                        extensionCoordinate.toString());
                }
            }
        }
        String[] bizIds = extensionsAnnotation.bizId();
        String[] useCases = extensionsAnnotation.useCase();
        String[] scenarios = extensionsAnnotation.scenario();
        for (String bizId : bizIds) {
            for (String useCase : useCases) {
                for (String scenario : scenarios) {
                    BizScenario bizScenario = BizScenario.valueOf(bizId, useCase, scenario);
                    ExtensionCoordinate<?> extensionCoordinate = new ExtensionCoordinate<>(
                        calculateExtensionPoint(extensionClz), bizScenario.getUniqueIdentity());
                    ExtensionPoint preVal =
                        extensionRepository.getExtensionRepo().put(extensionCoordinate, extensionObject);
                    if (preVal != null) {
                        throw SystemExceptionFactory.bizException(ESystemErrorCode.EXTENSION_DEFINE_DUPLICATE,
                            extensionCoordinate.toString());
                    }
                }
            }
        }
    }

    private String calculateExtensionPoint(Class<?> targetClz) {
        Class<?>[] interfaces = ClassUtils.getAllInterfacesForClass(targetClz);
        if (interfaces.length == 0) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.EXTENSION_ILLEGAL, targetClz.toString());
        }
        for (Class<?> inter : interfaces) {
            String extensionPoint = inter.getSimpleName();
            if (extensionPoint.contains(EXTENSION_EXT_PT_NAMING)) {
                return inter.getName();
            }
        }
        throw SystemExceptionFactory.bizException(ESystemErrorCode.EXTENSION_INTERFACE_NAME_ILLEGAL,
            targetClz.toString(), EXTENSION_EXT_PT_NAMING);
    }

    public void doRegistrationForArk(BizScenario bizScenario, Class<?> interfaceType, ExtensionPoint proxy) {
        ExtensionCoordinate<?> extensionCoordinate =
            new ExtensionCoordinate<>(interfaceType.getName(), bizScenario.getUniqueIdentity());
        ExtensionPoint preVal = extensionRepository.getExtensionRepo().put(extensionCoordinate, proxy);
        if (preVal != null) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.EXTENSION_DEFINE_DUPLICATE,
                extensionCoordinate.toString());
        }
    }
}
