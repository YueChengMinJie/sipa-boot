package com.sipa.boot.lock.client.etcd;

import lombok.Data;

/**
 * @author caszhou
 * @date 2023/4/20
 */
@Data
public class EtcdRequest {
    private String value;

    private boolean prevExist;

    private boolean refresh;

    private Long ttl;
}
