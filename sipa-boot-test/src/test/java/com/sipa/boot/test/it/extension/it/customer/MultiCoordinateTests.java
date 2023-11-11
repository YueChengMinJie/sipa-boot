package com.sipa.boot.test.it.extension.it.customer;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sipa.boot.extension.BizScenario;
import com.sipa.boot.extension.ExtensionExecutor;
import com.sipa.boot.test.SipaTestApplication;
import com.sipa.boot.test.it.extension.materials.customer.app.extensionpoint.StatusNameConvertorExtPt;

/**
 * 多坐标测试
 *
 * @author caszhou
 * @date 2019/4/24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SipaTestApplication.class)
public class MultiCoordinateTests {
    @Resource
    private ExtensionExecutor extensionExecutor;

    @Test
    @Ignore
    public void testMultiCoordinate() {
        BizScenario bizScenario1 = BizScenario.valueOf("Samsung", "order", "scenario1");
        BizScenario bizScenario2 = BizScenario.valueOf("Samsung", "order", "scenario2");
        BizScenario bizScenario3 = BizScenario.valueOf("Samsung", "parts", "scenario1");
        BizScenario bizScenario4 = BizScenario.valueOf("Samsung", "parts", "scenario2");
        BizScenario bizScenario5 = BizScenario.valueOf("Motorola", "order", "scenario1");
        BizScenario bizScenario6 = BizScenario.valueOf("Motorola", "order", "scenario2");
        BizScenario bizScenario7 = BizScenario.valueOf("Motorola", "parts", "scenario1");
        BizScenario bizScenario8 = BizScenario.valueOf("Motorola", "parts", "scenario2");
        String name1 =
                this.extensionExecutor.execute(StatusNameConvertorExtPt.class, bizScenario1, pt -> pt.statusNameConvertor(1));
        String name2 =
                this.extensionExecutor.execute(StatusNameConvertorExtPt.class, bizScenario2, pt -> pt.statusNameConvertor(2));
        String name3 =
                this.extensionExecutor.execute(StatusNameConvertorExtPt.class, bizScenario3, pt -> pt.statusNameConvertor(3));
        String name4 =
                this.extensionExecutor.execute(StatusNameConvertorExtPt.class, bizScenario4, pt -> pt.statusNameConvertor(4));
        String name5 =
                this.extensionExecutor.execute(StatusNameConvertorExtPt.class, bizScenario5, pt -> pt.statusNameConvertor(5));
        String name6 =
                this.extensionExecutor.execute(StatusNameConvertorExtPt.class, bizScenario6, pt -> pt.statusNameConvertor(6));
        String name7 =
                this.extensionExecutor.execute(StatusNameConvertorExtPt.class, bizScenario7, pt -> pt.statusNameConvertor(7));
        String name8 =
                this.extensionExecutor.execute(StatusNameConvertorExtPt.class, bizScenario8, pt -> pt.statusNameConvertor(8));

        Assert.assertEquals("one", name1);
        Assert.assertEquals("two", name2);
        Assert.assertEquals("three", name3);
        Assert.assertEquals("four", name4);
        Assert.assertEquals("five", name5);
        Assert.assertEquals("six", name6);
        Assert.assertEquals("seven", name7);
        Assert.assertEquals("eight", name8);
    }

    @Test
    @Ignore
    public void testMultiCoordinateWithAnnotation() {
        BizScenario bizScenario1 = BizScenario.valueOf("Sony", "user", "scenario3");
        BizScenario bizScenario2 = BizScenario.valueOf("Siemens", "order", "scenario1");
        String name1 =
                this.extensionExecutor.execute(StatusNameConvertorExtPt.class, bizScenario1, pt -> pt.statusNameConvertor(1));
        String name2 =
                this.extensionExecutor.execute(StatusNameConvertorExtPt.class, bizScenario2, pt -> pt.statusNameConvertor(2));
        Assert.assertEquals("one", name1);
        Assert.assertEquals("two", name2);
    }
}
