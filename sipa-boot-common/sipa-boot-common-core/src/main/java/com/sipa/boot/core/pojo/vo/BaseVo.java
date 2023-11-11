package com.sipa.boot.core.pojo.vo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * @author caszhou
 * @date 2023/4/23
 */
@Getter
@Setter
public class BaseVo {
    @Schema(example = "id")
    private Long id;

    @Schema(example = "状态")
    private Integer status;

    @Schema(example = "创建时间")
    private LocalDateTime createTime;

    @Schema(example = "创建人id")
    private String createBy;

    @Schema(example = "创建人name")
    private String createName;

    @Schema(example = "主体id")
    private String createCompanyId;

    @Schema(example = "更新时间")
    private LocalDateTime updateTime;

    @Schema(example = "更新人id")
    private String updateBy;

    @Schema(example = "更新人名称")
    private String updateName;

    @Schema(example = "逻辑删除")
    private Boolean deleted;

    @Schema(example = "乐观锁")
    private Integer version;
}
