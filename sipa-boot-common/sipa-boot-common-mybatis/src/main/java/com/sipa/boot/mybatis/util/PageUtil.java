package com.sipa.boot.mybatis.util;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sipa.boot.core.pojo.form.PageForm;

import ma.glasnost.orika.MapperFacade;

/**
 * @author xiajiezhou
 * @date 2022/4/9
 */
@Component
public class PageUtil {
    public static MapperFacade mapperFacade;

    public PageUtil(MapperFacade mapperFacade) {
        PageUtil.mapperFacade = mapperFacade;
    }

    public static <T> Page<T> getPage(PageForm form) {
        return new Page<>(form.getPageNum(), form.getPageSize());
    }

    public static <T> Page<T> getPage(Page<?> page, Class<T> clazz) {
        return new Page<T>(page.getCurrent(), page.getSize(), page.getTotal())
            .setRecords(mapperFacade.mapAsList(page.getRecords(), clazz));
    }

    public static <T> Page<T> getPage(Page<?> page, Class<T> clazz, Consumer<? super T> consumer) {
        return new Page<T>(page.getCurrent(), page.getSize(), page.getTotal()).setRecords(
            mapperFacade.mapAsList(page.getRecords(), clazz).stream().peek(consumer).collect(Collectors.toList()));
    }
}
