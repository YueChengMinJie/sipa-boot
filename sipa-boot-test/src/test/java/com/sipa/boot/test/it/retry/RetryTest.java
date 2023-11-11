package com.sipa.boot.test.it.retry;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.RetryCallback;
import org.springframework.test.context.junit4.SpringRunner;

import com.sipa.boot.retry.RetryUtil;
import com.sipa.boot.test.SipaTestApplication;

import lombok.SneakyThrows;

/**
 * @author guozhipeng
 * @date 2023/8/25 16:47 Version: 1.0
 */
@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SipaTestApplication.class)
public class RetryTest {
    @Test
    @SneakyThrows
    public void testNotRetry() {
        // exception不一样 不会重试
        RetryCallback<Void, IOException> retryCallback = context -> {
            System.out.println("------------");
            throw new RuntimeException();
        };
        RetryUtil.customExecute(retryCallback, IOException.class, 3);
    }

    @Test
    @SneakyThrows
    public void testRetry() {
        // exception 一样 会重试
        RetryCallback<Void, IOException> retryCallback = context -> {
            System.out.println("------------");
            throw new IOException();
        };
        RetryUtil.customExecute(retryCallback, IOException.class, 3);
    }
}
