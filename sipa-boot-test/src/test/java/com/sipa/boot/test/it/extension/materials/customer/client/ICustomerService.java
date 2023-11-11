package com.sipa.boot.test.it.extension.materials.customer.client;

import com.sipa.boot.test.it.extension.materials.customer.app.SingleResponse;

/**
 * CustomerServiceI
 *
 * @author caszhou
 * @date 2019/4/24
 */
public interface ICustomerService {
    Response addCustomer(AddCustomerCmd addCustomerCmd);

    SingleResponse<CustomerDTO> getCustomer(GetOneCustomerQry getOneCustomerQry);
}
