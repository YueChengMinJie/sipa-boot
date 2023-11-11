package com.sipa.boot.test.es;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author caszhou
 * @date 2023/9/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Logan {
    private long id;

    private long taskId;

    private Integer logType;

    private String content;

    private long logTime;

    private String addTime;

    private String updateTime;
}
