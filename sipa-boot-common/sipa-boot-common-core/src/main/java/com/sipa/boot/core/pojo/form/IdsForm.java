package com.sipa.boot.core.pojo.form;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author caszhou
 * @since 2021-07-23
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdsForm {
    @NotEmpty
    @Schema(description = "主键ID列表")
    private List<Long> ids;
}
