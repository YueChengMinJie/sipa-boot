package com.sipa.boot.test.it.extension.materials.customer.app.extensionpoint;

import com.sipa.boot.extension.ExtensionPoint;
import com.sipa.boot.test.it.extension.materials.customer.client.AddCustomerCmd;

/**
 * AddCustomerValidatorExtPt
 *
 * @author caszhou
 * @date 2019/4/24
 */
public interface AddCustomerValidatorExtPt extends ExtensionPoint {
    void validate(AddCustomerCmd addCustomerCmd);
}
