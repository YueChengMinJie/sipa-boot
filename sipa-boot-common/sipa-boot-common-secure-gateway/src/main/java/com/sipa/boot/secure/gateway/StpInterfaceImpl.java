package com.sipa.boot.secure.gateway;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.compress.utils.Lists;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.secure.IdpAuth;
import com.sipa.boot.core.secure.IdpUser;
import com.sipa.boot.secure.SecureHelper;
import com.sipa.boot.secure.gateway.property.GatewaySecureProperty;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.extra.spring.SpringUtil;

/**
 * @author caszhou
 * @date 2023/5/11
 */
public class StpInterfaceImpl implements StpInterface {
    @Resource
    private IdpManager idpManager;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long applicationId = SecureHelper.getApplicationId();
        Long companyId = SecureHelper.getCompanyId();
        Long accountId = Convert.convert(new TypeReference<>() {}, loginId);
        IdpUser newIdpuser = this.idpManager.getIdpUser(accountId, applicationId, companyId);
        if (Objects.nonNull(newIdpuser)) {
            List<IdpAuth> auths = newIdpuser.getAuths();
            if (CollectionUtils.isNotEmpty(auths)) {
                return auths.stream().map(IdpAuth::getCode).collect(Collectors.toList());
            }
        }

        return Lists.newArrayList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roles = Lists.newArrayList();
        Long applicationId = SecureHelper.getApplicationId();
        Long companyId = SecureHelper.getCompanyId();
        Long accountId = Convert.convert(new TypeReference<>() {}, loginId);
        IdpUser idpUser = this.idpManager.getIdpUser(accountId, applicationId, companyId);
        if (Objects.nonNull(idpUser)) {
            List<String> userRoles = idpUser.getRoles();
            if (CollectionUtils.isNotEmpty(userRoles)) {
                roles.addAll(userRoles);
            }

            List<String> calcRoles = this.addRoles(idpUser.getAuths());
            if (CollectionUtils.isNotEmpty(calcRoles)) {
                roles.addAll(calcRoles);
            }
        }
        return roles;
    }

    private List<String> addRoles(List<IdpAuth> auths) {
        List<String> roles = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(auths)) {
            Map<String, List<String>> roleAuthentication =
                SpringUtil.getBean(GatewaySecureProperty.class).getRoleAuthentication();
            if (MapUtils.isNotEmpty(roleAuthentication)) {
                for (Map.Entry<String, List<String>> entry : roleAuthentication.entrySet()) {
                    if (auths.stream()
                        .anyMatch(idpAuth -> idpAuth.getUrl().startsWith(SipaConstant.Symbol.SLASH + entry.getKey()))) {
                        roles.addAll(entry.getValue());
                    }
                }
            }
        }
        return roles;
    }
}
