package com.sipa.boot.storage.tika;

import org.apache.tika.Tika;

/**
 * Tika 工厂类接口
 * 
 * @author caszhou
 * @date 2022/12/23
 */
public interface TikaFactory {
    Tika getTika();
}
