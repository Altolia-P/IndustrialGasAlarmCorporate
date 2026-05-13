package com.niit.industrialgasalarmcorporate.infrastructure.security;

import com.niit.industrialgasalarmcorporate.domain.auth.PasswordHasher;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return org.springframework.security.crypto.bcrypt.BCrypt.checkpw(rawPassword, hashedPassword);
    }

    @Override
    public String hash(String rawPassword) {
        return org.springframework.security.crypto.bcrypt.BCrypt.hashpw(
                rawPassword, org.springframework.security.crypto.bcrypt.BCrypt.gensalt());
    }
}
