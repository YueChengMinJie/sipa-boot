package com.sipa.boot.testcontainer.command;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.sipa.boot.testcontainer.TestContainer;

/**
 * @author caszhou
 * @date 2019/4/24
 */
public class TestMethodRunCmd extends AbstractCommand {
    private static final String RE_RECORD = "rr";

    public final static String DOT = ".";

    public final static String NOTE_SYMBOL = "#";

    private String methodName;

    private String className;

    public TestMethodRunCmd(String cmdRaw) {
        super(cmdRaw);
        parseCommand();
    }

    @Override
    protected void action() {
        try {
            TestContainer.getTestExecutor().execute(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initParser(Options options) {
        Option point = Option.builder(RE_RECORD)
            .hasArgs()
            .argName("p1,p2...")
            .valueSeparator(',')
            .desc("A directories list with ',' separate to handle its child files")
            .build();
        options.addOption(point);
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClassName() {
        return className;
    }

    public boolean isSegmentRecord() {
        return false;
    }

    private void parseCommand() {
        String cmd = getCommandLine().getArgs()[0];
        if (isEclipseMethod(cmd)) {
            methodName = cmd.substring(cmd.lastIndexOf(DOT) + 1, cmd.indexOf("("));
            className = cmd.substring(0, cmd.lastIndexOf(DOT));
        }
        if (isIdeaMethod(cmd)) {
            methodName = cmd.substring(cmd.lastIndexOf(NOTE_SYMBOL) + 1);
            className = cmd.substring(0, cmd.lastIndexOf(NOTE_SYMBOL));
        }
    }
}
