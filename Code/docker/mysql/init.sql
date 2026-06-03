-- ============================================================
-- Docker 初始化脚本 — 创建数据库 + 表结构
-- 在 MySQL 容器首次启动时自动执行
-- ============================================================

CREATE DATABASE IF NOT EXISTS industrial_gas_alarm_corp
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS industrial_gas_alarm_collector
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- ============================================================
-- 主库 industrial_gas_alarm_corp
-- ============================================================
USE industrial_gas_alarm_corp;

CREATE TABLE IF NOT EXISTS t_admin_user (
    user_uuid     CHAR(36)     PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone         VARCHAR(20)  NULL,
    company       VARCHAR(200) NULL,
    fail_count    INT          NOT NULL DEFAULT 0,
    locked        TINYINT(1)   NOT NULL DEFAULT 0,
    lock_time     DATETIME     NULL,
    last_login_at DATETIME     NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'USER',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_username UNIQUE (username),
    INDEX idx_user_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_staff (
    staff_uuid    CHAR(36)     PRIMARY KEY,
    user_uuid     CHAR(36)     NULL,
    name          VARCHAR(100) NOT NULL,
    phone         VARCHAR(20)  NOT NULL,
    email         VARCHAR(100) NULL,
    role          VARCHAR(20)  NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'STANDBY',
    deleted       TINYINT(1)   NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_staff_role (role),
    INDEX idx_staff_status (status),
    INDEX idx_staff_user_uuid (user_uuid),
    CHECK (role IN ('FIELD_TECH','CUSTOMER_SERVICE','TECH_SUPPORT','AFTER_SALES')),
    CHECK (status IN ('VACATION','STANDBY','WORKING','BUSINESS_TRIP'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_device (
    device_uuid     CHAR(36)      PRIMARY KEY,
    serial_number   VARCHAR(100)  NOT NULL,
    api_token       VARCHAR(64)   NOT NULL,
    name            VARCHAR(200)  NOT NULL,
    model           VARCHAR(100)  NOT NULL,
    customer_uuid   CHAR(36)      NOT NULL,
    install_location VARCHAR(200) NULL,
    install_date    DATE          NULL,
    gas_type        VARCHAR(20)   NOT NULL,
    range_min       DECIMAL(10,4) NULL,
    range_max       DECIMAL(10,4) NULL,
    alert_threshold DECIMAL(10,4) NULL,
    status          VARCHAR(20)   NOT NULL DEFAULT 'NORMAL',
    version         INT           NOT NULL DEFAULT 0,
    deleted         TINYINT(1)    NOT NULL DEFAULT 0,
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_device_serial UNIQUE (serial_number),
    INDEX idx_device_customer (customer_uuid),
    INDEX idx_device_status (status, deleted),
    INDEX idx_device_gas_type (gas_type),
    CHECK (gas_type IN ('CH4','H2S','CO','NH3','O2','OTHER')),
    CHECK (status IN ('NORMAL','ABNORMAL','OFFLINE','MAINTENANCE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_alert_rule (
    rule_uuid            CHAR(36)     PRIMARY KEY,
    name                 VARCHAR(200) NOT NULL,
    device_uuid          CHAR(36)     NULL,
    rule_type            VARCHAR(30)  NOT NULL,
    gas_type             VARCHAR(20)  NULL,
    threshold            DECIMAL(10,4) NULL,
    duration_seconds     INT          NOT NULL DEFAULT 60,
    severity             VARCHAR(20)  NOT NULL DEFAULT 'WARNING',
    auto_create_work_order TINYINT(1) NOT NULL DEFAULT 0,
    enabled              TINYINT(1)   NOT NULL DEFAULT 1,
    version              INT          NOT NULL DEFAULT 0,
    deleted              TINYINT(1)   NOT NULL DEFAULT 0,
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_rule_device (device_uuid),
    INDEX idx_rule_type_enabled (rule_type, enabled),
    CHECK (rule_type IN ('THRESHOLD','OFFLINE','LOW_BATTERY')),
    CHECK (severity IN ('CRITICAL','WARNING','INFO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_alert (
    alert_uuid      CHAR(36)     PRIMARY KEY,
    device_uuid     CHAR(36)     NOT NULL,
    rule_uuid       CHAR(36)     NULL,
    alert_type      VARCHAR(30)  NOT NULL,
    severity        VARCHAR(20)  NOT NULL,
    concentration   DECIMAL(10,4) NULL,
    threshold       DECIMAL(10,4) NULL,
    message         TEXT         NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    triggered_at    DATETIME     NOT NULL,
    confirmed_at    DATETIME     NULL,
    confirmed_by    VARCHAR(50)  NULL,
    resolved_at     DATETIME     NULL,
    resolved_by     VARCHAR(50)  NULL,
    work_order_uuid CHAR(36)     NULL,
    version         INT          NOT NULL DEFAULT 0,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_alert_device (device_uuid),
    INDEX idx_alert_status_triggered (status, triggered_at DESC),
    INDEX idx_alert_work_order (work_order_uuid),
    INDEX idx_alert_rule (rule_uuid),
    INDEX idx_alert_type (alert_type),
    INDEX idx_alert_severity (severity),
    INDEX idx_alert_triggered_at (triggered_at),
    INDEX idx_alert_device_status (device_uuid, status),
    INDEX idx_alert_device_triggered (device_uuid, triggered_at DESC),
    CHECK (alert_type IN ('THRESHOLD','OFFLINE','LOW_BATTERY')),
    CHECK (severity IN ('CRITICAL','WARNING','INFO')),
    CHECK (status IN ('PENDING','CONFIRMED','RESOLVED','CLOSED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_notification (
    notification_uuid CHAR(36)     PRIMARY KEY,
    alert_uuid        CHAR(36)     NOT NULL,
    recipient         VARCHAR(100) NOT NULL,
    channel           VARCHAR(20)  NOT NULL,
    content           TEXT         NOT NULL,
    status            VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    retry_count       INT          NOT NULL DEFAULT 0,
    error_message     VARCHAR(500) NULL,
    sent_at           DATETIME     NULL,
    version           INT          NOT NULL DEFAULT 0,
    deleted           TINYINT(1)   NOT NULL DEFAULT 0,
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_notif_alert (alert_uuid),
    INDEX idx_notif_status (status, created_at),
    INDEX idx_notif_channel (channel),
    CHECK (channel IN ('SMS','EMAIL','IN_APP')),
    CHECK (status IN ('PENDING','SENT','DELIVERED','FAILED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_work_order (
    work_order_uuid     CHAR(36)     PRIMARY KEY,
    title               VARCHAR(200) NOT NULL,
    type                VARCHAR(20)  NOT NULL,
    description         TEXT         NOT NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    priority            VARCHAR(10)  NOT NULL DEFAULT 'MEDIUM',
    customer_name       VARCHAR(100) NOT NULL,
    customer_phone      VARCHAR(20)  NULL,
    assigned_staff_uuid CHAR(36)     NULL,
    assigned_staff_name VARCHAR(100) NULL,
    resolution          TEXT         NULL,
    completed_at        DATETIME     NULL,
    version             INT          NOT NULL DEFAULT 0,
    deleted             TINYINT(1)   NOT NULL DEFAULT 0,
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_wo_type_status (type, status),
    INDEX idx_wo_assigned (assigned_staff_uuid),
    INDEX idx_wo_status_priority (status, priority),
    INDEX idx_wo_customer_phone (customer_phone),
    INDEX idx_wo_staff_status (assigned_staff_uuid, status),
    CHECK (type IN ('TECH_SUPPORT','AFTER_SALES','ALERT')),
    CHECK (status IN ('PENDING','IN_PROGRESS','COMPLETED')),
    CHECK (priority IN ('HIGH','MEDIUM','LOW'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_category (
    category_uuid CHAR(36)     PRIMARY KEY,
    name          VARCHAR(200) NOT NULL,
    type          VARCHAR(20)  NOT NULL,
    parent_uuid   CHAR(36)     NULL,
    sort_order    INT          NOT NULL DEFAULT 0,
    deleted       TINYINT(1)   NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category_type (type),
    INDEX idx_parent_uuid (parent_uuid),
    CHECK (type IN ('PRODUCT_CATEGORY','CONTENT_CATEGORY'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_product (
    product_uuid  CHAR(36)     PRIMARY KEY,
    name          VARCHAR(200) NOT NULL,
    description   TEXT         NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    cover_image   VARCHAR(500) NULL,
    category_uuid CHAR(36)     NOT NULL,
    version       INT          NOT NULL DEFAULT 0,
    deleted       TINYINT(1)   NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_product_name_category UNIQUE (name, category_uuid, deleted),
    INDEX idx_product_category_status (category_uuid, status, deleted),
    CHECK (status IN ('DRAFT','PUBLISHED','UNPUBLISHED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_product_image (
    image_id      CHAR(36)     PRIMARY KEY,
    product_uuid  CHAR(36)     NOT NULL,
    url           VARCHAR(500) NOT NULL,
    alt_text      VARCHAR(200) NULL,
    sort_order    INT          NOT NULL DEFAULT 0,
    version       INT          NOT NULL DEFAULT 0,
    deleted       TINYINT(1)   NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_image_product (product_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_product_attribute (
    attr_id       CHAR(36)     PRIMARY KEY,
    product_uuid  CHAR(36)     NOT NULL,
    attr_key      VARCHAR(100) NOT NULL,
    attr_val      VARCHAR(500) NOT NULL,
    version       INT          NOT NULL DEFAULT 0,
    deleted       TINYINT(1)   NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_attr_product (product_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_content (
    content_uuid  CHAR(36)     PRIMARY KEY,
    title         VARCHAR(200) NOT NULL,
    summary       VARCHAR(500) NULL,
    body          MEDIUMTEXT   NULL,
    cover_image   VARCHAR(500) NULL,
    type          VARCHAR(20)  NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    category_uuid CHAR(36)     NOT NULL,
    version       INT          NOT NULL DEFAULT 0,
    deleted       TINYINT(1)   NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_content_type_status (type, status, deleted),
    INDEX idx_content_category (category_uuid),
    CHECK (status IN ('DRAFT','PUBLISHED')),
    CHECK (type IN ('SOLUTION','NEWS'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_comment (
    comment_uuid CHAR(36)     PRIMARY KEY,
    target_type  VARCHAR(30)  NOT NULL,
    target_uuid  CHAR(36)     NOT NULL,
    author_type  VARCHAR(10)  NOT NULL,
    author_uuid  CHAR(36)     NOT NULL,
    author_name  VARCHAR(50)  NOT NULL,
    content      TEXT         NOT NULL,
    version      INT          NOT NULL DEFAULT 0,
    deleted      TINYINT(1)   NOT NULL DEFAULT 0,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_comment_target (target_type, target_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_contact_message (
    message_uuid        CHAR(36)     PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    phone               VARCHAR(20)  NOT NULL,
    content             TEXT         NOT NULL,
    ip                  VARCHAR(45)  NOT NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    processor           VARCHAR(50)  NULL,
    remark              VARCHAR(500) NULL,
    assigned_staff_uuid CHAR(36)     NULL,
    assigned_staff_name VARCHAR(100) NULL,
    submitted_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at        DATETIME     NULL,
    version             INT          NOT NULL DEFAULT 0,
    deleted             TINYINT(1)   NOT NULL DEFAULT 0,
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_msg_status (status, deleted, submitted_at DESC),
    INDEX idx_msg_phone (phone, submitted_at),
    INDEX idx_msg_ip (ip, submitted_at),
    INDEX idx_msg_assigned_staff (assigned_staff_uuid),
    CHECK (status IN ('PENDING','IN_PROGRESS','PROCESSED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_download_file (
    download_uuid CHAR(36)     PRIMARY KEY,
    display_name  VARCHAR(200) NOT NULL,
    original_name VARCHAR(200) NOT NULL,
    file_size     BIGINT       NOT NULL DEFAULT 0,
    content_type  VARCHAR(100) NOT NULL DEFAULT 'application/octet-stream',
    stored_path   VARCHAR(500) NOT NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_system_config (
    config_key   VARCHAR(100) PRIMARY KEY,
    config_value TEXT         NOT NULL,
    description  VARCHAR(200) NULL,
    version      INT          NOT NULL DEFAULT 0,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_operation_log (
    log_id           CHAR(36)     PRIMARY KEY,
    operator_uuid    CHAR(36)     NOT NULL,
    operator_name    VARCHAR(50)  NOT NULL,
    operation        VARCHAR(30)  NOT NULL,
    target_type      VARCHAR(30)  NOT NULL,
    target_id        CHAR(36)     NOT NULL,
    target_name      VARCHAR(200) NULL,
    detail           JSON         NULL,
    business_purpose VARCHAR(500) NULL,
    ip               VARCHAR(45)  NULL,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_log_operator (operator_uuid),
    INDEX idx_log_target (target_type, target_id),
    INDEX idx_log_created_at (created_at),
    INDEX idx_log_operation (operation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_event_outbox (
    event_id       CHAR(36)     PRIMARY KEY,
    aggregate_type VARCHAR(30)  NOT NULL,
    aggregate_id   CHAR(36)     NOT NULL,
    event_type     VARCHAR(50)  NOT NULL,
    payload        JSON         NOT NULL,
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    retry_count    INT          NOT NULL DEFAULT 0,
    error_msg      TEXT         NULL,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at        DATETIME     NULL,
    INDEX idx_outbox_status_created (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 采集器库 industrial_gas_alarm_collector
-- ============================================================
USE industrial_gas_alarm_collector;

CREATE TABLE IF NOT EXISTS t_device_data_point (
    data_point_id  CHAR(36)      PRIMARY KEY,
    device_uuid    CHAR(36)      NOT NULL,
    recorded_at    DATETIME      NOT NULL,
    concentration  DECIMAL(10,4) NULL,
    battery        DECIMAL(5,2)  NULL,
    temperature    DECIMAL(5,2)  NULL,
    humidity       DECIMAL(5,2)  NULL,
    signal_strength INT          NULL,
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_dp_device_time (device_uuid, recorded_at DESC),
    INDEX idx_dp_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 插入默认管理员账号 (admin / admin123)
-- ============================================================
USE industrial_gas_alarm_corp;

INSERT IGNORE INTO t_admin_user (user_uuid, username, password_hash, role, company)
VALUES (
    'admin-00000000000000000000000001',
    'admin',
    -- BCrypt hash of 'admin123'
    '$2a$10$XzoULx0wAHzIj7ITj6IEhuqxaC1yyGFlApbx1q1ezjJV95HXQ8t3S',
    'ADMIN',
    '系统管理'
);
