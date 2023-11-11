package com.sipa.boot.test.it.extension.materials.customer.app.extension;

import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.extension.Extension;
import com.sipa.boot.test.it.extension.materials.customer.app.extensionpoint.AddCustomerValidatorExtPt;
import com.sipa.boot.test.it.extension.materials.customer.client.AddCustomerCmd;
import com.sipa.boot.test.it.extension.materials.customer.client.Constants;
import com.sipa.boot.test.it.extension.materials.customer.domain.CustomerType;

import lombok.extern.slf4j.Slf4j;

/**
 * AddCustomerBizOneValidator
 *
 * @author caszhou
 * @date 2019/4/24
 */
@Slf4j
@Extension(bizId = Constants.BIZ_1)
public class AddCustomerBizOneValidator implements AddCustomerValidatorExtPt {
    @Override
    public void validate(AddCustomerCmd addCustomerCmd) {
        log.info("Do validation for Biz_One'");
        // For BIZ TWO CustomerTYpe could not be VIP
        if (CustomerType.VIP == addCustomerCmd.getCustomerDTO().getCustomerType())
            throw SystemExceptionFactory.bizException("Customer Type could not be VIP for Biz One");
    }
}
