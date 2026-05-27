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
                    image_id     CHAR(36)     PRIMARY KEY,
                    product_uuid CHAR(36)     NOT NULL,
                    url          VARCHAR(500) NOT NULL,
                    alt_text     VARCHAR(200) NULL,
                    sort_order   INT          NOT NULL DEFAULT 0,
                    version      INT          NOT NULL DEFAULT 0,
                    deleted      TINYINT(1)   NOT NULL DEFAULT 0,
                    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS t_product_attribute (
                    attr_id      CHAR(36)     PRIMARY KEY,
                    product_uuid CHAR(36)     NOT NULL,
                    attr_key     VARCHAR(100) NOT NULL,
                    attr_val     VARCHAR(500) NOT NULL,
                    version      INT          NOT NULL DEFAULT 0,
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
                    message_uuid        CHAR(36)     PRIMARY KEY,
                    name                VARCHAR(100) NOT NULL,
                    phone               VARCHAR(20)  NOT NULL,
                    content             TEXT         NOT NULL,
                    ip                  VARCHAR(45)  NOT NULL,
                    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
                    assigned_staff_uuid CHAR(36)     NULL,
                    assigned_staff_name VARCHAR(100) NULL,
                    processor           VARCHAR(50)  NULL,
                    remark              VARCHAR(500) NULL,
                    submitted_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    processed_at        DATETIME     NULL,
                    version             INT          NOT NULL DEFAULT 0,
                    deleted             TINYINT(1)   NOT NULL DEFAULT 0,
                    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS t_staff (
                    staff_uuid  CHAR(36)     PRIMARY KEY,
                    name        VARCHAR(50)  NOT NULL,
                    phone       VARCHAR(20)  NOT NULL,
                    email       VARCHAR(100) NULL,
                    role        VARCHAR(30)  NOT NULL,
                    status      VARCHAR(20)  NOT NULL DEFAULT 'STANDBY',
                    deleted     TINYINT(1)   NOT NULL DEFAULT 0,
                    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS t_work_order (
                    work_order_uuid       CHAR(36)     PRIMARY KEY,
                    title                 VARCHAR(200) NOT NULL,
                    type                  VARCHAR(30)  NOT NULL DEFAULT 'TECH_SUPPORT',
                    description           TEXT         NULL,
                    status                VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
                    priority              VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM',
                    assigned_staff_uuid   CHAR(36)     NULL,
                    assigned_staff_name   VARCHAR(50)  NULL,
                    customer_name         VARCHAR(50)  NULL,
                    customer_phone        VARCHAR(20)  NULL,
                    resolution            VARCHAR(500) NULL,
                    completed_at          DATETIME     NULL,
                    version               INT          NOT NULL DEFAULT 0,
                    deleted               TINYINT(1)   NOT NULL DEFAULT 0,
                    created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS t_device (
                    device_uuid      CHAR(36)     PRIMARY KEY,
                    serial_number    VARCHAR(100) NOT NULL,
                    name             VARCHAR(200) NOT NULL,
                    model            VARCHAR(100) NOT NULL,
                    customer_uuid    CHAR(36)     NOT NULL,
                    install_location VARCHAR(300) NULL,
                    install_date     DATE         NULL,
                    gas_type         VARCHAR(20)  NOT NULL,
                    range_min        DECIMAL(10,4) NULL,
                    range_max        DECIMAL(10,4) NULL,
                    alert_threshold  DECIMAL(10,4) NULL,
                    status           VARCHAR(20)  NOT NULL DEFAULT 'NORMAL',
                    version          INT          NOT NULL DEFAULT 0,
                    deleted          TINYINT(1)   NOT NULL DEFAULT 0,
                    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS t_device_data_point (
                    data_point_id   CHAR(36)     PRIMARY KEY,
                    device_uuid     CHAR(36)     NOT NULL,
                    recorded_at     DATETIME     NOT NULL,
                    concentration   DECIMAL(10,4) NOT NULL,
                    battery         DECIMAL(10,4) NULL,
                    temperature     DECIMAL(10,4) NULL,
                    humidity        DECIMAL(10,4) NULL,
                    signal_strength INT          NULL DEFAULT 0,
                    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS t_alert_rule (
                    rule_uuid              CHAR(36)     PRIMARY KEY,
                    name                   VARCHAR(200) NOT NULL,
                    device_uuid            CHAR(36)     NULL,
                    rule_type              VARCHAR(30)  NOT NULL,
                    gas_type               VARCHAR(20)  NULL,
                    threshold              DECIMAL(10,4) NULL,
                    duration_seconds       INT          NOT NULL DEFAULT 60,
                    severity               VARCHAR(20)  NOT NULL DEFAULT 'WARNING',
                    auto_create_work_order TINYINT(1)   NOT NULL DEFAULT 0,
                    enabled                TINYINT(1)   NOT NULL DEFAULT 1,
                    version                INT          NOT NULL DEFAULT 0,
                    deleted                TINYINT(1)   NOT NULL DEFAULT 0,
                    created_at             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS t_alert (
                    alert_uuid     CHAR(36)     PRIMARY KEY,
                    device_uuid    CHAR(36)     NOT NULL,
                    rule_uuid      CHAR(36)     NULL,
                    alert_type     VARCHAR(30)  NOT NULL,
                    severity       VARCHAR(20)  NOT NULL DEFAULT 'WARNING',
                    concentration  DECIMAL(10,4) NULL,
                    threshold      DECIMAL(10,4) NULL,
                    message        VARCHAR(500) NULL,
                    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
                    triggered_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    confirmed_at   DATETIME     NULL,
                    confirmed_by   VARCHAR(50)  NULL,
                    resolved_at    DATETIME     NULL,
                    resolved_by    VARCHAR(50)  NULL,
                    work_order_uuid CHAR(36)    NULL,
                    version        INT          NOT NULL DEFAULT 0,
                    deleted        TINYINT(1)   NOT NULL DEFAULT 0,
                    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS t_notification (
                    notification_uuid CHAR(36)     PRIMARY KEY,
                    alert_uuid        CHAR(36)     NOT NULL,
                    recipient         VARCHAR(100) NOT NULL,
                    channel           VARCHAR(20)  NOT NULL DEFAULT 'IN_APP',
                    content           TEXT         NULL,
                    status            VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
                    retry_count       INT          NOT NULL DEFAULT 0,
                    error_message     VARCHAR(500) NULL,
                    sent_at           DATETIME     NULL,
                    version           INT          NOT NULL DEFAULT 0,
                    deleted           TINYINT(1)   NOT NULL DEFAULT 0,
                    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        log.info("数据库表初始化完成 (14 tables)");
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
