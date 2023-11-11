package com.sipa.boot.geetest.server.form;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * @author caszhou
 * @date 2023/6/16
 */
@Data
@Builder
public class GeetestServerRegisterForm {
    private String digestmod;

    private String gt;

    private String jsonFormat;

    private String sdk;

    public Map<String, String> toMap() {
        Map<String, String> rs = new HashMap<>(16);
        rs.put("digestmod", getDigestmod());
        rs.put("gt", getGt());
        rs.put("json_format", getJsonFormat());
        rs.put("sdk", getSdk());
        return rs;
    }
}
