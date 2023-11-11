package com.sipa.boot.test.it.test;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sipa.boot.test.SipaTestApplication;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

/**
 * @author caszhou
 * @date 2023/9/7
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SipaTestApplication.class)
public class GrobotilsTest {
    @Test
    @SneakyThrows
    public void testGrobotils() {
        TestRunnable runner = new TestRunnable() {
            @Override
            public void runTest() {
                System.out.println(Thread.currentThread().getName());
            }
        };
        // 线程数
        int runnerCount = 500;
        TestRunnable[] trs = new TestRunnable[runnerCount];
        Arrays.fill(trs, runner);
        MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
        // 执行方法
        mttr.runTestRunnables();
    }
}
