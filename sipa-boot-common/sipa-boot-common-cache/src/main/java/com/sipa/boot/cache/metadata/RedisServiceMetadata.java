package com.sipa.boot.cache.metadata;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.cache.util.RedisUtil;
import com.sipa.boot.core.tool.metadata.IServiceMetadata;
import com.sipa.boot.core.tool.uid.SnowflakeIdWorker;
import com.sipa.boot.core.util.SipaUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/4/23
 */
@Slf4j
public class RedisServiceMetadata implements IServiceMetadata {
    private static final String SPRING_PORT_KEY = "server.port";

    private static final String KEY_PREFIX = "sipa_boot:service_metadata:";

    private static final int CAN_USE_LIST_START_IDX = 0;

    private static final int EXPIRE_DAY = 2;

    private static final int BACKGROUND_THREAD = 1;

    public RedisServiceMetadata() {
        this.runRefreshTask();
    }

    private void runRefreshTask() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date time = calendar.getTime();
        if (time.before(new Date())) {
            time = new Date(time.getTime() + 24 * 60 * 60 * 1000);
        }
        long initialDelay = time.getTime() - System.currentTimeMillis();
        long period = 24 * 60 * 60 * 1000;
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(BACKGROUND_THREAD);
        executorService.scheduleAtFixedRate(new RefreshTask(), initialDelay, period, TimeUnit.MILLISECONDS);
    }

    @Override
    public String getAppName() {
        return SpringUtil.getApplicationName();
    }

    @Override
    public String getIp() {
        return SipaUtil.isLocal() ? SipaUtil.getLocalIntranetIp() : SipaUtil.getHostInContainer();
    }

    @Override
    public String getPort() {
        return SpringUtil.getProperty(SPRING_PORT_KEY);
    }

    @Override
    public int getWorkId() {
        int workId = 0;
        String key = this.getKey();
        String current = RedisUtil.get(key);
        if (StringUtils.isNotBlank(current)) {
            workId = Integer.parseInt(current);
        } else {
            List<String> keys = RedisUtil.getKeysByPattern(this.getPatternKey());
            if (CollectionUtils.isNotEmpty(keys)) {
                List<Integer> dbList = RedisUtil.getValuesByKeys(keys)
                    .stream()
                    .map(obj -> Integer.valueOf(String.valueOf(obj)))
                    .collect(Collectors.toList());
                List<Integer> canUseList = IntStream.range(CAN_USE_LIST_START_IDX, (int)SnowflakeIdWorker.MAX_WORKER)
                    .boxed()
                    .collect(Collectors.toList());
                canUseList.removeAll(dbList);
                if (CollectionUtils.isNotEmpty(canUseList)) {
                    workId = CollUtil.getFirst(canUseList);
                } else {
                    throw new RuntimeException("workId用尽，请检查");
                }
            }
        }
        RedisUtil.set(key, SipaUtil.stringValueOf(workId), TimeUnit.DAYS.toSeconds(EXPIRE_DAY));
        log.info("workId = [{}]", workId);
        return workId;
    }

    private String getPatternKey() {
        return KEY_PREFIX + this.getAppName() + "*";
    }

    private String getKey() {
        return KEY_PREFIX + this.getAppName() + ":" + this.getIp() + "_" + this.getPort();
    }

    private class RefreshTask implements Runnable {
        @Override
        public void run() {
            String val = RedisUtil.get(RedisServiceMetadata.this.getKey());
            if (StringUtils.isNotBlank(val)) {
                boolean result =
                    RedisUtil.expire(RedisServiceMetadata.this.getKey(), TimeUnit.DAYS.toSeconds(EXPIRE_DAY));
                log.info("workId [{}] 续期结果 [{}]", val, result);
            }
        }
    }
}
