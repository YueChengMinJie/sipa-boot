package com.sipa.boot.testcontainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sipa.boot.testcontainer.command.AbstractCommand;
import com.sipa.boot.testcontainer.command.GuideCmd;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2019/4/24
 */
@Slf4j
@Component
public class TestContainer implements ApplicationContextAware {
    private static ApplicationContext context;

    private static TestExecutor testExecutor;

    private static final AtomicBoolean INIT_FLAG = new AtomicBoolean(false);

    public static void init(ApplicationContext context) {
        if (!INIT_FLAG.compareAndSet(false, true)) {
            return;
        }
        if (context == null) {
            testExecutor = new TestExecutor(TestContainer.context);
        } else {
            testExecutor = new TestExecutor(context);
        }
    }

    /**
     * TestsContainer is optional to be in Spring Container
     *
     * @param context
     *            ApplicationContext to be provided
     */
    public static void start(ApplicationContext context) {
        TestContainer.context = context;
        start();
    }

    /**
     * TestsContainer must be within Spring Container
     */
    public static void start() {
        init(TestContainer.context);
        monitorConsole();
    }

    public static void execute(String input) {
        if (StringUtils.isEmpty(input)) {
            return;
        }
        input = input.trim();
        AbstractCommand command = AbstractCommand.createCmd(input);
        if (command == null) {
            log.error("Your input is not a valid qualified name");
            return;
        }
        command.execute();
    }

    public static TestExecutor getTestExecutor() {
        return testExecutor;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    private static void monitorConsole() {
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        String input = GuideCmd.GUIDE_HELP;
        while (true) {
            try {
                execute(input);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
                break;
            }
            try {
                input = bufferRead.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
