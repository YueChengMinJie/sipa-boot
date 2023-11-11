package com.sipa.boot.secure.gateway.util;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.core.secure.IdpAuth;
import com.sipa.boot.core.secure.IdpUser;
import com.sipa.boot.secure.gateway.IdpManager;
import com.sipa.boot.secure.gateway.property.GatewaySecureProperty;

import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/6/22
 */
@Slf4j
public class SipaStpUtil {
    private static String getServiceName() {
        String path = SaReactorSyncHolder.getContext().getRequest().getURI().getPath();
        if (StringUtils.isNotBlank(path)) {
            String[] split = path.split("/");
            if (split.length > 1) {
                return split[1];
            }
        }
        return null;
    }

    private static GatewaySecureProperty getGatewaySecureProperty() {
        return SpringUtil.getBean(GatewaySecureProperty.class);
    }

    public static void checkRole() {
        String serviceName = getServiceName();
        if (StringUtils.isNotBlank(serviceName)) {
            List<String> roleNames = getGatewaySecureProperty().getRoleAuthentication().get(serviceName);
            if (CollectionUtils.isNotEmpty(roleNames)) {
                StpUtil.checkRoleOr(ArrayUtil.toArray(roleNames, String.class));
            }
        }
    }

    public static void checkPermission(IdpUser idpUser) {
        if (Objects.nonNull(idpUser)) {
            List<IdpAuth> idpAuths = getIdpManager().getIdpAuthList(idpUser.getApplicationId());
            if (CollectionUtils.isNotEmpty(idpAuths)) {
                for (IdpAuth auth : idpAuths) {
                    if (auth.notNull()) {
                        String url = auth.getUrl();
                        String code = auth.getCode();
                        SaRouter.match(url).check(() -> StpUtil.checkPermission(code));
                    }
                }
            }
        }
    }

    private static IdpManager getIdpManager() {
        return SpringUtil.getBean(IdpManager.class);
    }
}
