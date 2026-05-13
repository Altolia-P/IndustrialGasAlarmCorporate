package com.niit.industrialgasalarmcorporate.infrastructure.config;

import com.niit.industrialgasalarmcorporate.domain.auth.PasswordHasher;
import com.niit.industrialgasalarmcorporate.domain.auth.User;
import com.niit.industrialgasalarmcorporate.domain.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    private static final String DEFAULT_ADMIN = "admin";
    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public void run(String... args) {
        ensureTables();
        ensureAdminUser();
    }

    private void ensureTables() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS t_admin_user (
                    user_uuid     CHAR(36)     PRIMARY KEY,
                    username      VARCHAR(50)  NOT NULL UNIQUE,
                    password_hash VARCHAR(255) NOT NULL,
                    phone         VARCHAR(20)  NULL,
                    company       VARCHAR(200) NULL,
                    fail_count    INT          NOT NULL DEFAULT 0,
                    locked        TINYINT(1)   NOT NULL DEFAULT 0,
                    lock_time     DATETIME     NULL,
                    last_login_at DATETIME     NULL,
                    role          VARCHAR(20)  NOT NULL DEFAULT 'USER',
                    version       INT          NOT NULL DEFAULT 0,
                    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);
        log.info("数据库表初始化完成");
    }

    private void ensureAdminUser() {
        if (userRepository.findByUsername(DEFAULT_ADMIN).isPresent()) {
            log.info("管理员账号已存在，跳过初始化");
            return;
        }
        User admin = new User(DEFAULT_ADMIN, passwordHasher.hash(DEFAULT_PASSWORD), "ADMIN");
        userRepository.save(admin);
        log.info("已创建默认管理员账号: admin / <password hidden>");
    }
}
