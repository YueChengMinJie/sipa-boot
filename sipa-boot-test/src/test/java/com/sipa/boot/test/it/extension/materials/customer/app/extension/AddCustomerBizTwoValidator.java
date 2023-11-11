package com.sipa.boot.test.it.extension.materials.customer.app.extension;

import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.extension.Extension;
import com.sipa.boot.test.it.extension.materials.customer.app.extensionpoint.AddCustomerValidatorExtPt;
import com.sipa.boot.test.it.extension.materials.customer.client.AddCustomerCmd;
import com.sipa.boot.test.it.extension.materials.customer.client.Constants;

/**
 * AddCustomerBizTwoValidator
 *
 * @author caszhou
 * @date 2019/4/24
 */
@Extension(bizId = Constants.BIZ_2)
public class AddCustomerBizTwoValidator implements AddCustomerValidatorExtPt {
    @Override
    public void validate(AddCustomerCmd addCustomerCmd) {
        // For BIZ TWO CustomerTYpe could not be null
        if (addCustomerCmd.getCustomerDTO().getCustomerType() == null)
            throw SystemExceptionFactory.bizException("CustomerType could not be null");
    }
}
