package com.sipa.boot.extension;

import org.springframework.context.ApplicationContextAware;

/**
 * @author caszhou
 * @date 2023/6/6
 */
public interface ExtensionBootstrap extends ApplicationContextAware {
    void init();
}
