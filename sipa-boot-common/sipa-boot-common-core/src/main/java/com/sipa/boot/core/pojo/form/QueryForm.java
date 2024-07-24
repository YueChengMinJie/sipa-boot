package com.sipa.boot.core.pojo.form;

import lombok.Data;

/**
 * @author caszhou
 * @date 2023/4/19
 */
@Data
public class QueryForm {
    private Long createBy;

    private String createName;

    private Long updateBy;

    private String updateName;

    private Long createCompanyId;

    private String createCompanyName;
}
