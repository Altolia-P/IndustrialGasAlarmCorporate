-- ============================================================
-- 项目：工业气体报警企业官网系统 (Industrial Gas Alarm Corporate)
-- 数据库：MySQL 8.0
-- 字符集：utf8mb4
-- 存储引擎：InnoDB
-- 主键策略：UUID v4 字符串，CHAR(36)
-- 设计依据：《类图设计文档（最终版）》《架构设计文档 V1.4》《前端数据模型》
-- 版本：V1.3
-- ============================================================

CREATE DATABASE IF NOT EXISTS industrial_gas_alarm_corp
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE industrial_gas_alarm_corp;

-- -----------------------------------------------------------
-- 1. 分类表 (t_category)
-- -----------------------------------------------------------
CREATE TABLE t_category (
    category_uuid CHAR(36) PRIMARY KEY COMMENT '分类唯一标识',
    name          VARCHAR(200) NOT NULL COMMENT '分类名称',
    type          VARCHAR(20)  NOT NULL COMMENT '分类类型：PRODUCT_CATEGORY / CONTENT_CATEGORY',
    parent_uuid   CHAR(36)     NULL COMMENT '父分类ID',
    sort_order    INT          NOT NULL DEFAULT 0 COMMENT '排序序号',
    version       INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    deleted       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category_type (type),
    INDEX idx_parent_uuid (parent_uuid),
    CHECK (type IN ('PRODUCT_CATEGORY','CONTENT_CATEGORY'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- -----------------------------------------------------------
-- 2. 产品表 (t_product)
-- -----------------------------------------------------------
CREATE TABLE t_product (
    product_uuid  CHAR(36)  PRIMARY KEY COMMENT '产品唯一标识',
    name          VARCHAR(200) NOT NULL COMMENT '产品名称',
    description   TEXT         NULL COMMENT '产品描述',
    status        VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT / PUBLISHED / UNPUBLISHED',
    cover_image   VARCHAR(500) NULL COMMENT '封面图片URL',
    category_uuid CHAR(36)  NOT NULL COMMENT '所属分类ID',
    version       INT        NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    deleted       TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at    DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_product_name_category UNIQUE (name, category_uuid, deleted),
    INDEX idx_product_category_status (category_uuid, status, deleted),
    CHECK (status IN ('DRAFT','PUBLISHED','UNPUBLISHED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- -----------------------------------------------------------
-- 3. 产品图片表 (t_product_image)
-- -----------------------------------------------------------
CREATE TABLE t_product_image (
    image_id      CHAR(36)     PRIMARY KEY COMMENT '图片唯一标识（UUID）',
    product_uuid  CHAR(36)     NOT NULL COMMENT '所属产品ID',
    url           VARCHAR(500) NOT NULL COMMENT '图片URL',
    alt_text      VARCHAR(200) NULL COMMENT '替代文本',
    sort_order    INT          NOT NULL DEFAULT 0 COMMENT '排序序号',
    version       INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    deleted       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_image_product_uuid (product_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品图片表';

-- -----------------------------------------------------------
-- 4. 产品属性表 (t_product_attribute)
-- -----------------------------------------------------------
CREATE TABLE t_product_attribute (
    attr_id       CHAR(36)     PRIMARY KEY COMMENT '属性唯一标识（UUID）',
    product_uuid  CHAR(36)     NOT NULL COMMENT '所属产品ID',
    attr_key      VARCHAR(100) NOT NULL COMMENT '属性键',
    attr_val      VARCHAR(500) NOT NULL COMMENT '属性值',
    version       INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    deleted       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_attr_product_uuid (product_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品属性表';

-- -----------------------------------------------------------
-- 5. 内容表 (t_content) — 解决方案/新闻
-- -----------------------------------------------------------
CREATE TABLE t_content (
    content_uuid  CHAR(36)  PRIMARY KEY COMMENT '内容唯一标识',
    title         VARCHAR(200) NOT NULL COMMENT '标题',
    summary       VARCHAR(500) NULL COMMENT '摘要',
    body          MEDIUMTEXT   NULL COMMENT '正文富文本',
    cover_image   VARCHAR(500) NULL COMMENT '封面图片URL',
    type          VARCHAR(20)  NOT NULL COMMENT 'SOLUTION / NEWS',
    status        VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT / PUBLISHED',
    category_uuid CHAR(36)  NOT NULL COMMENT '所属分类ID',
    version       INT        NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    deleted       TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at    DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_content_type_status (type, status, deleted),
    INDEX idx_content_category (category_uuid),
    CHECK (status IN ('DRAFT','PUBLISHED')),
    CHECK (type IN ('SOLUTION','NEWS'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容表（解决方案/新闻）';

-- -----------------------------------------------------------
-- 6. 留言表 (t_contact_message)
--     V1.3: status 增加 IN_PROGRESS（PENDING→IN_PROGRESS→PROCESSED）
--           processor 替换为 assigned_staff_uuid + assigned_staff_name
-- -----------------------------------------------------------
CREATE TABLE t_contact_message (
    message_uuid        CHAR(36)     PRIMARY KEY COMMENT '留言唯一标识',
    name                VARCHAR(100) NOT NULL COMMENT '联系人姓名',
    phone               VARCHAR(20)  NOT NULL COMMENT '联系电话',
    content             TEXT         NOT NULL COMMENT '需求描述',
    ip                  VARCHAR(45)  NOT NULL COMMENT '提交IP',
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING / IN_PROGRESS / PROCESSED',
    assigned_staff_uuid CHAR(36)     NULL COMMENT '指派员工UUID',
    assigned_staff_name VARCHAR(100) NULL COMMENT '指派员工姓名（冗余）',
    remark              VARCHAR(500) NULL COMMENT '处理备注',
    version             INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    submitted_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    processed_at        DATETIME     NULL COMMENT '处理完成时间',
    deleted             TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_msg_status_submitted (status, deleted, submitted_at DESC),
    INDEX idx_msg_phone_submitted (phone, submitted_at),
    INDEX idx_msg_ip_submitted (ip, submitted_at),
    INDEX idx_msg_assigned_staff (assigned_staff_uuid),
    CHECK (status IN ('PENDING','IN_PROGRESS','PROCESSED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户留言表';

-- -----------------------------------------------------------
-- 7. 用户表 (t_admin_user)
--     V1.3: 增加 phone、company 字段，支持客户注册
--           role 支持 ADMIN / STAFF / USER
-- -----------------------------------------------------------
CREATE TABLE t_admin_user (
    user_uuid     CHAR(36)     PRIMARY KEY COMMENT '用户唯一标识',
    username      VARCHAR(50)  NOT NULL COMMENT '登录用户名',
    password_hash VARCHAR(255) NOT NULL COMMENT 'BCrypt密码哈希',
    phone         VARCHAR(20)  NULL COMMENT '手机号',
    company       VARCHAR(200) NULL COMMENT '公司名称',
    fail_count    INT          NOT NULL DEFAULT 0 COMMENT '连续失败次数',
    locked        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否锁定',
    lock_time     DATETIME     NULL COMMENT '锁定时间',
    last_login_at DATETIME     NULL COMMENT '最后登录时间',
    role          VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN / STAFF / USER',
    version       INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_username UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- -----------------------------------------------------------
-- 8. 员工表 (t_staff) — V1.3 新增
--     管理员分配员工，员工负责处理留言和工单
-- -----------------------------------------------------------
CREATE TABLE t_staff (
    staff_uuid CHAR(36)     PRIMARY KEY COMMENT '员工唯一标识',
    name       VARCHAR(100) NOT NULL COMMENT '员工姓名',
    phone      VARCHAR(20)  NOT NULL COMMENT '联系电话',
    email      VARCHAR(100) NULL COMMENT '邮箱',
    role       VARCHAR(20)  NOT NULL COMMENT 'FIELD_TECH / CUSTOMER_SERVICE / TECH_SUPPORT / AFTER_SALES',
    status     VARCHAR(20)  NOT NULL DEFAULT 'STANDBY' COMMENT 'VACATION / STANDBY / WORKING / BUSINESS_TRIP',
    version    INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    deleted    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_staff_role (role),
    INDEX idx_staff_status (status),
    CHECK (role IN ('FIELD_TECH','CUSTOMER_SERVICE','TECH_SUPPORT','AFTER_SALES')),
    CHECK (status IN ('VACATION','STANDBY','WORKING','BUSINESS_TRIP'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- -----------------------------------------------------------
-- 9. 工单表 (t_work_order) — V1.3 新增
--     客户提交工单，管理员分配员工处理
-- -----------------------------------------------------------
CREATE TABLE t_work_order (
    work_order_uuid     CHAR(36)     PRIMARY KEY COMMENT '工单唯一标识',
    title               VARCHAR(200) NOT NULL COMMENT '工单标题',
    type                VARCHAR(20)  NOT NULL COMMENT 'TECH_SUPPORT / AFTER_SALES',
    description         TEXT         NOT NULL COMMENT '工单描述',
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING / IN_PROGRESS / COMPLETED',
    priority            VARCHAR(10)  NOT NULL DEFAULT 'MEDIUM' COMMENT 'HIGH / MEDIUM / LOW',
    customer_name       VARCHAR(100) NOT NULL COMMENT '客户名称',
    customer_phone      VARCHAR(20)  NULL COMMENT '客户电话',
    assigned_staff_uuid CHAR(36)     NULL COMMENT '指派员工UUID',
    assigned_staff_name VARCHAR(100) NULL COMMENT '指派员工姓名（冗余）',
    resolution          TEXT         NULL COMMENT '处理结果',
    version             INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    deleted             TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_workorder_type_status (type, status),
    INDEX idx_workorder_assigned_staff (assigned_staff_uuid),
    INDEX idx_workorder_status_priority (status, priority),
    CHECK (type IN ('TECH_SUPPORT','AFTER_SALES')),
    CHECK (status IN ('PENDING','IN_PROGRESS','COMPLETED')),
    CHECK (priority IN ('HIGH','MEDIUM','LOW'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表';

-- -----------------------------------------------------------
-- 10. 操作日志表 (t_operation_log)
-- -----------------------------------------------------------
CREATE TABLE t_operation_log (
    log_id        CHAR(36)     PRIMARY KEY COMMENT '日志唯一标识（UUID）',
    operator_uuid CHAR(36)     NOT NULL COMMENT '操作人UUID',
    operator_name VARCHAR(50)  NOT NULL COMMENT '操作人用户名（冗余，避免 JOIN）',
    operation     VARCHAR(30)  NOT NULL COMMENT '操作类型：CREATE/UPDATE/DELETE/PUBLISH/UNPUBLISH/PROCESS',
    target_type   VARCHAR(30)  NOT NULL COMMENT '操作对象：PRODUCT/CONTENT/CATEGORY/MESSAGE/USER/STAFF/WORKORDER',
    target_id     CHAR(36)     NOT NULL COMMENT '操作对象UUID',
    target_name   VARCHAR(200) NULL COMMENT '操作对象名称（冗余，便于日志检索）',
    detail        JSON         NULL COMMENT '操作详情（变更前后对比，JSON格式）',
    ip            VARCHAR(45)  NULL COMMENT '操作人IP',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_log_operator (operator_uuid),
    INDEX idx_log_target (target_type, target_id),
    INDEX idx_log_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- -----------------------------------------------------------
-- 11. 系统配置表 (t_system_config)
-- -----------------------------------------------------------
CREATE TABLE t_system_config (
    config_key    VARCHAR(100) PRIMARY KEY COMMENT '配置键（如 site_name, contact_email, contact_phone）',
    config_value  TEXT         NOT NULL COMMENT '配置值',
    description   VARCHAR(200) NULL COMMENT '配置说明',
    version       INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- -----------------------------------------------------------
-- 12. 事件发件箱表 (t_event_outbox)
-- -----------------------------------------------------------
CREATE TABLE t_event_outbox (
    event_id       CHAR(36)     PRIMARY KEY COMMENT '事件唯一标识（UUID）',
    aggregate_type VARCHAR(30)  NOT NULL COMMENT '聚合根类型：PRODUCT/CONTENT/MESSAGE/USER/STAFF/WORKORDER',
    aggregate_id   CHAR(36)     NOT NULL COMMENT '聚合根UUID',
    event_type     VARCHAR(50)  NOT NULL COMMENT '事件类型：ProductPublished/MessageSubmitted等',
    payload        JSON         NOT NULL COMMENT '事件载荷（JSON格式）',
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING / SENT / FAILED',
    retry_count    INT          NOT NULL DEFAULT 0 COMMENT '重试次数',
    error_msg      TEXT         NULL COMMENT '最后一次失败原因',
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '事件创建时间',
    sent_at        DATETIME     NULL COMMENT '发送成功时间',
    INDEX idx_outbox_status_created (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件发件箱表（微服务可靠消息投递）';

-- ============================================================
-- 示例数据 (Seed Data)
-- 所有示例账号密码均为 123456（BCrypt 已加密）
-- ============================================================

-- 13.1 管理员用户（后台管理）
--     用户名: admin  密码: 123456  角色: ADMIN
INSERT INTO t_admin_user (user_uuid, username, password_hash, phone, company, fail_count, locked, role, version)
VALUES ('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'admin',
        '$2b$10$2x4FO0ZjqAWpggCW.jbiYO7o1CRn3wiu2z0.v7f5VnwgUTuv4jOBK',
        '13800000001', '气体报警科技有限公司', 0, 0, 'ADMIN', 0);

-- 13.2 员工用户（员工后台）
--     用户名: staff  密码: 123456  角色: STAFF
INSERT INTO t_admin_user (user_uuid, username, password_hash, phone, company, fail_count, locked, role, version)
VALUES ('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'staff',
        '$2b$10$6iIJzG3G.FTxYyh2dLHZfu5WRuCPn0OQy2Dg7vwpHi3UKICTJwuXC',
        '13800000002', '气体报警科技有限公司', 0, 0, 'STAFF', 0);

-- 13.3 普通用户（前台注册客户）
--     用户名: user  密码: 123456  角色: USER
INSERT INTO t_admin_user (user_uuid, username, password_hash, phone, company, fail_count, locked, role, version)
VALUES ('c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', 'user',
        '$2b$10$bv/aEFIOlT3vIsY9dQJ.euL5IUNXejKP0QRa15oeAu7anC0Mfqgne',
        '13900000001', '中化集团', 0, 0, 'USER', 0);

-- 13.4 员工档案（关联 staff 登录账号，用于留言和工单指派）
--     对应登录账号: staff (b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e)
INSERT INTO t_staff (staff_uuid, name, phone, email, role, status, version)
VALUES ('d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a', '张工',
        '13800000002', 'zhang@example.com',
        'TECH_SUPPORT', 'WORKING', 0);

-- ============================================================
-- 示例数据说明
-- ┌──────────┬──────────┬──────────┬──────────────────────────┐
-- │ 用户名    │ 密码      │ 角色      │ 前端入口                  │
-- ├──────────┼──────────┼──────────┼──────────────────────────┤
-- │ admin    │ 123456   │ ADMIN    │ /admin → 后台管理         │
-- │ staff    │ 123456   │ STAFF    │ /staff → 员工后台         │
-- │ user     │ 123456   │ USER     │ /user  → 客户中心         │
-- └──────────┴──────────┴──────────┴──────────────────────────┘
-- ============================================================
