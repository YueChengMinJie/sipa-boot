package com.sipa.boot.test;

// import org.springframework.context.annotation.AnnotationConfigApplicationContext;
// import org.springframework.context.annotation.ComponentScan;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.PropertySource;
//
// import com.sipa.boot.core.property.YamlPropertySourceFactory;
import com.sipa.boot.testcontainer.TestContainer;

/**
 * @author caszhou
 * @date 2023/5/29
 */
public class SipaTestContainer {
    public static void main(String[] args) {
        // new AnnotationConfigApplicationContext(SpringConfig.class);
        TestContainer.start();
    }

    /**
     * 没有nacos
     */
    // @Configuration
    // @ComponentScan
    // @PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
    // static class SpringConfig {
    //
    // }
}
