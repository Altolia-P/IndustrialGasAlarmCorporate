package com.niit.industrialgasalarmcorporate.domain.auth;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private static final int MAX_FAIL_COUNT = 5;
    private static final int LOCK_MINUTES = 30;

    private final String userUuid;
    private final String username;
    private String passwordHash;
    private String phone;
    private String company;
    private int failCount;
    private boolean locked;
    private LocalDateTime lockTime;
    private LocalDateTime lastLoginAt;
    private String role;

    public User(String username, String passwordHash, String role) {
        this.userUuid = UUID.randomUUID().toString();
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.failCount = 0;
        this.locked = false;
    }

    public User(String username, String passwordHash, String phone, String company, String role) {
        this(username, passwordHash, role);
        this.phone = phone;
        this.company = company;
    }

    public User(String userUuid, String username, String passwordHash, String phone, String company,
                int failCount, boolean locked, LocalDateTime lockTime, LocalDateTime lastLoginAt, String role) {
        this.userUuid = userUuid;
        this.username = username;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.company = company;
        this.failCount = failCount;
        this.locked = locked;
        this.lockTime = lockTime;
        this.lastLoginAt = lastLoginAt;
        this.role = role;
    }

    public LoginResult login(String rawPassword, PasswordHasher passwordHasher) {
        if (isLocked()) {
            return LoginResult.failed("账号已锁定，请30分钟后重试");
        }
        if (!passwordHasher.matches(rawPassword, this.passwordHash)) {
            incrementFailCount();
            int remaining = MAX_FAIL_COUNT - this.failCount;
            if (remaining <= 0) {
                return LoginResult.failed("账号已锁定，请30分钟后重试");
            }
            return LoginResult.failed("用户名或密码错误，剩余尝试次数：" + remaining);
        }
        this.failCount = 0;
        this.lastLoginAt = LocalDateTime.now();
        return LoginResult.success();
    }

    public boolean isLocked() {
        if (!this.locked) {
            return false;
        }
        if (this.lockTime != null && this.lockTime.plusMinutes(LOCK_MINUTES).isBefore(LocalDateTime.now())) {
            this.locked = false;
            this.failCount = 0;
            this.lockTime = null;
            return false;
        }
        return true;
    }

    public void changePassword(String newRawPassword, PasswordHasher passwordHasher) {
        this.passwordHash = passwordHasher.hash(newRawPassword);
    }

    private void incrementFailCount() {
        this.failCount++;
        if (this.failCount >= MAX_FAIL_COUNT) {
            this.locked = true;
            this.lockTime = LocalDateTime.now();
        }
    }

    public boolean justLocked() {
        return this.locked && this.failCount == MAX_FAIL_COUNT;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int getFailCount() {
        return failCount;
    }

    public boolean getLocked() {
        return locked;
    }

    public LocalDateTime getLockTime() {
        return lockTime;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public String getPhone() {
        return phone;
    }

    public String getCompany() {
        return company;
    }

    public String getRole() {
        return role;
    }
}
