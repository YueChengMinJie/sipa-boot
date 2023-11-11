package com.sipa.boot.test.it.extension.materials.customer.infrastructure;

import com.sipa.boot.test.it.extension.materials.customer.domain.CustomerEntity;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

/**
 * CustomerRepository
 *
 * @author caszhou
 * @date 2019/4/24
 */
@Slf4j
@Repository
public class CustomerRepository {
    public void persist(CustomerEntity customerEntity) {
        log.info("Persist customer to DB : " + customerEntity);
    }
}
