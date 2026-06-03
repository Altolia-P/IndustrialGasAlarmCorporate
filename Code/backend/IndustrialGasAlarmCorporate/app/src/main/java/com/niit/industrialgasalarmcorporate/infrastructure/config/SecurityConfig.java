package com.niit.industrialgasalarmcorporate.infrastructure.config;

import com.niit.industrialgasalarmcorporate.common.utils.JwtUtil;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.JwtBlacklistRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.security.ApiTokenFilter;
import com.niit.industrialgasalarmcorporate.infrastructure.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 配置。
 * 
 * 注意：JwtAuthFilter 和 ApiTokenFilter 不标注 @Component，由此处手动创建，
 * 避免 Spring Boot 3.5 Filter 双重注册冲突。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers:Authorization,Content-Type,X-Trace-Id}")
    private String allowedHeaders;

    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    /** 手动创建 JwtAuthFilter，不作为 Servlet Filter 自动注册 */
    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtUtil jwtUtil, JwtBlacklistRepository jwtBlacklistRepository) {
        return new JwtAuthFilter(jwtUtil, jwtBlacklistRepository);
    }

    /** 手动创建 ApiTokenFilter，不作为 Servlet Filter 自动注册 */
    @Bean
    public ApiTokenFilter apiTokenFilter() {
        return new ApiTokenFilter();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthFilter jwtAuthFilter,
                                           ApiTokenFilter apiTokenFilter,
                                           CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .headers(headers -> headers
                        .xssProtection(xss -> xss.headerValue(org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                        .contentTypeOptions(contentType -> {})
                        .frameOptions(frame -> frame.deny())
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/v1/internal/**").permitAll()
                        .requestMatchers("/api/v1/public/**").permitAll()
                        .requestMatchers("/api/v1/dashboard/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/captcha").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/logout").authenticated()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/staff/**").hasAnyRole("STAFF", "ADMIN")
                        .requestMatchers("/api/v1/user/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiTokenFilter, JwtAuthFilter.class);

        return http.build();
    }
}
