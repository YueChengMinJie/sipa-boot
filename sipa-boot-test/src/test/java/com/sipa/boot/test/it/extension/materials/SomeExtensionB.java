package com.sipa.boot.test.it.extension.materials;

import com.sipa.boot.extension.Extension;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2019/4/24
 */
@Slf4j
@Extension(bizId = "B")
@Component
public class SomeExtensionB implements SomeExtPt {
    @Override
    public void doSomeThing() {
        log.info("SomeExtensionB::doSomething");
    }
}
