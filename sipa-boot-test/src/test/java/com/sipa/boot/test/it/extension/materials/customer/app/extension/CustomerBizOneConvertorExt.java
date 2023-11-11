package com.sipa.boot.test.it.extension.materials.customer.app.extension;

import com.sipa.boot.extension.Extension;
import com.sipa.boot.test.it.extension.materials.customer.app.extensionpoint.CustomerConvertorExtPt;
import com.sipa.boot.test.it.extension.materials.customer.domain.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;

import com.sipa.boot.test.it.extension.materials.customer.client.AddCustomerCmd;
import com.sipa.boot.test.it.extension.materials.customer.client.Constants;
import com.sipa.boot.test.it.extension.materials.customer.client.CustomerDTO;
import com.sipa.boot.test.it.extension.materials.customer.domain.SourceType;

/**
 * CustomerBizOneConvertorExt
 *
 * @author caszhou
 * @date 2019/4/24
 */
@Extension(bizId = Constants.BIZ_1)
public class CustomerBizOneConvertorExt implements CustomerConvertorExtPt {
    // Composite basic convertor to do basic conversion
    @Autowired
    private CustomerConvertor customerConvertor;

    @Override
    public CustomerEntity clientToEntity(AddCustomerCmd addCustomerCmd) {
        CustomerEntity customerEntity = customerConvertor.clientToEntity(addCustomerCmd);
        CustomerDTO customerDTO = addCustomerCmd.getCustomerDTO();
        // In this business, AD and RFQ are regarded as different source
        if (Constants.SOURCE_AD.equals(customerDTO.getSource())) {
            customerEntity.setSourceType(SourceType.AD);
        }
        if (Constants.SOURCE_RFQ.equals(customerDTO.getSource())) {
            customerEntity.setSourceType(SourceType.RFQ);
        }
        return customerEntity;
    }
}
