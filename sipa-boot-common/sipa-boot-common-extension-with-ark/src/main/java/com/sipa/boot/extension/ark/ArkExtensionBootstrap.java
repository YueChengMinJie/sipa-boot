package com.sipa.boot.extension.ark;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.param.ReferenceParam;
import com.sipa.boot.ark.property.ArkHostProperty;
import com.sipa.boot.extension.BizScenario;
import com.sipa.boot.extension.DefaultExtensionBootstrap;
import com.sipa.boot.extension.ExtensionPoint;

/**
 * @author caszhou
 * @date 2019/4/24
 */
public class ArkExtensionBootstrap extends DefaultExtensionBootstrap implements ClientFactoryAware {
    @Resource
    private ArkHostProperty property;

    private ClientFactory clientFactory;

    @Override
    @PostConstruct
    public void init() {
        if (CollectionUtils.isNotEmpty(property.getExtensions())) {
            property.getExtensions().forEach(extensionProperty -> {
                ReferenceClient referenceClient = clientFactory.getClient(ReferenceClient.class);
                ReferenceParam<Object> referenceParam = new ReferenceParam<>();
                referenceParam.setInterfaceType(extensionProperty.getInterfaceType());
                String uniqueId = extensionProperty.getUniqueId();
                if (StringUtils.isNotBlank(uniqueId)) {
                    referenceParam.setUniqueId(uniqueId);
                }
                Object proxy = referenceClient.reference(referenceParam);
                extensionRegister
                    .doRegistrationForArk(
                        BizScenario.valueOf(extensionProperty.getBizId(), extensionProperty.getUseCase(),
                            extensionProperty.getScenario()),
                        extensionProperty.getInterfaceType(), (ExtensionPoint)proxy);
            });
        }

        super.init();
    }

    @Override
    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }
}
