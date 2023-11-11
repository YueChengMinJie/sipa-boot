package com.sipa.boot.mybatis.handler;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sipa.boot.core.secure.IdpUserUtil;

/**
 * @author caszhou
 * @date 2023/4/23
 */
@Component
public class CommonMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("createBy", IdpUserUtil.getId(), metaObject);
        this.setFieldValByName("createName", IdpUserUtil.getName(), metaObject);
        this.setFieldValByName("createCompanyId", IdpUserUtil.getCompanyId(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateBy", IdpUserUtil.getId(), metaObject);
        this.setFieldValByName("updateName", IdpUserUtil.getName(), metaObject);
    }
}
