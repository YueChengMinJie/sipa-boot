package com.sipa.boot.extension;

import java.util.Objects;

import javax.annotation.Resource;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2019/4/24
 */
@Slf4j
public class ExtensionExecutor extends AbstractExecutor {
    @Resource
    private ExtensionRepository extensionRepository;

    @Override
    protected <T extends ExtensionPoint> T locateComponent(Class<T> targetClz, BizScenario bizScenario,
        boolean findDefault) {
        T extension = locateExtension(targetClz, bizScenario, findDefault);
        if (log.isDebugEnabled()) {
            log.debug("[Located Extension]: " + extension.getClass().getSimpleName());
        }
        return extension;
    }

    /**
     * if the bizScenarioUniqueIdentity is "ali.tmall.supermarket"
     * <p>
     * the search path is as below: 1、first try to get extension by "ali.tmall.supermarket", if get, return it. 2、loop
     * try to get extension by "ali.tmall", if get, return it. 3、loop try to get extension by "ali", if get, return it.
     * 4、if not found, try the default extension
     */
    protected <T extends ExtensionPoint> T locateExtension(Class<T> targetClz, BizScenario bizScenario,
        boolean findDefault) {
        checkNull(bizScenario);

        if (log.isDebugEnabled()) {
            log.debug("BizScenario in locateExtension is : " + bizScenario.getUniqueIdentity());
        }

        // first try with full namespace
        T extension = firstTry(targetClz, bizScenario);
        if (Objects.nonNull(extension) && (!findDefault || isaDefault(extension))) {
            return extension;
        }
        // second try with default scenario
        extension = secondTry(targetClz, bizScenario);
        if (Objects.nonNull(extension) && (!findDefault || isaDefault(extension))) {
            return extension;
        }
        // third try with default use case + default scenario
        extension = defaultUseCaseTry(targetClz, bizScenario);
        if (Objects.nonNull(extension) && (!findDefault || isaDefault(extension))) {
            return extension;
        }
        if (Objects.isNull(extension) && !findDefault) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.EXTENSION_NOT_FOUND, targetClz.toString(),
                bizScenario.getUniqueIdentity());
        } else {
            return null;
        }
    }

    private static <T extends ExtensionPoint> boolean isaDefault(T extension) {
        try {
            return extension.isDefault();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * first try with full namespace
     * <p>
     * example: biz1.useCase1.scenario1
     */
    private <T extends ExtensionPoint> T firstTry(Class<T> targetClz, BizScenario bizScenario) {
        if (log.isDebugEnabled()) {
            log.debug("First trying with " + bizScenario.getUniqueIdentity());
        }
        return locate(targetClz.getName(), bizScenario.getUniqueIdentity());
    }

    /**
     * second try with default scenario
     * <p>
     * example: biz1.useCase1.#defaultScenario#
     */
    private <T extends ExtensionPoint> T secondTry(Class<T> targetClz, BizScenario bizScenario) {
        if (log.isDebugEnabled()) {
            log.debug("Second trying with " + bizScenario.getIdentityWithDefaultScenario());
        }
        return locate(targetClz.getName(), bizScenario.getIdentityWithDefaultScenario());
    }

    /**
     * third try with default use case + default scenario
     * <p>
     * example: biz1.#defaultUseCase#.#defaultScenario#
     */
    private <T extends ExtensionPoint> T defaultUseCaseTry(Class<T> targetClz, BizScenario bizScenario) {
        if (log.isDebugEnabled()) {
            log.debug("Third trying with " + bizScenario.getIdentityWithDefaultUseCase());
        }
        return locate(targetClz.getName(), bizScenario.getIdentityWithDefaultUseCase());
    }

    @SuppressWarnings("unchecked")
    private <T extends ExtensionPoint> T locate(String name, String uniqueIdentity) {
        return (T)extensionRepository.getExtensionRepo().get(new ExtensionCoordinate<>(name, uniqueIdentity));
    }

    private void checkNull(BizScenario bizScenario) {
        if (bizScenario == null) {
            throw new IllegalArgumentException("BizScenario can not be null for extension");
        }
    }
}
