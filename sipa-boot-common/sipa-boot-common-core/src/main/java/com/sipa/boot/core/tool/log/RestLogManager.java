package com.sipa.boot.core.tool.log;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 * @author caszhou
 * @date 2023/5/22
 */
public class RestLogManager {
    private static final String START_LOG_TEMPLATE = "请求开始 - method:[{}] uri:[{}] url:[{}] header:[{}]";

    private static final String START_LOG_FAIL_TEMPLATE = "Failed to print request info, uri:[{}]";

    private static final String FINISH_LOG_TEMPLATE = "请求结束 - method:[{}] uri:[{}] url:[{}] duration:[{}]s";

    private static final String DIGEST_LOG_TEMPLATE =
        "请求摘要 host:[{}] clientIp:[{}] referer:[{}] method:[{}] uri:[{}] url:[{}] duration:[{}]s";

    private static final String FINISH_LOG__FAIL_TEMPLATE = "Failed to print response info, uri:[{}]";

    public static void logStart(Logger log, String method, String uri, String url, String header) {
        log.info(START_LOG_TEMPLATE, StringUtils.trimToEmpty(method), StringUtils.trimToEmpty(uri),
            StringUtils.trimToEmpty(url), StringUtils.trimToEmpty(header));
    }

    public static void logStartFail(Logger log, String uri, Exception e) {
        log.error(START_LOG_FAIL_TEMPLATE, StringUtils.trimToEmpty(uri), e);
    }

    public static void logFinishFail(Logger log, String uri, Exception e) {
        log.error(FINISH_LOG__FAIL_TEMPLATE, StringUtils.trimToEmpty(uri), e);
    }

    public static void logDigest(Logger log, double duration, String host, String clientIp, String referer,
        String method, String uri, String url) {
        log.info(DIGEST_LOG_TEMPLATE, StringUtils.trimToEmpty(host), StringUtils.trimToEmpty(clientIp),
            StringUtils.trimToEmpty(referer), StringUtils.trimToEmpty(method), StringUtils.trimToEmpty(uri),
            StringUtils.trimToEmpty(url), duration);
    }

    public static void logFinish(Logger log, double duration, String method, String uri, String url) {
        log.info(FINISH_LOG_TEMPLATE, StringUtils.trimToEmpty(method), StringUtils.trimToEmpty(uri),
            StringUtils.trimToEmpty(url), duration);
    }
}
