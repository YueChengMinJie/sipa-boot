package com.sipa.boot.core.pojo.form;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author caszhou
 * @since 2021-07-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class IdForm {
    @NotNull(message = "ID不能为空")
    @Schema(description = "主键ID")
    private Long id;
}
