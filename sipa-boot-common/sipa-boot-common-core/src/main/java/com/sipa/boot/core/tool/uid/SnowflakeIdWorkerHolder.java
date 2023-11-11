package com.sipa.boot.core.tool.uid;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.sipa.boot.core.tool.metadata.IServiceMetadata;

/**
 * @author caszhou
 * @date 2019-05-13
 */
public class SnowflakeIdWorkerHolder {
    private static volatile SnowflakeIdWorker instance = null;

    private SnowflakeIdWorkerHolder() {
        // 私有化构造方法，防止外部创建对象
    }

    public static SnowflakeIdWorker getInstance() {
        if (instance == null) {
            synchronized (SnowflakeIdWorker.class) {
                if (instance == null) {
                    instance = new SnowflakeIdWorker(getWorkId());
                }
            }
        }
        return instance;
    }

    private static long getWorkId() {
        ServiceLoader<IServiceMetadata> loader = ServiceLoader.load(IServiceMetadata.class);
        Iterator<IServiceMetadata> iterator = loader.iterator();
        if (iterator.hasNext()) {
            return iterator.next().getWorkId();
        } else {
            return IServiceMetadata.DEFAULT.getWorkId();
        }
    }
}
