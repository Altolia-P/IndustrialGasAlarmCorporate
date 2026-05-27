package com.niit.industrialgasalarmcorporate.domain.auth;

public interface PasswordHasher {

    boolean matches(String rawPassword, String hashedPassword);

    String hash(String rawPassword);
}
