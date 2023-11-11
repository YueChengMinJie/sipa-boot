package com.sipa.boot.test.it.extension.materials.customer.app.extensionpoint;

import com.sipa.boot.extension.ExtensionPoint;
import com.sipa.boot.test.it.extension.materials.customer.client.AddCustomerCmd;
import com.sipa.boot.test.it.extension.materials.customer.domain.CustomerEntity;

/**
 * CustomerConvertorExtPt
 *
 * @author caszhou
 * @date 2019/4/24
 */
public interface CustomerConvertorExtPt extends ExtensionPoint {
    CustomerEntity clientToEntity(AddCustomerCmd addCustomerCmd);
}
