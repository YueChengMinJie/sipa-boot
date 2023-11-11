package com.sipa.boot.test.it.extension.materials.customer.app;

import javax.annotation.Resource;

import com.sipa.boot.extension.ExtensionExecutor;
import com.sipa.boot.test.it.extension.materials.customer.app.extensionpoint.AddCustomerValidatorExtPt;
import com.sipa.boot.test.it.extension.materials.customer.app.extensionpoint.CustomerConvertorExtPt;
import com.sipa.boot.test.it.extension.materials.customer.client.AddCustomerCmd;
import com.sipa.boot.test.it.extension.materials.customer.client.Response;
import com.sipa.boot.test.it.extension.materials.customer.domain.CustomerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * AddCustomerCmdExe
 *
 * @author caszhou
 * @date 2019/4/24
 */
@Component
public class AddCustomerCmdExe {
    private final Logger logger = LoggerFactory.getLogger(AddCustomerCmd.class);

    @Resource
    private ExtensionExecutor extensionExecutor;

    public Response execute(AddCustomerCmd cmd) {
        logger.info("Start processing command:" + cmd);

        // validation
        extensionExecutor.executeVoid(AddCustomerValidatorExtPt.class, cmd.getBizScenario(),
            extension -> extension.validate(cmd));

        // Convert CO to Entity
        CustomerEntity customerEntity = extensionExecutor.execute(CustomerConvertorExtPt.class, cmd.getBizScenario(),
            extension -> extension.clientToEntity(cmd));

        // Call Domain Entity for business logic processing
        logger.info("Call Domain Entity for business logic processing..." + customerEntity);
        customerEntity.addNewCustomer();

        logger.info("End processing command:" + cmd);
        return Response.buildSuccess();
    }
}
