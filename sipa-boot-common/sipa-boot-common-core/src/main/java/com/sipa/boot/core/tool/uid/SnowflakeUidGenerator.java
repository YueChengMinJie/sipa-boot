package com.sipa.boot.core.tool.uid;

import org.springframework.stereotype.Component;

/**
 * spring容器中使用
 * 
 * @author caszhou
 * @date 2019-05-13
 */
@Component
public class SnowflakeUidGenerator implements IUidGenerator {
    @Override
    public Long nextLid() {
        return SnowflakeIdWorkerHolder.getInstance().nextId();
    }

    @Override
    public String nextSid() {
        return String.valueOf(nextLid());
    }
}
