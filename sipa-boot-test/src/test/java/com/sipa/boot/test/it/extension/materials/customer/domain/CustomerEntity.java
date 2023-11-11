package com.sipa.boot.test.it.extension.materials.customer.domain;

import com.sipa.boot.extension.BizScenario;
import com.sipa.boot.extension.ExtensionExecutor;
import com.sipa.boot.test.it.extension.materials.customer.domain.rule.CustomerRuleExtPt;
import org.springframework.beans.factory.annotation.Autowired;

import com.sipa.boot.test.it.extension.materials.customer.infrastructure.CustomerRepository;

import lombok.Data;

/**
 * Customer Entity
 *
 * @author caszhou
 * @date 2019/4/24
 */
@Entity
@Data
public class CustomerEntity {
    private String companyName;

    private SourceType sourceType;

    private CustomerType customerType;

    private BizScenario bizScenario;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ExtensionExecutor extensionExecutor;

    public CustomerEntity() {}

    public void addNewCustomer() {
        // Add customer policy
        extensionExecutor.execute(CustomerRuleExtPt.class, this.getBizScenario(),
            extension -> extension.addCustomerCheck(this));

        // Persist customer
        customerRepository.persist(this);
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }
}
