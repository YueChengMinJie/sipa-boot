package com.sipa.boot.storage.platform;

import java.util.List;

import com.sipa.boot.storage.FileEventForm;

/**
 * @author guozhipeng
 * @date 2023/6/27 17:49 Version: 1.0
 */
public interface OssFileEventService {
    void consume(List<FileEventForm> events);
}
