package com.sipa.boot.core.pojo.vo.geetest;

import lombok.Builder;
import lombok.Data;

/**
 * @author caszhou
 * @date 2023/6/16
 */
@Data
@Builder
public class GeetestClientRegisterVo {
    private int success;

    private boolean newCaptcha;

    private String challenge;

    private String gt;
}
