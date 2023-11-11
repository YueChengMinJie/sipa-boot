package com.sipa.boot.test.it.extension.materials.customer.client;

/**
 * GetOneCustomerQry
 *
 * @author caszhou
 * @date 2019/4/24
 */
public class GetOneCustomerQry extends Query {
    private static final long serialVersionUID = -8067132207649031169L;
    private long customerId;

    private String companyName;

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
