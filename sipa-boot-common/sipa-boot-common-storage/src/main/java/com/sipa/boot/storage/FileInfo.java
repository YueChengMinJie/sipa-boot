package com.sipa.boot.storage;

import java.io.Serializable;
import java.time.LocalDateTime;

import cn.hutool.core.lang.Dict;
import lombok.Data;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Data
public class FileInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文件id
     */
    private Long id;

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 主体id
     */
    private Long companyId;

    /**
     * 文件访问地址
     */
    private String url;

    /**
     * 存储桶
     */
    private String bucket;

    /**
     * 基础存储路径
     */
    private String basePath;

    /**
     * key名
     */
    private String key;

    /**
     * 文件大小，单位字节
     */
    private Long size;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalFileName;

    /**
     * 存储路径
     */
    private String path;

    /**
     * 文件扩展名
     */
    private String ext;

    /**
     * MIME 类型
     */
    private String contentType;

    /**
     * 存储平台
     */
    private String platform;

    /**
     * 缩略图访问路径
     */
    private String thUrl;

    /**
     * 缩略图名称
     */
    private String thFileName;

    /**
     * 缩略图大小，单位字节
     */
    private Long thSize;

    /**
     * 缩略图 MIME 类型
     */
    private String thContentType;

    /**
     * 文件所属对象id
     */
    private String objectId;

    /**
     * 文件所属对象类型，例如用户头像，评价图片
     */
    private String objectType;

    /**
     * 附加属性字典
     */
    private Dict attr;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime uploadTime;
}
