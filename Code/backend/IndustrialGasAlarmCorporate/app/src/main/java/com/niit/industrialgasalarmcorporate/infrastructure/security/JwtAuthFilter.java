package com.niit.industrialgasalarmcorporate.infrastructure.security;

import com.niit.industrialgasalarmcorporate.common.utils.JwtUtil;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.JwtBlacklistRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtBlacklistRepository jwtBlacklistRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        if (jwtBlacklistRepository.isBlacklisted(token)) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Claims claims = jwtUtil.parseToken(token);
            String userUuid = claims.getSubject();
            if (userUuid == null || userUuid.isBlank()) {
                log.debug("JWT claims 缺少 subject，跳过认证");
                filterChain.doFilter(request, response);
                return;
            }
            String role = claims.get("role", String.class);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userUuid, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.setAttribute("userUuid", userUuid);
            request.setAttribute("username", claims.get("username", String.class));
        } catch (JwtException e) {
            log.debug("JWT 解析失败: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
