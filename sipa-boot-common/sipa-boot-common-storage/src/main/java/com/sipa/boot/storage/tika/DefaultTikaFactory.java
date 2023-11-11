package com.sipa.boot.storage.tika;

import org.apache.tika.Tika;

/**
 * 默认的 Tika 工厂类
 * 
 * @author caszhou
 * @date 2022/12/23
 */
public class DefaultTikaFactory implements TikaFactory {
    private Tika tika;

    @Override
    public Tika getTika() {
        if (tika == null) {
            tika = new Tika();
        }
        return tika;
    }
}
