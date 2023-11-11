package com.sipa.boot.testcontainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.sipa.boot.testcontainer.command.TestClassRunCmd;
import com.sipa.boot.testcontainer.command.TestMethodRunCmd;
import com.sipa.boot.testcontainer.util.BeanMetaUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2019/4/24
 */
@Slf4j
public class TestExecutor {
    private String className;

    private String methodName;

    private final ApplicationContext context;

    public TestExecutor(ApplicationContext context) {
        this.context = context;
    }

    public void execute(TestClassRunCmd cmd) throws Exception {
        setClassName(cmd.getClassName());

        Class<?> testClz = Class.forName(className);
        Object testInstance = getTestInstance(testClz);
        runClassTest(testClz, testInstance);
    }

    public void execute(TestMethodRunCmd cmd) throws Exception {
        setClassName(cmd.getClassName());
        setMethodName(cmd.getMethodName());

        Class<?> testClz = Class.forName(className);
        Object testInstance = getTestInstance(testClz);
        runMethodTest(testClz, testInstance);
    }

    private void runMethodTest(Class<?> testClz, Object testInstance) throws Exception {
        Method beforeMethod = BeanMetaUtil.findMethod(testClz, Before.class);
        Method afterMethod = BeanMetaUtil.findMethod(testClz, After.class);
        Method method = testClz.getMethod(methodName);

        invokeMethod(testInstance, beforeMethod);

        invokeMethod(testInstance, method);

        invokeMethod(testInstance, afterMethod);
    }

    private Object getTestInstance(Class<?> testClz) throws Exception {
        Object testInstance = testClz.getConstructor().newInstance();
        injectWiredBean(testClz, testInstance);
        return testInstance;
    }

    private void runClassTest(Class<?> testClz, Object testInstance) throws Exception {
        Method[] allMethods = testClz.getMethods();
        Method beforeMethod = null;
        Method afterMethod = null;
        List<Method> testMethods = new ArrayList<>();
        for (Method method : allMethods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Before) {
                    beforeMethod = method;
                    break;
                }
                if (annotation instanceof After) {
                    afterMethod = method;
                    break;
                }
                if (annotation instanceof Test || method.getName().startsWith("test")) {
                    testMethods.add(method);
                    break;
                }
            }
        }
        // invoke before method
        invokeMethod(testInstance, beforeMethod);
        // invoke test methods
        for (Method testMethod : testMethods) {
            invokeMethod(testInstance, testMethod);
        }
        // invoke after method
        invokeMethod(testInstance, afterMethod);
    }

    private static void invokeMethod(Object obj, Method method) throws Exception {
        if (method == null) {
            return;
        }
        method.invoke(obj);
    }

    private void injectWiredBean(Class<?> testClz, Object testInstance) {
        Field[] fields = testClz.getDeclaredFields();
        for (Field field : fields) {
            String beanName = field.getName();
            Annotation autowiredAnn = field.getDeclaredAnnotation(Autowired.class);
            Annotation resourceAnn = field.getDeclaredAnnotation(Resource.class);
            if (autowiredAnn == null && resourceAnn == null) {
                continue;
            }
            trySetFieldValue(field, testInstance, beanName);
        }
    }

    private void trySetFieldValue(Field field, Object testInstance, String beanName) {
        try {
            field.setAccessible(true);
            field.set(testInstance, context.getBean(beanName));
            return;
        } catch (IllegalArgumentException e) {
            if (!StringUtils.isEmpty(e.getMessage()) && e.getMessage().indexOf("\\$Proxy") > 0) {
                log.error("此错误一般是实际类被代理导致，请尝试把字段类型改为接口!");
                throw e;
            }
        } catch (BeansException | IllegalAccessException e) {
            log.error("根据beanName查找失败，尝试byType查找");
        }
        try {
            field.set(testInstance, context.getBean(field.getType()));
        } catch (Exception innerE) {
            innerE.printStackTrace();
            log.error("oops!!! " + beanName + " can not be injected to " + className);
        }
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className
     *            the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @param methodName
     *            the methodName to set
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
