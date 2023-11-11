package com.sipa.boot.test.it.extension.materials.customer.client;

import com.sipa.boot.extension.BizScenario;

import lombok.Data;

/**
 * AddCustomerCmd
 *
 * @author caszhou
 * @date 2019/4/24
 */
@Data
public class AddCustomerCmd extends Command {
    private static final long serialVersionUID = 938279537699138622L;
    private CustomerDTO customerDTO;

    private String biz;

    private BizScenario bizScenario;
}
