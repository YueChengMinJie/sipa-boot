package com.sipa.boot.core.pojo.po;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Getter;
import lombok.Setter;

/**
 * @author caszhou
 * @date 2023/4/23
 */
@Getter
@Setter
public class BasePo {
    @TableId
    private Long id;

    private Integer status;

    @TableField(updateStrategy = FieldStrategy.NEVER, fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(updateStrategy = FieldStrategy.NEVER, fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(updateStrategy = FieldStrategy.NEVER, fill = FieldFill.INSERT)
    private String createName;

    @TableField(updateStrategy = FieldStrategy.NEVER, fill = FieldFill.INSERT)
    private Long createCompanyId;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.UPDATE)
    private Long updateBy;

    @TableField(fill = FieldFill.UPDATE)
    private String updateName;

    @TableLogic
    @TableField("is_deleted")
    private Boolean deleted;

    @Version
    private Integer version;
}
