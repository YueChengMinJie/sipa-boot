package com.sipa.boot.test.it.core;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sipa.boot.core.tool.uid.SnowflakeIdWorker;
import com.sipa.boot.core.tool.uid.UidUtil;
import com.sipa.boot.test.SipaTestApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/4/23
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SipaTestApplication.class)
public class UidTest {
    @Test
    @Ignore
    public void testGenerator() {
        log.info("SEQUENCE_BITS = " + SnowflakeIdWorker.SEQUENCE_BITS);
        log.info("MAX_DATA_CENTER = " + SnowflakeIdWorker.MAX_DATA_CENTER);
        log.info("MAX_WORKER = " + SnowflakeIdWorker.MAX_WORKER);
        log.info("SEQUENCE_MASK = " + SnowflakeIdWorker.SEQUENCE_MASK);
        log.info("PRESSURE_MEASUREMENT_FLAG_SHIFT = " + SnowflakeIdWorker.PRESSURE_MEASUREMENT_FLAG_SHIFT);
        log.info("WORKER_ID_SHIFT = " + SnowflakeIdWorker.WORKER_ID_SHIFT);
        log.info("DATA_CENTER_ID_SHIFT = " + SnowflakeIdWorker.DATA_CENTER_ID_SHIFT);
        log.info("TIMESTAMP_LEFT_SHIFT = " + SnowflakeIdWorker.TIMESTAMP_LEFT_SHIFT);

        Long uid = UidUtil.nextLid();
        log.info("uid: [{}]", uid);
        Assertions.assertThat(uid).isNotNull();
    }

    @Test
    @Ignore
    public void testHash() {
        HashSet<Integer> set = new HashSet<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int count = 0;
        for (int i = 0; i < 100; i++) {
            int r = random.nextInt(127);
            if (set.contains(r)) {
                count++;
            } else {
                set.add(r);
            }
        }
        log.info("碰撞" + count);
    }
}
