package com.sipa.boot.test.it.extension.materials.customer.app.extensionpoint;

import com.sipa.boot.extension.ExtensionPoint;

/**
 * This extension point supports state transition operations of multiple manufacturers and different business lines
 *
 * @author caszhou
 * @date 2019/4/24
 */
public interface StatusNameConvertorExtPt extends ExtensionPoint {
    String statusNameConvertor(Integer statusCode);
}
