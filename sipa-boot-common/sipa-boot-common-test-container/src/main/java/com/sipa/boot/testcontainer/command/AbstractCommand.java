package com.sipa.boot.testcontainer.command;

import java.util.HashMap;
import java.util.Map;

import com.sipa.boot.testcontainer.enumeration.ECommand;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

/**
 * @author caszhou
 * @date 2019/4/24
 */
public abstract class AbstractCommand {
    private static final CommandLineParser PARSER = new DefaultParser();

    protected static AbstractCommand curCmd;

    protected static AbstractCommand preCmd;

    protected String cmdRaw;

    private final Map<String, Object> params;

    private final Options options;

    private final CommandLine commandLine;

    private final String SPACE = " ";

    public AbstractCommand(String cmdRaw) {
        this.cmdRaw = cmdRaw.replaceAll(" +", SPACE);
        params = new HashMap<>(16);
        options = new Options();
        initParser(options);
        commandLine = parse();
    }

    public void execute() {
        System.out.println("===Run start==== " + cmdRaw);
        action();
        System.out.println("===Run end====\n");
    }

    /**
     * 清理当前命令的上下文
     */
    protected void cleanContext() {}

    protected void initParser(Options options) {}

    protected abstract void action();

    public CommandLine parse() {
        try {
            return PARSER.parse(options, cmdRaw.split(SPACE));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getParam(String key) {
        return params.get(key);
    }

    public void putParam(String key, Object value) {
        params.put(key, value);
    }

    public String getStringParam(String key) {
        Object value = params.get(key);
        if (value == null) {
            return StringUtils.EMPTY;
        }
        return value.toString();
    }

    public boolean isEclipseMethod(String input) {
        return input.indexOf("(") > 0;
    }

    public boolean isIdeaMethod(String input) {
        return input.indexOf("#") > 0;
    }

    public CommandLine getCommandLine() {
        return commandLine;
    }

    public static AbstractCommand createCmd(String cmdRaw) {
        if (StringUtils.isEmpty(cmdRaw)) {
            return null;
        }
        AbstractCommand command = null;
        if (cmdRaw.matches(ECommand.TestMethodRunCmd.getDesc())) {
            command = new TestMethodRunCmd(cmdRaw);
        } else if (cmdRaw.matches(ECommand.TestClassRunCmd.getDesc())) {
            command = new TestClassRunCmd(cmdRaw);
        } else if (cmdRaw.matches(ECommand.GuideCmd.getDesc())) {
            command = new GuideCmd(cmdRaw);
        }
        if (command != null) {
            preCmd = curCmd;
            curCmd = command;
        }
        if (preCmd != null) {
            preCmd.cleanContext();
        }
        return command;
    }
}
