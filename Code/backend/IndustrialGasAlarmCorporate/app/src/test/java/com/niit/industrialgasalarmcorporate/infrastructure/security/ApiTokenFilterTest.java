package com.niit.industrialgasalarmcorporate.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApiTokenFilter X-API-Key 认证过滤器")
class ApiTokenFilterTest {

    private static final String SHARED_KEY_VALUE = "my-secret-api-key";
    private static final String DEV_TOKEN_VALUE = "dev-test-token-2026";

    @Mock private DeviceRepository deviceRepository;
    private ApiTokenFilter filter;

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        filter = new ApiTokenFilter();
        SecurityContextHolder.clearContext();
    }

    // ──────────────────────────────────────────────
    // No config (no shared-key, no dev-mode)
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("无配置时")
    class WhenNoConfig {

        @BeforeEach
        void setUp() {
            ReflectionTestUtils.setField(filter, "sharedKey", "");
            ReflectionTestUtils.setField(filter, "devMode", false);
            ReflectionTestUtils.setField(filter, "devToken", "");
        }

        @Test
        @DisplayName("无 X-API-Key 头部 → 直接放行")
        void shouldPassThroughWhenNoHeader() throws Exception {
            when(request.getHeader("X-API-Key")).thenReturn(null);

            filter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            verify(response, never()).setStatus(anyInt());
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("带 X-API-Key 头部 → 拒绝 401（密钥未配置，无法验证）")
        void shouldRejectWhenApiKeyPresent() throws Exception {
            StringWriter sw = new StringWriter();
            when(request.getHeader("X-API-Key")).thenReturn("some-key");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(request.getRequestURI()).thenReturn("/api/v1/device/data");
            when(response.getWriter()).thenReturn(new PrintWriter(sw));

            filter.doFilter(request, response, filterChain);

            verify(filterChain, never()).doFilter(request, response);
            verify(response).setStatus(401);
        }
    }

    // ──────────────────────────────────────────────
    // Shared-key configured
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("预共享密钥 shared-key 已配置时")
    class WhenSharedKeyConfigured {

        @BeforeEach
        void setUp() {
            ReflectionTestUtils.setField(filter, "sharedKey", SHARED_KEY_VALUE);
            ReflectionTestUtils.setField(filter, "devMode", false);
            ReflectionTestUtils.setField(filter, "devToken", "");
        }

        @Test
        @DisplayName("无 X-API-Key 头部 → 直接放行")
        void shouldPassThroughWhenNoHeader() throws Exception {
            when(request.getHeader("X-API-Key")).thenReturn(null);

            filter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("X-API-Key 匹配 shared-key → 设置 ROLE_API 认证")
        void shouldAuthenticateWhenKeyMatches() throws Exception {
            when(request.getHeader("X-API-Key")).thenReturn(SHARED_KEY_VALUE);
            lenient().when(request.getRequestURI()).thenReturn("/api/v1/user/profile");

            filter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth);
            assertEquals("api-client", auth.getPrincipal());
            assertEquals(1, auth.getAuthorities().size());
            assertEquals("ROLE_API", auth.getAuthorities().iterator().next().getAuthority());
        }

        @Test
        @DisplayName("X-API-Key 不匹配 shared-key → 返回 401")
        void shouldReturn401WhenKeyMismatch() throws Exception {
            StringWriter sw = new StringWriter();
            when(request.getHeader("X-API-Key")).thenReturn("wrong-key");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(request.getRequestURI()).thenReturn("/api/v1/user/profile");
            when(response.getWriter()).thenReturn(new PrintWriter(sw));

            filter.doFilter(request, response, filterChain);

            verify(filterChain, never()).doFilter(request, response);
            verify(response).setStatus(401);
            verify(response).setContentType("application/json;charset=UTF-8");

            String json = sw.toString();
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(json, Map.class);
            assertEquals(401, result.get("code"));
            assertNotNull(result.get("message"));
        }

        @Test
        @DisplayName("空 X-API-Key 头部 → 直接放行")
        void shouldPassThroughWhenHeaderIsEmpty() throws Exception {
            when(request.getHeader("X-API-Key")).thenReturn("");

            filter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }

    // ──────────────────────────────────────────────
    // Dev-mode enabled
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("dev-mode 开启时")
    class WhenDevModeEnabled {

        @BeforeEach
        void setUp() {
            ReflectionTestUtils.setField(filter, "sharedKey", "");
            ReflectionTestUtils.setField(filter, "devMode", true);
            ReflectionTestUtils.setField(filter, "devToken", DEV_TOKEN_VALUE);
        }

        @Test
        @DisplayName("X-API-Key 匹配 dev-token → 设置 ROLE_DEV 认证")
        void shouldAuthWithDevToken() throws Exception {
            when(request.getHeader("X-API-Key")).thenReturn(DEV_TOKEN_VALUE);
            lenient().when(request.getRequestURI()).thenReturn("/api/v1/user/profile");

            filter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth);
            assertEquals("dev-user", auth.getPrincipal());
            assertEquals("ROLE_DEV", auth.getAuthorities().iterator().next().getAuthority());
        }

        @Test
        @DisplayName("X-API-Key 不匹配 dev-token → 返回 401")
        void shouldRejectInvalidDevToken() throws Exception {
            StringWriter sw = new StringWriter();
            when(request.getHeader("X-API-Key")).thenReturn("invalid-dev-token");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(request.getRequestURI()).thenReturn("/api/v1/user/profile");
            when(response.getWriter()).thenReturn(new PrintWriter(sw));

            filter.doFilter(request, response, filterChain);

            verify(filterChain, never()).doFilter(request, response);
            verify(response).setStatus(401);
        }
    }

    // ──────────────────────────────────────────────
    // Shared-key + dev-mode both configured
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("shared-key 和 dev-mode 同时配置时")
    class WhenBothConfigured {

        @BeforeEach
        void setUp() {
            ReflectionTestUtils.setField(filter, "sharedKey", SHARED_KEY_VALUE);
            ReflectionTestUtils.setField(filter, "devMode", true);
            ReflectionTestUtils.setField(filter, "devToken", DEV_TOKEN_VALUE);
        }

        @Test
        @DisplayName("shared-key 优先级高于 dev-token")
        void shouldPreferSharedKeyOverDevToken() throws Exception {
            // dev-token 恰好与 shared-key 不同，但用 shared-key 匹配
            when(request.getHeader("X-API-Key")).thenReturn(SHARED_KEY_VALUE);
            lenient().when(request.getRequestURI()).thenReturn("/api/v1/user/profile");

            filter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth);
            assertEquals("api-client", auth.getPrincipal());  // ROLE_API, not ROLE_DEV
        }

        @Test
        @DisplayName("均不匹配 → 401")
        void shouldRejectWhenNeitherMatches() throws Exception {
            StringWriter sw = new StringWriter();
            when(request.getHeader("X-API-Key")).thenReturn("completely-wrong-key");
            when(request.getRemoteAddr()).thenReturn("10.0.0.1");
            when(request.getRequestURI()).thenReturn("/api/v1/staff/tasks");
            when(response.getWriter()).thenReturn(new PrintWriter(sw));

            filter.doFilter(request, response, filterChain);

            verify(filterChain, never()).doFilter(request, response);
            verify(response).setStatus(401);
        }
    }
}
