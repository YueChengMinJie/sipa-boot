package com.sipa.boot.test.it.lock;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.lock.Lock;
import com.sipa.boot.test.SipaTestApplication;

import cn.hutool.core.thread.ThreadUtil;
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
public class LockTest {
    @Test
    @SneakyThrows
    public void testGrobotils() {
        String lockKey = "lockKey";
        TestRunnable runner = new TestRunnable() {
            @Override
            public void runTest() {
                try (Lock lock = Lock.factory().redisRedissonSolution().newLock(lockKey)) {
                    boolean success = lock.acquire(10, 0.005, 10);
                    log.info("success={}", success);
                    ThreadUtil.safeSleep(TimeUnit.SECONDS.toMillis(SipaConstant.Number.INT_4));
                }
            }
        };
        // 线程数
        int runnerCount = 30;
        TestRunnable[] trs = new TestRunnable[runnerCount];
        Arrays.fill(trs, runner);
        MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
        // 执行方法
        mttr.runTestRunnables();
    }
}
