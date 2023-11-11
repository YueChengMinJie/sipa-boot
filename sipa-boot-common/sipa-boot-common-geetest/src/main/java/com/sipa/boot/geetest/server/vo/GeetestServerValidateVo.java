package com.sipa.boot.geetest.server.vo;

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
public class GeetestServerValidateVo {
    private String seccode;
}
