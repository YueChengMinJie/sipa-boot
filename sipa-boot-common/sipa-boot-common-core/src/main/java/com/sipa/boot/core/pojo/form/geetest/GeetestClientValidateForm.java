package com.sipa.boot.core.pojo.form.geetest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author caszhou
 * @date 2023/6/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeetestClientValidateForm {
    @Schema(description = "流水号，一次完整验证流程的唯一标识")
    private String challenge;

    @Schema(description = "待校验的核心数据，客户端向极验请求的http://api.geetest.com/ajax.php 接口返回得到")
    private String validate;

    @Schema(description = "待校验的核心数据，geetest_validate加上|jordan组成的字符串")
    private String seccode;
}
