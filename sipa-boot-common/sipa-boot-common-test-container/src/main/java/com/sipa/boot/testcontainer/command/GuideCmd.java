package com.sipa.boot.testcontainer.command;

import com.sipa.boot.testcontainer.TestContainer;

/**
 * @author caszhou
 * @date 2019/4/24
 */
public class GuideCmd extends AbstractCommand {
    public static final String GUIDE_HELP = "h";

    public static final String GUIDE_REPEAT = "r";

    public static final String GUIDE_QUIT = "q";

    public GuideCmd(String cmdRaw) {
        super(cmdRaw);
    }

    @Override
    public void execute() {
        this.action();
    }

    @Override
    protected void action() {
        switch (this.cmdRaw) {
            case GUIDE_HELP:
                System.out.println("************** 欢迎使用轻量级TDD测试工具 ***************************");
                System.out.println("**** 1.测试单个方法，请在控制台输入方法全称");
                System.out.println("**** 例如：com.sipa.bcp.process.it.activiti.ProcessContractTest.testDeployment()");
                System.out.println("**** 2.测试整个测试类，请在控制台输入类全称");
                System.out.println("**** 例如：com.sipa.bcp.process.it.activiti.ProcessContractTest");
                System.out.println("**** 3.重复上一次测试，只需在控制台输入字母 - ‘r’");
                System.out.println("**** 4.自动生成测试类,请输入‘new 方法全称  参数1 参数2 ...’");
                System.out.println("**** 例如：new com.sipa.bcp.process.it.activiti.ProcessContractTest#testDeployment");
                System.out
                    .println("***********************************************************************************");
                break;
            case GUIDE_REPEAT:
                TestContainer.execute(preCmd.cmdRaw);
                break;
            case GUIDE_QUIT:
                System.exit(0);
                throw new Error("强制退出");
            default:
                break;
        }
    }
}
