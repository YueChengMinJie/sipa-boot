package com.sipa.boot.test.it.extension.materials.customer.app.extension;

import com.sipa.boot.extension.Extension;
import com.sipa.boot.test.it.extension.materials.customer.app.extensionpoint.AddCustomerValidatorExtPt;
import com.sipa.boot.test.it.extension.materials.customer.client.AddCustomerCmd;
import com.sipa.boot.test.it.extension.materials.customer.client.Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * AddCustomerBiz1UseCase1Scenario1Validator
 *
 * @author caszhou
 * @date 2019/4/24
 */
@Slf4j
@Extension(bizId = Constants.BIZ_1, useCase = Constants.USE_CASE_1, scenario = Constants.SCENARIO_1)
public class AddCustomerBiz1UseCase1Scenario1Validator implements AddCustomerValidatorExtPt {
    @Override
    public void validate(AddCustomerCmd addCustomerCmd) {
        log.info("Do validation for Biz_One's Use_Case_One's Scenario_One");
    }
}
