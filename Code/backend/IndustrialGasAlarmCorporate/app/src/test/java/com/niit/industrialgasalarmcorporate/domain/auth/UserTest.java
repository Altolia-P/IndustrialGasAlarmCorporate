package com.niit.industrialgasalarmcorporate.domain.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("User 领域实体 — 登录锁定逻辑")
class UserTest {

    @Mock
    private PasswordHasher passwordHasher;

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "correctPass";
    private static final String PASSWORD_HASH = "hashed-correctPass";

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(USERNAME, PASSWORD_HASH, "ADMIN");
    }

    @Nested
    @DisplayName("正常登录")
    class NormalLogin {

        @Test
        @DisplayName("密码正确 -> 登录成功")
        void shouldSucceedWithCorrectPassword() {
            when(passwordHasher.matches(PASSWORD, PASSWORD_HASH)).thenReturn(true);

            LoginResult result = user.login(PASSWORD, passwordHasher);

            assertTrue(result.isSuccess());
            assertNull(result.getReason());
            assertEquals(0, user.getFailCount());
            assertFalse(user.getLocked());
            assertFalse(user.justLocked());
        }

        @Test
        @DisplayName("密码错误 -> 登录失败 + failCount 递增")
        void shouldFailWithWrongPassword() {
            when(passwordHasher.matches(anyString(), anyString())).thenReturn(false);

            LoginResult result = user.login("wrong", passwordHasher);

            assertFalse(result.isSuccess());
            assertEquals(1, user.getFailCount());
            assertFalse(user.getLocked());
            assertFalse(user.justLocked());
        }
    }

    @Nested
    @DisplayName("锁定机制")
    class LockMechanism {

        @Test
        @DisplayName("5 次密码错误后账户锁定")
        void shouldLockAfterMaxFailures() {
            when(passwordHasher.matches(anyString(), anyString())).thenReturn(false);

            for (int i = 1; i <= 4; i++) {
                LoginResult r = user.login("wrong", passwordHasher);
                assertFalse(r.isSuccess());
                assertEquals(i, user.getFailCount());
                assertFalse(user.getLocked());
                assertFalse(user.justLocked());
            }

            // 5th attempt — locks the account
            LoginResult r = user.login("wrong", passwordHasher);
            assertFalse(r.isSuccess());
            assertEquals(5, user.getFailCount());
            assertTrue(user.getLocked());
            assertNotNull(user.getLockTime());
            assertTrue(user.justLocked()); // justLocked returns true on first check

            // justLocked should return false after being consumed
            assertFalse(user.justLocked());
        }

        @Test
        @DisplayName("锁定后登录 -> 直接返回锁定消息, failCount 不再递增")
        void shouldReturnLockedMessageWhenAlreadyLocked() {
            // Lock the user via repeated wrong attempts to properly set the state
            when(passwordHasher.matches(anyString(), anyString())).thenReturn(false);
            for (int i = 0; i < 5; i++) {
                user.login("wrong", passwordHasher);
            }
            // consume justLocked flag
            user.justLocked();

            assertEquals(5, user.getFailCount());
            assertTrue(user.getLocked());

            // Now try to login again while locked
            LoginResult r = user.login("wrong", passwordHasher);
            assertFalse(r.isSuccess());
            assertTrue(r.getReason().contains("锁定"));
            assertEquals(5, user.getFailCount()); // no increment
            assertFalse(user.justLocked());       // no event
        }

        @Test
        @DisplayName("锁定过期后自动解锁")
        void shouldAutoUnlockAfterLockExpires() {
            // Use full constructor to set a lock that expired 31 minutes ago
            LocalDateTime oldLockTime = LocalDateTime.now().minusMinutes(31);
            user = new User(java.util.UUID.randomUUID().toString(), USERNAME, PASSWORD_HASH,
                    "", "", 5, true, oldLockTime, null, "ADMIN");

            // isLocked() should auto-unlock
            assertFalse(user.isLocked());
            assertFalse(user.getLocked());
            assertEquals(0, user.getFailCount());
            assertNull(user.getLockTime());
        }

        @Test
        @DisplayName("锁定未过期时 isLocked 返回 true")
        void shouldRemainLockedBeforeExpiry() {
            LocalDateTime recentLockTime = LocalDateTime.now().minusMinutes(15);
            user = new User(java.util.UUID.randomUUID().toString(), USERNAME, PASSWORD_HASH,
                    "", "", 5, true, recentLockTime, null, "ADMIN");

            assertTrue(user.isLocked());
            assertTrue(user.getLocked());
        }

        @Test
        @DisplayName("failCount 并发 cap — 最多不超过 MAX_FAIL_COUNT + 1")
        void shouldCapFailCount() {
            when(passwordHasher.matches(anyString(), anyString())).thenReturn(false);

            // Call login 10 times with wrong password;
            // after the 5th call the account is locked, subsequent calls
            // hit isLocked() early and don't call incrementFailCount()
            for (int i = 0; i < 10; i++) {
                user.login("wrong", passwordHasher);
            }

            // failCount is capped at MAX_FAIL_COUNT + 1 = 6
            assertTrue(user.getFailCount() <= 6);
            assertTrue(user.getLocked());
        }

        @Test
        @DisplayName("justLocked 是瞬时的 — DB 重建的实例不会触发")
        void shouldResetJustLockedOnNewInstance() {
            when(passwordHasher.matches(anyString(), anyString())).thenReturn(false);

            // Lock the account
            for (int i = 0; i < 5; i++) {
                user.login("wrong", passwordHasher);
            }
            assertTrue(user.justLocked()); // consumed

            // Simulate reloading from DB — transient field defaults to false
            User freshUser = new User(user.getUserUuid(), USERNAME, PASSWORD_HASH,
                    "", "", 5, true, user.getLockTime(), null, "ADMIN");

            assertFalse(freshUser.justLocked());
        }
    }

    @Nested
    @DisplayName("failCount 并发安全")
    class FailCountConcurrency {

        @Test
        @DisplayName("failCount 最大不超过 MAX_FAIL_COUNT + 1")
        void shouldNotExceedMaxPlusOne() {
            when(passwordHasher.matches(anyString(), anyString())).thenReturn(false);

            // Login 10 times — after 5th the account is locked,
            // subsequent logins are rejected by isLocked() before incrementFailCount
            for (int i = 0; i < 10; i++) {
                user.login("wrong", passwordHasher);
            }

            assertTrue(user.getFailCount() <= 6, "failCount should not exceed MAX_FAIL_COUNT + 1");
        }
    }
}
