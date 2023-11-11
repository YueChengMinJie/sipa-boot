package com.sipa.boot.test.it.extension.materials.customer.app;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.sipa.boot.test.it.extension.materials.customer.client.*;

/**
 * CustomerServiceImpl
 *
 * @author caszhou
 * @date 2019/4/24
 */
@Service
public class CustomerServiceImpl implements ICustomerService {
    @Resource
    private AddCustomerCmdExe addCustomerCmdExe;

    @Resource
    private GetOneCustomerQryExe getOneCustomerQryExe;

    @Override
    public Response addCustomer(AddCustomerCmd addCustomerCmd) {
        return this.addCustomerCmdExe.execute(addCustomerCmd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SingleResponse<CustomerDTO> getCustomer(GetOneCustomerQry getOneCustomerQry) {
        return this.getOneCustomerQryExe.execute(getOneCustomerQry);
    }
}
