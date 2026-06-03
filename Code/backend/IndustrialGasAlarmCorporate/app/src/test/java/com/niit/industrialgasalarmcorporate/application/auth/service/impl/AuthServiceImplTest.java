package com.niit.industrialgasalarmcorporate.application.auth.service.impl;

import com.niit.industrialgasalarmcorporate.application.auth.dto.LoginDTO;
import com.niit.industrialgasalarmcorporate.application.auth.dto.RegisterDTO;
import com.niit.industrialgasalarmcorporate.application.auth.dto.UpdateProfileDTO;
import com.niit.industrialgasalarmcorporate.application.auth.dto.UserResetPasswordDTO;
import com.niit.industrialgasalarmcorporate.application.auth.vo.LoginResultVO;
import com.niit.industrialgasalarmcorporate.application.auth.vo.UserVO;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.common.exception.InvalidPasswordException;
import com.niit.industrialgasalarmcorporate.common.exception.AccountLockedException;
import com.niit.industrialgasalarmcorporate.common.utils.CaptchaGenerator;
import com.niit.industrialgasalarmcorporate.common.utils.JwtUtil;
import com.niit.industrialgasalarmcorporate.domain.auth.LoginResult;
import com.niit.industrialgasalarmcorporate.domain.auth.PasswordHasher;
import com.niit.industrialgasalarmcorporate.domain.auth.User;
import com.niit.industrialgasalarmcorporate.domain.auth.UserRepository;
import com.niit.industrialgasalarmcorporate.domain.event.AccountLockedEvent;
import com.niit.industrialgasalarmcorporate.domain.event.EventBus;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.CaptchaRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.JwtBlacklistRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.LoginRateLimitRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.RegisterRateLimitRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl 认证服务")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EventBus eventBus;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private CaptchaGenerator captchaGenerator;

    @Mock
    private CaptchaRepository captchaRepository;

    @Mock
    private JwtBlacklistRepository jwtBlacklistRepository;

    @Mock
    private LoginRateLimitRepository loginRateLimitRepository;

    @Mock
    private RegisterRateLimitRepository registerRateLimitRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthServiceImpl authService;

    private static final String USERNAME = "testadmin";
    private static final String PASSWORD = "password123";
    private static final String USER_UUID = "user-001";
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(USER_UUID, USERNAME, "hashed-" + PASSWORD,
                "13800138000", "测试公司",
                0, false, null, null, "ADMIN");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        lenient().when(loginRateLimitRepository.tryAcquire(anyString())).thenReturn(true);
        lenient().when(registerRateLimitRepository.tryAcquire(anyString())).thenReturn(true);
    }

    // ==================== Login ====================

    @Nested
    @DisplayName("登录")
    class Login {

        @Test
        @DisplayName("登录成功")
        void shouldLoginSuccessfully() {
            LoginDTO dto = new LoginDTO();
            dto.setUsername(USERNAME);
            dto.setPassword(PASSWORD);
            dto.setCaptcha("AB123");
            dto.setCaptchaToken("valid-token");

            when(captchaRepository.getAndRemove("valid-token")).thenReturn("AB123");
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(passwordHasher.matches(PASSWORD, "hashed-" + PASSWORD)).thenReturn(true);
            when(jwtUtil.generateToken(anyString(), anyString(), anyString())).thenReturn("jwt-token");

            LoginResultVO result = authService.login(dto);

            assertNotNull(result);
            assertEquals(USER_UUID, result.getUserUuid());
            assertEquals(USERNAME, result.getUsername());
            assertEquals("jwt-token", result.getToken());
            verify(userRepository).save(user);
            verify(eventBus, never()).publish(any());
        }

        @Test
        @DisplayName("密码错误 -> InvalidPasswordException")
        void shouldThrowInvalidPasswordWhenWrongPassword() {
            LoginDTO dto = new LoginDTO();
            dto.setUsername(USERNAME);
            dto.setPassword("wrong");
            dto.setCaptcha("AB123");
            dto.setCaptchaToken("valid-token");

            when(captchaRepository.getAndRemove("valid-token")).thenReturn("AB123");
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(passwordHasher.matches("wrong", "hashed-" + PASSWORD)).thenReturn(false);

            InvalidPasswordException ex = assertThrows(InvalidPasswordException.class,
                    () -> authService.login(dto));
            assertEquals(ErrorCode.INVALID_PASSWORD.getCode(), ex.getCode());
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("用户名不存在 -> 返回统一 InvalidPasswordException（防枚举）")
        void shouldReturnGenericErrorWhenUserNotFound() {
            LoginDTO dto = new LoginDTO();
            dto.setUsername("nonexistent");
            dto.setPassword("anypassword");
            dto.setCaptcha("AB123");
            dto.setCaptchaToken("valid-token");

            when(captchaRepository.getAndRemove("valid-token")).thenReturn("AB123");
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

            InvalidPasswordException ex = assertThrows(InvalidPasswordException.class,
                    () -> authService.login(dto));
            assertEquals(ErrorCode.INVALID_PASSWORD.getCode(), ex.getCode());
            assertEquals("用户名或密码错误", ex.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("连续5次密码错误触发锁定 + AccountLockedEvent")
        void shouldLockAccountAndFireEventAfterMaxFailures() {
            LoginDTO dto = new LoginDTO();
            dto.setUsername(USERNAME);
            dto.setPassword("wrong");
            dto.setCaptcha("AB123");
            dto.setCaptchaToken("valid-token");

            when(captchaRepository.getAndRemove("valid-token")).thenReturn("AB123");
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(passwordHasher.matches("wrong", "hashed-" + PASSWORD)).thenReturn(false);

            // 4 failed attempts (5th will lock)
            for (int i = 0; i < 4; i++) {
                assertThrows(InvalidPasswordException.class, () -> authService.login(dto));
            }

            // Reset mock to capture the 5th call differently
            // The 5th attempt triggers AccountLockedException
            assertThrows(AccountLockedException.class, () -> authService.login(dto));

            // Verify justLocked event was published exactly once
            ArgumentCaptor<AccountLockedEvent> eventCaptor = ArgumentCaptor.forClass(AccountLockedEvent.class);
            verify(eventBus, times(1)).publish(eventCaptor.capture());
            assertEquals(USER_UUID, eventCaptor.getValue().getUserUuid());
        }

        @Test
        @DisplayName("锁定状态下再次登录 -> AccountLockedException，且不再重复触发事件")
        void shouldNotFireEventAgainWhenAlreadyLocked() {
            // Manually lock the user
            user = new User(USER_UUID, USERNAME, "hashed-" + PASSWORD,
                    "13800138000", "测试公司",
                    5, true, java.time.LocalDateTime.now(), null, "ADMIN");

            LoginDTO dto = new LoginDTO();
            dto.setUsername(USERNAME);
            dto.setPassword(PASSWORD);
            dto.setCaptcha("AB123");
            dto.setCaptchaToken("valid-token");

            when(captchaRepository.getAndRemove("valid-token")).thenReturn("AB123");
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

            // First login attempt while locked
            assertThrows(AccountLockedException.class, () -> authService.login(dto));
            verify(eventBus, never()).publish(any(AccountLockedEvent.class));
        }

        @Test
        @DisplayName("锁定过期后自动解锁 -> 登录成功")
        void shouldAutoUnlockAfterLockExpires() {
            // Lock was set 31 minutes ago
            java.time.LocalDateTime oldLockTime = java.time.LocalDateTime.now().minusMinutes(31);
            user = new User(USER_UUID, USERNAME, "hashed-" + PASSWORD,
                    "13800138000", "测试公司",
                    5, true, oldLockTime, null, "ADMIN");

            LoginDTO dto = new LoginDTO();
            dto.setUsername(USERNAME);
            dto.setPassword(PASSWORD);
            dto.setCaptcha("AB123");
            dto.setCaptchaToken("valid-token");

            when(captchaRepository.getAndRemove("valid-token")).thenReturn("AB123");
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(passwordHasher.matches(PASSWORD, "hashed-" + PASSWORD)).thenReturn(true);
            when(jwtUtil.generateToken(anyString(), anyString(), anyString())).thenReturn("jwt-token");

            LoginResultVO result = authService.login(dto);
            assertNotNull(result);
            assertEquals(USER_UUID, result.getUserUuid());

            // User should be unlocked (locked=false, failCount=0)
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User saved = userCaptor.getValue();
            assertFalse(saved.getLocked());
            assertEquals(0, saved.getFailCount());

            verify(eventBus, never()).publish(any(AccountLockedEvent.class));
        }

        @Test
        @DisplayName("验证码错误 -> BusinessException")
        void shouldFailWhenCaptchaInvalid() {
            LoginDTO dto = new LoginDTO();
            dto.setUsername(USERNAME);
            dto.setPassword(PASSWORD);
            dto.setCaptcha("wrong");
            dto.setCaptchaToken("token-1");

            when(captchaRepository.getAndRemove("token-1")).thenReturn("ABC12");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> authService.login(dto));
            assertEquals(ErrorCode.VALIDATION_ERROR.getCode(), ex.getCode());
            verify(userRepository, never()).findByUsername(any());
        }
    }

    // ==================== Registration ====================

    @Nested
    @DisplayName("注册")
    class Register {

        @Test
        @DisplayName("注册成功")
        void shouldRegisterSuccessfully() {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername("newuser");
            dto.setPassword("pass123");
            dto.setPhone("13900000000");
            dto.setCompany("新公司");

            when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
            when(passwordHasher.hash("pass123")).thenReturn("hashed-pass123");

            authService.register(dto);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertEquals("newuser", captor.getValue().getUsername());
        }

        @Test
        @DisplayName("用户名已存在 -> BusinessException")
        void shouldFailWhenUsernameExists() {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername(USERNAME);
            dto.setPassword("pass123");

            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

            assertThrows(BusinessException.class, () -> authService.register(dto));
            verify(userRepository, never()).save(any());
        }
    }
}
