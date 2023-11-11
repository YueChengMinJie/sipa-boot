package com.sipa.boot.test.it.extension.materials.customer.domain.rule;

import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.extension.Extension;
import com.sipa.boot.test.it.extension.materials.customer.client.Constants;
import com.sipa.boot.test.it.extension.materials.customer.domain.CustomerEntity;
import com.sipa.boot.test.it.extension.materials.customer.domain.SourceType;

/**
 * CustomerBizOneRuleExt
 *
 * @author caszhou
 * @date 2019/4/24
 */
@Extension(bizId = Constants.BIZ_1)
public class CustomerBizOneRuleExt implements CustomerRuleExtPt {
    @Override
    public boolean addCustomerCheck(CustomerEntity customerEntity) {
        if (SourceType.AD == customerEntity.getSourceType()) {
            throw SystemExceptionFactory
                .bizException("Sorry, Customer from advertisement can not be added in this period");
        }
        return true;
    }
}
