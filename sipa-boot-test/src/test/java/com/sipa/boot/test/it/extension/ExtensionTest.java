package com.sipa.boot.test.it.extension;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sipa.boot.extension.BizScenario;
import com.sipa.boot.extension.ExtensionExecutor;
import com.sipa.boot.test.SipaTestApplication;
import com.sipa.boot.test.it.extension.materials.SomeExtPt;

/**
 * @author caszhou
 * @date 2019/4/24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SipaTestApplication.class)
public class ExtensionTest {
    @Resource
    private ExtensionExecutor executor;

    @Test
    @Ignore
    public void test() {
        this.executor.executeVoid(SomeExtPt.class, BizScenario.valueOf("A"), SomeExtPt::doSomeThing);
        this.executor.executeVoid(SomeExtPt.class, BizScenario.valueOf("B"), SomeExtPt::doSomeThing);
    }
}
