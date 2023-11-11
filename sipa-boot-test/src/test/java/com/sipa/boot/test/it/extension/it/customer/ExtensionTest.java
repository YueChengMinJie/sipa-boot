package com.sipa.boot.test.it.extension.it.customer;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sipa.boot.extension.BizScenario;
import com.sipa.boot.test.SipaTestApplication;
import com.sipa.boot.test.it.extension.materials.customer.client.*;
import com.sipa.boot.test.it.extension.materials.customer.domain.CustomerType;

/**
 * ExtensionTest
 *
 * @author caszhou
 * @date 2019/4/24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SipaTestApplication.class)
public class ExtensionTest {
    @Resource
    private ICustomerService customerService;

    @Test
    @Ignore
    public void testBiz1UseCase1Scenario1AddCustomerSuccess() {
        // 1. Prepare
        AddCustomerCmd addCustomerCmd = new AddCustomerCmd();
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCompanyName("alibaba");
        customerDTO.setSource(Constants.SOURCE_RFQ);
        customerDTO.setCustomerType(CustomerType.IMPORTANT);
        addCustomerCmd.setCustomerDTO(customerDTO);
        BizScenario scenario = BizScenario.valueOf(Constants.BIZ_1, Constants.USE_CASE_1, Constants.SCENARIO_1);
        addCustomerCmd.setBizScenario(scenario);

        // 2. Execute
        Response response = this.customerService.addCustomer(addCustomerCmd);

        // 3. Expect Success
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    @Ignore
    public void testBiz1UseCase1AddCustomerSuccess() {
        // 1. Prepare
        AddCustomerCmd addCustomerCmd = new AddCustomerCmd();
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCompanyName("alibaba");
        customerDTO.setSource(Constants.SOURCE_RFQ);
        customerDTO.setCustomerType(CustomerType.IMPORTANT);
        addCustomerCmd.setCustomerDTO(customerDTO);
        BizScenario scenario = BizScenario.valueOf(Constants.BIZ_1, Constants.USE_CASE_1);
        addCustomerCmd.setBizScenario(scenario);

        // 2. Execute
        Response response = this.customerService.addCustomer(addCustomerCmd);

        // 3. Expect Success
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    @Ignore
    public void testBiz1AddCustomerSuccess() {
        // 1. Prepare
        AddCustomerCmd addCustomerCmd = new AddCustomerCmd();
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCompanyName("jingdong");
        customerDTO.setSource(Constants.SOURCE_RFQ);
        customerDTO.setCustomerType(CustomerType.IMPORTANT);
        addCustomerCmd.setCustomerDTO(customerDTO);
        BizScenario scenario = BizScenario.valueOf(Constants.BIZ_1);
        addCustomerCmd.setBizScenario(scenario);

        // 2. Execute
        Response response = this.customerService.addCustomer(addCustomerCmd);

        // 3. Expect Success
        Assert.assertTrue(response.isSuccess());
    }
}
