package com.sipa.boot.test.it.extension.materials.customer.app.extension;

import com.sipa.boot.extension.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.sipa.boot.test.it.extension.materials.customer.app.extensionpoint.CustomerConvertorExtPt;
import com.sipa.boot.test.it.extension.materials.customer.client.AddCustomerCmd;
import com.sipa.boot.test.it.extension.materials.customer.client.Constants;
import com.sipa.boot.test.it.extension.materials.customer.domain.CustomerEntity;
import com.sipa.boot.test.it.extension.materials.customer.domain.SourceType;

/**
 * CustomerBizTwoConvertorExt
 *
 * @author caszhou
 * @date 2019/4/24
 */
@Extension(bizId = Constants.BIZ_2)
public class CustomerBizTwoConvertorExt implements CustomerConvertorExtPt {
    @Autowired
    private CustomerConvertor customerConvertor;// Composite basic convertor to do basic conversion

    @Override
    public CustomerEntity clientToEntity(AddCustomerCmd addCustomerCmd) {
        CustomerEntity customerEntity = customerConvertor.clientToEntity(addCustomerCmd);
        // In this business, if customers from RFQ and Advertisement are both regarded as Advertisement
        if (Constants.SOURCE_AD.equals(addCustomerCmd.getCustomerDTO().getSource())
            || Constants.SOURCE_RFQ.equals(addCustomerCmd.getCustomerDTO().getSource())) {
            customerEntity.setSourceType(SourceType.AD);
        }
        return customerEntity;
    }
}
