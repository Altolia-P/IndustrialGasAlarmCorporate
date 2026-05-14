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

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS t_product (
                    product_uuid CHAR(36)     PRIMARY KEY,
                    name         VARCHAR(200) NOT NULL,
                    description  TEXT         NULL,
                    status       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
                    cover_image  VARCHAR(500) NULL,
                    category_uuid CHAR(36)    NULL,
                    version      INT          NOT NULL DEFAULT 0,
                    deleted      TINYINT(1)   NOT NULL DEFAULT 0,
                    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS t_product_image (
                    image_id     BIGINT       AUTO_INCREMENT PRIMARY KEY,
                    product_uuid CHAR(36)     NOT NULL,
                    url          VARCHAR(500) NOT NULL,
                    alt_text     VARCHAR(200) NULL,
                    sort_order   INT          NOT NULL DEFAULT 0,
                    deleted      TINYINT(1)   NOT NULL DEFAULT 0,
                    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS t_product_attribute (
                    attr_id      BIGINT       AUTO_INCREMENT PRIMARY KEY,
                    product_uuid CHAR(36)     NOT NULL,
                    attr_key     VARCHAR(100) NOT NULL,
                    attr_val     VARCHAR(500) NULL,
                    deleted      TINYINT(1)   NOT NULL DEFAULT 0,
                    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS t_content (
                    content_uuid  CHAR(36)     PRIMARY KEY,
                    title         VARCHAR(200) NOT NULL,
                    summary       VARCHAR(500) NULL,
                    body          TEXT         NULL,
                    cover_image   VARCHAR(500) NULL,
                    type          VARCHAR(20)  NOT NULL,
                    status        VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
                    category_uuid CHAR(36)     NULL,
                    version       INT          NOT NULL DEFAULT 0,
                    deleted       TINYINT(1)   NOT NULL DEFAULT 0,
                    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS t_category (
                    category_uuid CHAR(36)     PRIMARY KEY,
                    name          VARCHAR(100) NOT NULL,
                    type          VARCHAR(30)  NOT NULL,
                    parent_uuid   CHAR(36)     NULL,
                    sort_order    INT          NOT NULL DEFAULT 0,
                    deleted       TINYINT(1)   NOT NULL DEFAULT 0,
                    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS t_contact_message (
                    message_uuid CHAR(36)     PRIMARY KEY,
                    name         VARCHAR(50)  NOT NULL,
                    phone        VARCHAR(20)  NOT NULL,
                    content      TEXT         NOT NULL,
                    ip           VARCHAR(45)  NULL,
                    status       VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
                    processor    VARCHAR(50)  NULL,
                    remark       VARCHAR(500) NULL,
                    submitted_at DATETIME     NULL,
                    processed_at DATETIME     NULL,
                    version      INT          NOT NULL DEFAULT 0,
                    deleted      TINYINT(1)   NOT NULL DEFAULT 0,
                    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        log.info("数据库表初始化完成 (7 tables)");
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
