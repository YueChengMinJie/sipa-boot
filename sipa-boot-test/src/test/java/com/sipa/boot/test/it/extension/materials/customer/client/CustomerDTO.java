package com.sipa.boot.test.it.extension.materials.customer.client;

import com.sipa.boot.test.it.extension.materials.customer.domain.CustomerType;

/**
 * CustomerDTO
 *
 * @author caszhou
 * @date 2019/4/24
 */
public class CustomerDTO extends DTO {
    private static final long serialVersionUID = -6852056292893706250L;
    private String companyName;

    private String source; // advertisement, p4p, RFQ, ATM

    private CustomerType customerType; // potential, intentional, important, vip

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }
}
