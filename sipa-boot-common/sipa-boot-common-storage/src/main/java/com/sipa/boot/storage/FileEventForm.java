package com.sipa.boot.storage;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author guozhipeng
 * @date 2023/6/28 13:50 Version: 1.0
 */
@Data
public class FileEventForm {
    /**
     * 事件类型 不同的oss名字可能也不一样
     */
    private String eventName;

    /**
     * 事件产生的时间
     */
    private LocalDateTime eventTime;

    /**
     * oss桶名称
     */
    private String bucketName;

    /**
     * Object的名称 (OSS文件相对路径)
     */
    private String objectKey;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 构成文件要素 暂不包含文件md5
     *
     * @return 构成文件要素
     */
    public String getFileKey() {
        return this.bucketName + this.eventName + this.objectKey;
    }
}
