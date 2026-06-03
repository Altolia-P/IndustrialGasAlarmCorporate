package com.niit.industrialgasalarmcorporate.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * API 密钥认证过滤器。
 *
 * 从 X-API-Key 请求头读取 API 密钥，与配置的共享密钥（shared-key）
 * 或开发模式令牌（dev-token）进行比对：
 *
 * <ul>
 *   <li>未配置 shared-key 且 dev-mode=false：不校验，任何请求直接放行</li>
 *   <li>X-API-Key 为空或缺失：直接放行（不设置认证上下文）</li>
 *   <li>X-API-Key 匹配 shared-key：设置 ROLE_API 认证上下文</li>
 *   <li>X-API-Key 匹配 dev-token：设置 ROLE_DEV 认证上下文</li>
 *   <li>X-API-Key 不匹配任一：返回 401</li>
 * </ul>
 * 
 * 注意：不标注 @Component，由 SecurityConfig 手动创建 Bean，避免 Spring Boot 3.5
 * Filter 双重注册冲突。
 */
@Slf4j
public class ApiTokenFilter extends OncePerRequestFilter {

    /** 预共享密钥，通过 application.yml 配置 */
    @Value("${api-token-filter.shared-key:}")
    private String sharedKey;

    /** 是否开启开发模式（允许使用 dev-token） */
    @Value("${api-token-filter.dev-mode:false}")
    private boolean devMode;

    /** 开发模式令牌 */
    @Value("${api-token-filter.dev-token:}")
    private String devToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 未配置密钥 → 无 X-API-Key 头的请求放行（交给 JWT filter），
        // 带 X-API-Key 头的请求拒绝（无法验证）
        if ((sharedKey == null || sharedKey.isEmpty()) && !devMode) {
            String apiKey = request.getHeader("X-API-Key");
            if (apiKey != null && !apiKey.isEmpty()) {
                log.warn("API Token Filter: shared-key 未配置，拒绝 X-API-Key 请求: remoteAddr={}, uri={}",
                        request.getRemoteAddr(), request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"API密钥认证未配置\",\"data\":null}");
                return;
            }
            log.warn("API Token Filter: shared-key 未配置，所有请求将直接放行。生产环境请配置 api-token-filter.shared-key");
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader("X-API-Key");

        // 无 API Key → 直接放行，不设置上下文
        if (apiKey == null || apiKey.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 先检查 shared-key
        if (sharedKey != null && !sharedKey.isEmpty() && sharedKey.equals(apiKey)) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            "api-client", null,
                            List.of(new SimpleGrantedAuthority("ROLE_API")));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.setAttribute("apiClientType", "shared-key");
            log.debug("API 密钥认证成功：shared-key");
            filterChain.doFilter(request, response);
            return;
        }

        // 再检查 dev-token
        if (devMode && devToken != null && !devToken.isEmpty() && devToken.equals(apiKey)) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            "dev-user", null,
                            List.of(new SimpleGrantedAuthority("ROLE_DEV")));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.setAttribute("apiClientType", "dev-token");
            log.debug("API 密钥认证成功：dev-token");
            filterChain.doFilter(request, response);
            return;
        }

        // 不匹配 → 401
        log.warn("API 密钥认证失败: remoteAddr={}, uri={}",
                request.getRemoteAddr(), request.getRequestURI());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"API密钥认证失败\",\"data\":null}");
    }
}
