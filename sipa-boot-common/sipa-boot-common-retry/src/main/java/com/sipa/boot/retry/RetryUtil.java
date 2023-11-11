package com.sipa.boot.retry;

import java.util.HashMap;
import java.util.Map;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * @author guozhipeng
 * @date 2023/8/25 16:07 Version: 1.0
 */
public class RetryUtil {
    private static final RetryTemplate DEFAULT_RETRY_TEMPLATE = new RetryTemplate();

    private static final int DEFAULT_MAX_ATTEMPTS = 3;

    static {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(DEFAULT_MAX_ATTEMPTS);

        RetryUtil.DEFAULT_RETRY_TEMPLATE.setRetryPolicy(retryPolicy);
    }

    /**
     * 使用默认的retry机制 任何异常都会回滚 最多retry2次(最多算上第一次执行一共三次)
     */
    @SuppressWarnings("all")
    public static void defaultExecute(RetryCallback retryCallback) {
        DEFAULT_RETRY_TEMPLATE.execute(retryCallback);
    }

    /**
     * 自定义 对应抛出exceptionClass的exception才执行重试 maxAttempts最大执行次数(不要小于1)
     */
    public static <T, E extends Throwable> T customExecute(RetryCallback<T, E> retryCallback, Class<E> excptionClass,
        int maxAttempts) throws E {
        RetryTemplate retryTemplate = new RetryTemplate();
        // 定义重试策略
        ExceptionClassifierRetryPolicy retryPolicy = new ExceptionClassifierRetryPolicy();
        Map<Class<? extends Throwable>, RetryPolicy> policyMap = new HashMap<>();
        policyMap.put(excptionClass, new SimpleRetryPolicy(maxAttempts));
        retryPolicy.setPolicyMap(policyMap);
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate.execute(retryCallback);
    }

    /**
     * 自定义 对应抛出exceptionClass的exception才执行重试
     */
    public static <T, E extends Throwable> T customExecute(RetryCallback<T, E> retryCallback, Class<E> excptionClass)
        throws E {
        return customExecute(retryCallback, excptionClass, DEFAULT_MAX_ATTEMPTS);
    }
}
