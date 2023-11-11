package com.sipa.boot.secure.gateway;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.core.ParameterizedTypeReference;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.sipa.boot.core.app.AppConstant;
import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.secure.IdpAuth;
import com.sipa.boot.core.secure.IdpUser;
import com.sipa.boot.core.util.SipaUtil;
import com.sipa.boot.rest.util.LoadBalanceRestUtil;

import cn.dev33.satoken.same.SaSameUtil;
import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/5/12
 */
@Slf4j
public class IdpManager {
    private static final String IDP_USER_KEY = "/idp/account/application";

    private static final String AUTH_CACHE_KEY = "/idp/all";

    private static final String ALL = "/**";

    private static final ExecutorService EXECUTOR_SERVICE = SipaUtil.newNormalPool();

    private final LoadingCache<String,
        List<IdpAuth>> authCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .refreshAfterWrite(Duration.ofMinutes(1))
            .build(this::loadAuthList);

    public IdpUser getIdpUser(Long accountId, Long applicationId, Long companyId) {
        try {
            return CompletableFuture.supplyAsync(() -> LoadBalanceRestUtil.post(
                // url
                "http://" + AppConstant.BCP.SSO_IDP_NAME + IDP_USER_KEY,
                // header
                Map.of(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken()),
                // payload
                IdpUser.builder().id(accountId).companyId(companyId).applicationId(applicationId).build(),
                // rest
                IdpUser.class), EXECUTOR_SERVICE).get(SipaConstant.Number.INT_3, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("IdpManager.getIdpUser failed", e);
            return IdpUser.empty();
        }
    };

    public List<IdpAuth> getIdpAuthList(Long applicationId) {
        return this.authCache.get(AUTH_CACHE_KEY + SipaConstant.Symbol.SLASH + applicationId);
    }

    private List<IdpAuth> loadAuthList(@NonNull String key) {
        try {
            return CompletableFuture.supplyAsync(() -> LoadBalanceRestUtil.post(
                // url
                "http://" + AppConstant.BCP.SSO_IDP_NAME + key,
                // header
                Map.of(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken()),
                // payload
                null,
                // rest
                new ParameterizedTypeReference<List<IdpAuth>>() {}), EXECUTOR_SERVICE)
                .get(SipaConstant.Number.INT_3, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("IdpManager.loadAuthList failed", e);
            String applicationId = key.substring(key.lastIndexOf(SipaConstant.Symbol.SLASH) + 1);
            return List.of(IdpAuth.builder()
                .url(ALL)
                .code(UUID.randomUUID().toString())
                .applicationId(Long.valueOf(applicationId))
                .build());
        }
    }
}
