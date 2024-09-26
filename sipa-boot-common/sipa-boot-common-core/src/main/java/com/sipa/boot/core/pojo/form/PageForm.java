package com.sipa.boot.core.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询分页类
 * 
 * @author caszhou
 * @date 2023/4/19
 */
@Getter
@Setter
public class PageForm {
    /**
     * 当前页
     */
    @Schema(example = "当前页")
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    @Schema(example = "页大小")
    private Integer pageSize = 10;

    /**
     * 获取查询表起始 默认是0
     *
     * @return limit 起始点
     */
    public Integer getStartIndex() {
        if (this.pageNum == null) {
            return 0;
        }
        if (this.pageSize == null) {
            this.pageSize = 10;
        }
        return (this.pageNum - 1) * this.pageSize;
    }

    /**
     * 获取分页大小 默认是 10
     *
     * @return 分页大小
     */
    public Integer getLimitSize() {
        if (this.pageSize == null) {
            return 10;
        }
        return this.pageSize;
    }
}
