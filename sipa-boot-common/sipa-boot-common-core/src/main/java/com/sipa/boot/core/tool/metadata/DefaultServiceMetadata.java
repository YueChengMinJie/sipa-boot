package com.sipa.boot.core.tool.metadata;

import com.sipa.boot.core.tool.uid.SnowflakeIdWorker;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/4/23
 */
@Slf4j
public class DefaultServiceMetadata implements IServiceMetadata {
    private static final int CAN_USE_LIST_START_IDX = 0;

    @Override
    public String getAppName() {
        return null;
    }

    @Override
    public String getIp() {
        return null;
    }

    @Override
    public String getPort() {
        return null;
    }

    @Override
    public int getWorkId() {
        int workId = RandomUtil.randomInt(CAN_USE_LIST_START_IDX, (int)SnowflakeIdWorker.MAX_WORKER);
        log.info("workId [{}]", workId);
        return workId;
    }
}
