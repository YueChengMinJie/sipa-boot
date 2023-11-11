package com.sipa.boot.storage.platform;

import java.util.List;

import com.sipa.boot.storage.FileEventForm;

/**
 * @author guozhipeng
 * @date 2023/6/28 14:23 Version: 1.0
 */
public interface OssFileEventConverter {
    List<FileEventForm> convertEvent(String event);
}
