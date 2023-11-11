package com.sipa.boot.core.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * @author caszhou
 * @date 2023/4/19
 */
@Getter
@Setter
public class BaseForm extends PageForm {
    @Schema(example = "排序：默认降序：desc 升序：asc", hidden = true)
    private String sort;
}
