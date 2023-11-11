package com.sipa.boot.secure.server;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sipa.boot.core.secure.IdpUser;
import com.sipa.boot.core.secure.IdpUserHolder;
import com.sipa.boot.secure.SecureHelper;

/**
 * @author caszhou
 * @date 2023/5/11
 */
public class IdpUserHoldFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String idpUserHeader = request.getHeader(IdpUser.REST_WEB_KEY);
        if (StringUtils.isNotBlank(idpUserHeader)) {
            IdpUser idpUser = SecureHelper.parse(idpUserHeader);
            if (Objects.nonNull(idpUser)) {
                IdpUserHolder.set(idpUser);
            }
        }

        filterChain.doFilter(request, response);

        IdpUserHolder.remove();
    }
}
