package com.sipa.boot.testcontainer.command;

import com.sipa.boot.testcontainer.TestContainer;

/**
 * @author caszhou
 * @date 2019/4/24
 */
public class TestClassRunCmd extends AbstractCommand {
    private final String className;

    public TestClassRunCmd(String cmdRaw) {
        super(cmdRaw);
        this.className = cmdRaw;
    }

    @Override
    protected void action() {
        try {
            TestContainer.getTestExecutor().execute(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getClassName() {
        return className;
    }
}
