-- ============================================================
-- 工业气体报警企业系统 — 数据库建库脚本 V3.0
-- 数据库：MySQL 8.0 | 字符集：utf8mb4 | 主键：UUID v4 CHAR(36)
-- 生成依据：当前 17 个 app 模块 PO 类 + 1 个基础设施表
-- 生成日期：2026-06-03
-- ============================================================

DROP DATABASE IF EXISTS industrial_gas_alarm_corp;
CREATE DATABASE industrial_gas_alarm_corp
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE industrial_gas_alarm_corp;

-- ============================================================
-- 数据库：industrial_gas_alarm_corp（主库 — app 微服务）
-- ============================================================

-- ============================================================
-- 1. 管理员用户表 (t_admin_user)
-- PO: UserPO — 无 @Version, 无 @TableLogic
-- ============================================================
CREATE TABLE t_admin_user (
    user_uuid     CHAR(36)     PRIMARY KEY COMMENT 'UUID v4',
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL COMMENT 'BCrypt',
    phone         VARCHAR(20)  NULL,
    company       VARCHAR(200) NULL,
    fail_count    INT          NOT NULL DEFAULT 0,
    locked        TINYINT(1)   NOT NULL DEFAULT 0,
    lock_time     DATETIME     NULL,
    last_login_at DATETIME     NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT 'ADMIN / STAFF / USER',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_username UNIQUE (username),
    INDEX idx_user_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员用户表';

-- ============================================================
-- 2. 员工表 (t_staff)
-- PO: StaffPO — 无 department, 无 @Version; 有 @TableLogic + user_uuid
-- ============================================================
CREATE TABLE t_staff (
    staff_uuid    CHAR(36)     PRIMARY KEY,
    user_uuid     CHAR(36)     NULL COMMENT '关联用户UUID',
    name          VARCHAR(100) NOT NULL,
    phone         VARCHAR(20)  NOT NULL,
    email         VARCHAR(100) NULL,
    role          VARCHAR(20)  NOT NULL COMMENT 'FIELD_TECH/CUSTOMER_SERVICE/TECH_SUPPORT/AFTER_SALES',
    status        VARCHAR(20)  NOT NULL DEFAULT 'STANDBY' COMMENT 'VACATION/STANDBY/WORKING/BUSINESS_TRIP',
    deleted       TINYINT(1)   NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_staff_role (role),
    INDEX idx_staff_status (status),
    INDEX idx_staff_user_uuid (user_uuid),
    CHECK (role IN ('FIELD_TECH','CUSTOMER_SERVICE','TECH_SUPPORT','AFTER_SALES')),
    CHECK (status IN ('VACATION','STANDBY','WORKING','BUSINESS_TRIP'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- ============================================================
-- 3. 设备表 (t_device)
-- PO: DevicePO — 有 @Version, @TableLogic, customerUuid NOT NULL
-- ============================================================
CREATE TABLE t_device (
    device_uuid     CHAR(36)      PRIMARY KEY,
    serial_number   VARCHAR(100)  NOT NULL,
    api_token       VARCHAR(64)   NOT NULL COMMENT 'API Key SHA256',
    name            VARCHAR(200)  NOT NULL,
    model           VARCHAR(100)  NOT NULL,
    customer_uuid   CHAR(36)      NOT NULL,
    install_location VARCHAR(200) NULL,
    install_date    DATE          NULL,
    gas_type        VARCHAR(20)   NOT NULL COMMENT 'CH4/H2S/CO/NH3/O2/OTHER',
    range_min       DECIMAL(10,4) NULL,
    range_max       DECIMAL(10,4) NULL,
    alert_threshold DECIMAL(10,4) NULL,
    status          VARCHAR(20)   NOT NULL DEFAULT 'NORMAL' COMMENT 'NORMAL/ABNORMAL/OFFLINE/MAINTENANCE',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

-- ============================================================
-- 4. 告警规则表 (t_alert_rule)
-- PO: AlertRulePO — 有 @Version, @TableLogic; autoCreateWorkOrder 为 Integer
-- ============================================================
CREATE TABLE t_alert_rule (
    rule_uuid            CHAR(36)     PRIMARY KEY,
    name                 VARCHAR(200) NOT NULL,
    device_uuid          CHAR(36)     NULL COMMENT 'NULL=全局规则',
    rule_type            VARCHAR(30)  NOT NULL COMMENT 'THRESHOLD/OFFLINE/LOW_BATTERY',
    gas_type             VARCHAR(20)  NULL,
    threshold            DECIMAL(10,4) NULL,
    duration_seconds     INT          NOT NULL DEFAULT 60 COMMENT '持续超过N秒才触发',
    severity             VARCHAR(20)  NOT NULL DEFAULT 'WARNING' COMMENT 'CRITICAL/WARNING/INFO',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警规则表';

-- ============================================================
-- 5. 告警表 (t_alert)
-- PO: AlertPO — 有 @Version, @TableLogic
-- ============================================================
CREATE TABLE t_alert (
    alert_uuid      CHAR(36)     PRIMARY KEY,
    device_uuid     CHAR(36)     NOT NULL,
    rule_uuid       CHAR(36)     NULL,
    alert_type      VARCHAR(30)  NOT NULL COMMENT 'THRESHOLD/OFFLINE/LOW_BATTERY',
    severity        VARCHAR(20)  NOT NULL COMMENT 'CRITICAL/WARNING/INFO',
    concentration   DECIMAL(10,4) NULL,
    threshold       DECIMAL(10,4) NULL,
    message         TEXT         NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/CONFIRMED/RESOLVED/CLOSED',
    triggered_at    DATETIME     NOT NULL,
    confirmed_at    DATETIME     NULL,
    confirmed_by    VARCHAR(50)  NULL COMMENT '确认人UUID',
    resolved_at     DATETIME     NULL,
    resolved_by     VARCHAR(50)  NULL COMMENT '解决人UUID',
    work_order_uuid CHAR(36)     NULL COMMENT '关联工单UUID',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';

-- ============================================================
-- 6. 通知表 (t_notification)
-- PO: NotificationPO — 有 @Version, @TableLogic
-- ============================================================
CREATE TABLE t_notification (
    notification_uuid CHAR(36)     PRIMARY KEY,
    alert_uuid        CHAR(36)     NOT NULL,
    recipient         VARCHAR(100) NOT NULL COMMENT '手机号/邮箱',
    channel           VARCHAR(20)  NOT NULL COMMENT 'SMS/EMAIL/IN_APP',
    content           TEXT         NOT NULL,
    status            VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/SENT/DELIVERED/FAILED',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知记录表';

-- ============================================================
-- 7. 工单表 (t_work_order)
-- PO: WorkOrderPO — 有 @Version, @TableLogic
-- ============================================================
CREATE TABLE t_work_order (
    work_order_uuid     CHAR(36)     PRIMARY KEY,
    title               VARCHAR(200) NOT NULL,
    type                VARCHAR(20)  NOT NULL COMMENT 'TECH_SUPPORT/AFTER_SALES/ALERT',
    description         TEXT         NOT NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/IN_PROGRESS/COMPLETED',
    priority            VARCHAR(10)  NOT NULL DEFAULT 'MEDIUM' COMMENT 'HIGH/MEDIUM/LOW',
    customer_name       VARCHAR(100) NOT NULL,
    customer_phone      VARCHAR(20)  NULL,
    assigned_staff_uuid CHAR(36)     NULL,
    assigned_staff_name VARCHAR(100) NULL,
    resolution          TEXT         NULL,
    completed_at        DATETIME     NULL COMMENT '完成时间',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表';

-- ============================================================
-- 8. 分类表 (t_category)
-- PO: CategoryPO — 无 @Version; 有 @TableLogic
-- ============================================================
CREATE TABLE t_category (
    category_uuid CHAR(36)     PRIMARY KEY,
    name          VARCHAR(200) NOT NULL,
    type          VARCHAR(20)  NOT NULL COMMENT 'PRODUCT_CATEGORY/CONTENT_CATEGORY',
    parent_uuid   CHAR(36)     NULL,
    sort_order    INT          NOT NULL DEFAULT 0,
    deleted       TINYINT(1)   NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category_type (type),
    INDEX idx_parent_uuid (parent_uuid),
    CHECK (type IN ('PRODUCT_CATEGORY','CONTENT_CATEGORY'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- ============================================================
-- 9. 产品表 (t_product)
-- PO: ProductPO — 有 @Version, @TableLogic
-- ============================================================
CREATE TABLE t_product (
    product_uuid  CHAR(36)     PRIMARY KEY,
    name          VARCHAR(200) NOT NULL,
    description   TEXT         NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/PUBLISHED/UNPUBLISHED',
    cover_image   VARCHAR(500) NULL,
    category_uuid CHAR(36)     NOT NULL,
    version       INT          NOT NULL DEFAULT 0,
    deleted       TINYINT(1)   NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_product_name_category UNIQUE (name, category_uuid, deleted),
    INDEX idx_product_category_status (category_uuid, status, deleted),
    CHECK (status IN ('DRAFT','PUBLISHED','UNPUBLISHED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- ============================================================
-- 10. 产品图片表 (t_product_image)
-- PO: ProductImagePO — 有 @Version, @TableLogic; PK 为 imageId
-- ============================================================
CREATE TABLE t_product_image (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品图片表';

-- ============================================================
-- 11. 产品属性表 (t_product_attribute)
-- PO: ProductAttributePO — 有 @Version, @TableLogic; PK 为 attrId
-- ============================================================
CREATE TABLE t_product_attribute (
    attr_id       CHAR(36)     PRIMARY KEY,
    product_uuid  CHAR(36)     NOT NULL,
    attr_key      VARCHAR(100) NOT NULL,
    attr_val      VARCHAR(500) NOT NULL,
    version       INT          NOT NULL DEFAULT 0,
    deleted       TINYINT(1)   NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_attr_product (product_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品属性表';

-- ============================================================
-- 12. 内容表 (t_content)
-- PO: ContentPO — 有 @Version, @TableLogic
-- ============================================================
CREATE TABLE t_content (
    content_uuid  CHAR(36)     PRIMARY KEY,
    title         VARCHAR(200) NOT NULL,
    summary       VARCHAR(500) NULL,
    body          MEDIUMTEXT   NULL,
    cover_image   VARCHAR(500) NULL,
    type          VARCHAR(20)  NOT NULL COMMENT 'SOLUTION/NEWS',
    status        VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/PUBLISHED',
    category_uuid CHAR(36)     NOT NULL,
    version       INT          NOT NULL DEFAULT 0,
    deleted       TINYINT(1)   NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_content_type_status (type, status, deleted),
    INDEX idx_content_category (category_uuid),
    CHECK (status IN ('DRAFT','PUBLISHED')),
    CHECK (type IN ('SOLUTION','NEWS'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容表（解决方案/新闻）';

-- ============================================================
-- 13. 评论表 (t_comment)
-- PO: CommentPO — 有 @Version, @TableLogic
-- ============================================================
CREATE TABLE t_comment (
    comment_uuid CHAR(36)     PRIMARY KEY,
    target_type  VARCHAR(30)  NOT NULL COMMENT 'PRODUCT/CONTENT/WORK_ORDER',
    target_uuid  CHAR(36)     NOT NULL,
    author_type  VARCHAR(10)  NOT NULL COMMENT 'ADMIN/STAFF/CUSTOMER',
    author_uuid  CHAR(36)     NOT NULL,
    author_name  VARCHAR(50)  NOT NULL,
    content      TEXT         NOT NULL,
    version      INT          NOT NULL DEFAULT 0,
    deleted      TINYINT(1)   NOT NULL DEFAULT 0,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_comment_target (target_type, target_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ============================================================
-- 14. 客户留言表 (t_contact_message)
-- PO: ContactMessagePO — 有 @Version, @TableLogic; 有 assignedStaffUuid/Name
-- ============================================================
CREATE TABLE t_contact_message (
    message_uuid        CHAR(36)     PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    phone               VARCHAR(20)  NOT NULL,
    content             TEXT         NOT NULL,
    ip                  VARCHAR(45)  NOT NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/IN_PROGRESS/PROCESSED',
    processor           VARCHAR(50)  NULL COMMENT '处理人',
    remark              VARCHAR(500) NULL COMMENT '处理备注',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户留言表';

-- ============================================================
-- 15. 下载文件表 (t_download_file)
-- PO: DownloadFilePO — 无 @Version, 无 @TableLogic, 无 updatedAt
-- ============================================================
CREATE TABLE t_download_file (
    download_uuid CHAR(36)     PRIMARY KEY,
    display_name  VARCHAR(200) NOT NULL COMMENT '展示名称',
    original_name VARCHAR(200) NOT NULL COMMENT '原始文件名',
    file_size     BIGINT       NOT NULL DEFAULT 0,
    content_type  VARCHAR(100) NOT NULL DEFAULT 'application/octet-stream',
    stored_path   VARCHAR(500) NOT NULL COMMENT '存储路径',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='下载文件表';

-- ============================================================
-- 16. 系统配置表 (t_system_config)
-- PO: SystemConfigPO — 有 @Version; 无 @TableLogic, 无 createdAt
-- ============================================================
CREATE TABLE t_system_config (
    config_key   VARCHAR(100) PRIMARY KEY,
    config_value TEXT         NOT NULL,
    description  VARCHAR(200) NULL,
    version      INT          NOT NULL DEFAULT 0,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ============================================================
-- 17. 操作日志表 (t_operation_log)
-- PO: OperationLogPO — 无 @Version, 无 @TableLogic, 无 updatedAt
-- ============================================================
CREATE TABLE t_operation_log (
    log_id           CHAR(36)     PRIMARY KEY,
    operator_uuid    CHAR(36)     NOT NULL,
    operator_name    VARCHAR(50)  NOT NULL,
    operation        VARCHAR(30)  NOT NULL COMMENT 'CREATE/UPDATE/DELETE/PUBLISH/UNPUBLISH/PROCESS',
    target_type      VARCHAR(30)  NOT NULL COMMENT 'PRODUCT/CONTENT/CATEGORY/MESSAGE/USER/STAFF/WORKORDER',
    target_id        CHAR(36)     NOT NULL,
    target_name      VARCHAR(200) NULL,
    detail           JSON         NULL,
    business_purpose VARCHAR(500) NULL COMMENT '业务目的',
    ip               VARCHAR(45)  NULL,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_log_operator (operator_uuid),
    INDEX idx_log_target (target_type, target_id),
    INDEX idx_log_created_at (created_at),
    INDEX idx_log_operation (operation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ============================================================
-- 18. 事件发件箱表 (t_event_outbox)
-- 注：无对应 PO 类，由基础设施层直接管理
-- ============================================================
CREATE TABLE t_event_outbox (
    event_id       CHAR(36)     PRIMARY KEY,
    aggregate_type VARCHAR(30)  NOT NULL COMMENT 'PRODUCT/CONTENT/MESSAGE/USER/STAFF/WORKORDER',
    aggregate_id   CHAR(36)     NOT NULL,
    event_type     VARCHAR(50)  NOT NULL,
    payload        JSON         NOT NULL,
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/SENT/FAILED',
    retry_count    INT          NOT NULL DEFAULT 0,
    error_msg      TEXT         NULL,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at        DATETIME     NULL,
    INDEX idx_outbox_status_created (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件发件箱表（可靠消息投递）';
