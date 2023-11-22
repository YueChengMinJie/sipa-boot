package com.sipa.boot.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.alibaba.druid.support.http.StatViewServlet;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.sipa.boot.core.property.YamlPropertySourceFactory;
import com.sipa.boot.mybatis.handler.CommonMetaObjectHandler;

/**
 * @author caszhou
 * @date 2023/2/14
 */
@Configuration
@ComponentScan("com.sipa.boot.mybatis.**")
@ConditionalOnClass({CommonMetaObjectHandler.class})
@PropertySource(value = "classpath:mybatis.yml", factory = YamlPropertySourceFactory.class)
@MapperScan(basePackages = {"com.hm.**.mapper", "com.hmev.**.mapper"}) // todo by caszhou 业务配置应该与框架解耦
public class MybatisAutoConfiguration {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        ServletRegistrationBean<StatViewServlet> servletServletRegistrationBean =
            new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        servletServletRegistrationBean.addInitParameter("resetEnable", "true");
        servletServletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletServletRegistrationBean.addInitParameter("loginPassword", "Aa123456");
        return servletServletRegistrationBean;
    }
}
