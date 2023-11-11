package com.sipa.boot.test.it.extension.materials.customer.app.extension;

import org.springframework.stereotype.Component;

import com.sipa.boot.test.it.extension.materials.customer.client.AddCustomerCmd;
import com.sipa.boot.test.it.extension.materials.customer.client.CustomerDTO;
import com.sipa.boot.test.it.extension.materials.customer.domain.CustomerEntity;

/**
 * CustomerConvertor
 *
 * @author caszhou
 * @date 2019/4/24
 */
@Component
public class CustomerConvertor {
    public CustomerEntity clientToEntity(Object clientObject) {
        AddCustomerCmd addCustomerCmd = (AddCustomerCmd)clientObject;
        CustomerDTO customerDTO = addCustomerCmd.getCustomerDTO();
        CustomerEntity customerEntity = ApplicationContextHelper.getBean(CustomerEntity.class);
        customerEntity.setCompanyName(customerDTO.getCompanyName());
        customerEntity.setCustomerType(customerDTO.getCustomerType());
        customerEntity.setBizScenario(addCustomerCmd.getBizScenario());
        return customerEntity;
    }
}
