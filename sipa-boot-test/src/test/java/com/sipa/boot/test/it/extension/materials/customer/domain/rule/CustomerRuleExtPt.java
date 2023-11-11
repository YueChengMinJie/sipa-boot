package com.sipa.boot.test.it.extension.materials.customer.domain.rule;

import com.sipa.boot.extension.ExtensionPoint;
import com.sipa.boot.test.it.extension.materials.customer.domain.CustomerEntity;

/**
 * CustomerRuleExtPt
 *
 * @author caszhou
 * @date 2019/4/24
 */
public interface CustomerRuleExtPt extends ExtensionPoint {
    // Different business check for different biz
    boolean addCustomerCheck(CustomerEntity customerEntity);
}
