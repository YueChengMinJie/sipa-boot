package com.sipa.boot.test.it.extension.materials.customer.domain.rule;

import com.sipa.boot.extension.Extension;
import com.sipa.boot.test.it.extension.materials.customer.client.Constants;
import com.sipa.boot.test.it.extension.materials.customer.domain.CustomerEntity;

/**
 * CustomerBizTwoRuleExt
 *
 * @author caszhou
 * @date 2019/4/24
 */
@Extension(bizId = Constants.BIZ_2)
public class CustomerBizTwoRuleExt implements CustomerRuleExtPt {
    @Override
    public boolean addCustomerCheck(CustomerEntity customerEntity) {
        // Any Customer can be added
        return true;
    }
}
