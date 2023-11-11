package com.sipa.boot.geetest.server.form;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
public class GeetestServerValidateForm {
    private String seccode;

    private String challenge;

    private String jsonFormat;

    private String sdk;

    private String captchaId;

    public boolean validateFail(String validate) {
        return !StringUtils.isNotBlank(challenge) || !StringUtils.isNotBlank(validate)
            || !StringUtils.isNotBlank(seccode);
    }

    public MultiValueMap<String, String> toMap() {
        MultiValueMap<String, String> rs = new LinkedMultiValueMap<>();
        rs.add("seccode", getSeccode());
        rs.add("challenge", getChallenge());
        rs.add("json_format", getJsonFormat());
        rs.add("sdk", getSdk());
        rs.add("captchaid", getCaptchaId());
        return rs;
    }
}
