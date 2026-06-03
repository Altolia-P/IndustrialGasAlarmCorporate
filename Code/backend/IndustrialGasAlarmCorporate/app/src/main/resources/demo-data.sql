-- ================================================================
-- InterSense 工业气体报警 — 演示数据 V3.0
-- 对齐 Gas-ToB-SQL V3.0 schema（无 version 列的表已移除该字段）
-- 所有演示账号密码：123456（BCrypt 已加密）
-- 运行方式：在 MySQL 中执行此文件，或通过 DataInitializer 加载
-- ================================================================

-- ================================================================
-- 清理已有演示数据（保留非 demo- 前缀的正式数据）
-- ================================================================
DELETE FROM t_comment WHERE comment_uuid LIKE 'demo-%';
DELETE FROM t_notification WHERE notification_uuid LIKE 'demo-%';
DELETE FROM t_alert WHERE alert_uuid LIKE 'demo-%';
DELETE FROM t_alert_rule WHERE rule_uuid LIKE 'demo-%';
DELETE FROM t_device WHERE device_uuid LIKE 'demo-%';
DELETE FROM t_work_order WHERE work_order_uuid LIKE 'demo-%';
DELETE FROM t_admin_user WHERE user_uuid LIKE 'demo-%';
DELETE FROM t_staff WHERE staff_uuid LIKE 'demo-%';
DELETE FROM t_contact_message WHERE message_uuid LIKE 'demo-%';
DELETE FROM t_product_attribute WHERE product_uuid LIKE 'demo-%';
DELETE FROM t_product_image WHERE product_uuid LIKE 'demo-%';
DELETE FROM t_product WHERE product_uuid LIKE 'demo-%';
DELETE FROM t_content WHERE content_uuid LIKE 'demo-%';
DELETE FROM t_category WHERE category_uuid LIKE 'demo-%';
DELETE FROM t_download_file WHERE download_uuid LIKE 'demo-%';

-- ================================================================
-- 0. 系统基础数据（正式账号 + 员工，非 demo 前缀）
--    账号密码均为 123456
-- ================================================================

-- 管理员（后台管理入口 /admin）
INSERT IGNORE INTO t_admin_user (user_uuid, username, password_hash, phone, company, fail_count, locked, role)
VALUES ('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'admin',
        '$2b$10$2x4FO0ZjqAWpggCW.jbiYO7o1CRn3wiu2z0.v7f5VnwgUTuv4jOBK',
        '13800000001', 'InterSense 英森思科技有限公司', 0, 0, 'ADMIN');

-- 员工（员工后台 /staff，关联 staff 档案）
INSERT IGNORE INTO t_admin_user (user_uuid, username, password_hash, phone, company, fail_count, locked, role)
VALUES ('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'staff',
        '$2b$10$6iIJzG3G.FTxYyh2dLHZfu5WRuCPn0OQy2Dg7vwpHi3UKICTJwuXC',
        '13800000002', 'InterSense 英森思科技有限公司', 0, 0, 'STAFF');

-- 普通客户（客户中心 /user）
INSERT IGNORE INTO t_admin_user (user_uuid, username, password_hash, phone, company, fail_count, locked, role)
VALUES ('c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', 'user',
        '$2b$10$bv/aEFIOlT3vIsY9dQJ.euL5IUNXejKP0QRa15oeAu7anC0Mfqgne',
        '13900000001', '中化集团', 0, 0, 'USER');

-- 员工档案（关联 staff 登录账号）
INSERT IGNORE INTO t_staff (staff_uuid, name, phone, email, role, status)
VALUES ('d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a', '张工',
        '13800000002', 'zhang@intersense.com',
        'TECH_SUPPORT', 'WORKING');

-- ================================================================
-- 账号速查
-- ┌──────────┬──────────┬──────────┬──────────────────────────┐
-- │ 用户名    │ 密码      │ 角色      │ 前端入口                  │
-- ├──────────┼──────────┼──────────┼──────────────────────────┤
-- │ admin    │ 123456   │ ADMIN    │ /admin → 后台管理         │
-- │ staff    │ 123456   │ STAFF    │ /staff → 员工后台         │
-- │ user     │ 123456   │ USER     │ /user  → 客户中心         │
-- │ demo     │ 123456   │ USER     │ /user  → 演示客户         │
-- │ zhangsan │ 123456   │ USER     │ /user  → 演示客户         │
-- │ lisi     │ 123456   │ USER     │ /user  → 演示客户         │
-- └──────────┴──────────┴──────────┴──────────────────────────┘
-- ================================================================

-- ================================================================
-- 1. 分类 (t_category)
-- ================================================================
INSERT INTO t_category (category_uuid, name, type, parent_uuid, sort_order) VALUES
-- 产品分类
('demo-cat-001', '固定式气体检测仪', 'PRODUCT_CATEGORY', NULL, 1),
('demo-cat-002', '便携式气体检测仪', 'PRODUCT_CATEGORY', NULL, 2),
('demo-cat-003', '气体报警控制器',   'PRODUCT_CATEGORY', NULL, 3),
('demo-cat-004', '气体传感器模块',   'PRODUCT_CATEGORY', NULL, 4),
('demo-cat-005', '配件与耗材',       'PRODUCT_CATEGORY', NULL, 5),
-- 内容分类（解决方案）
('demo-cat-101', '石油化工安全', 'CONTENT_CATEGORY', NULL, 1),
('demo-cat-102', '冶金钢铁安全', 'CONTENT_CATEGORY', NULL, 2),
('demo-cat-103', '能源电力安全', 'CONTENT_CATEGORY', NULL, 3),
('demo-cat-104', '市政燃气安全', 'CONTENT_CATEGORY', NULL, 4),
-- 内容分类（新闻）
('demo-cat-201', '公司动态',     'CONTENT_CATEGORY', NULL, 1),
('demo-cat-202', '行业资讯',     'CONTENT_CATEGORY', NULL, 2),
('demo-cat-203', '技术前沿',     'CONTENT_CATEGORY', NULL, 3);

-- ================================================================
-- 2. 产品 (t_product + t_product_image + t_product_attribute)
-- ================================================================

-- 产品 1：固定式可燃气体检测仪
INSERT INTO t_product (product_uuid, name, description, status, cover_image, category_uuid) VALUES
('demo-prd-001', 'SenseGuard F-800 固定式可燃气体检测仪',
 'SenseGuard F-800 是新一代工业级固定式可燃气体检测仪，采用催化燃烧+红外双传感器冗余设计，可在高温高湿恶劣环境下长期稳定运行。支持 4-20mA / RS485 / HART 多种输出方式，内置声光报警，防护等级 IP66，防爆等级 Ex d IIC T6，适用于石油炼化、化工储运、天然气站等场所。配备自动校准和传感器寿命预测功能，大幅降低维护成本。',
 'PUBLISHED', '/uploads/demo/product-f800.jpg', 'demo-cat-001');

INSERT INTO t_product_image (image_id, product_uuid, url, alt_text, sort_order) VALUES
('demo-img-001-a', 'demo-prd-001', '/uploads/demo/product-f800-01.jpg', 'F-800 正面视图', 1),
('demo-img-001-b', 'demo-prd-001', '/uploads/demo/product-f800-02.jpg', 'F-800 安装场景', 2),
('demo-img-001-c', 'demo-prd-001', '/uploads/demo/product-f800-03.jpg', 'F-800 接线端子', 3);

INSERT INTO t_product_attribute (attr_id, product_uuid, attr_key, attr_val) VALUES
('demo-attr-001-a', 'demo-prd-001', '检测气体', '可燃气体（甲烷/丙烷/氢气等）'),
('demo-attr-001-b', 'demo-prd-001', '检测原理', '催化燃烧 + 红外（双传感器）'),
('demo-attr-001-c', 'demo-prd-001', '量程', '0-100% LEL'),
('demo-attr-001-d', 'demo-prd-001', '响应时间', 'T90 ≤ 15s'),
('demo-attr-001-e', 'demo-prd-001', '输出信号', '4-20mA / RS485 / HART'),
('demo-attr-001-f', 'demo-prd-001', '防爆等级', 'Ex d IIC T6'),
('demo-attr-001-g', 'demo-prd-001', '防护等级', 'IP66'),
('demo-attr-001-h', 'demo-prd-001', '工作温度', '-40℃ ~ +70℃'),
('demo-attr-001-i', 'demo-prd-001', '电源', '24VDC'),
('demo-attr-001-j', 'demo-prd-001', '质保', '3年');

-- 产品 2：固定式有毒气体检测仪
INSERT INTO t_product (product_uuid, name, description, status, cover_image, category_uuid) VALUES
('demo-prd-002', 'SenseGuard F-600 固定式有毒气体检测仪',
 'SenseGuard F-600 采用高精度电化学传感器，可检测 CO、H₂S、SO₂、NH₃、Cl₂ 等多种有毒有害气体。内置温湿度补偿算法，零点漂移极小，特别适合化工管道阀门区、污水处理站、制药车间等需要高精度检测的场所。支持传感器热插拔更换，现场维护仅需 30 秒。',
 'PUBLISHED', '/uploads/demo/product-f600.jpg', 'demo-cat-001');

INSERT INTO t_product_image (image_id, product_uuid, url, alt_text, sort_order) VALUES
('demo-img-002-a', 'demo-prd-002', '/uploads/demo/product-f600-01.jpg', 'F-600 正面视图', 1),
('demo-img-002-b', 'demo-prd-002', '/uploads/demo/product-f600-02.jpg', 'F-600 传感器模块', 2);

INSERT INTO t_product_attribute (attr_id, product_uuid, attr_key, attr_val) VALUES
('demo-attr-002-a', 'demo-prd-002', '检测气体', 'CO / H₂S / SO₂ / NH₃ / Cl₂（可选）'),
('demo-attr-002-b', 'demo-prd-002', '检测原理', '电化学'),
('demo-attr-002-c', 'demo-prd-002', '量程', 'CO: 0-1000ppm, H₂S: 0-100ppm'),
('demo-attr-002-d', 'demo-prd-002', '分辨率', 'CO: 1ppm, H₂S: 0.1ppm'),
('demo-attr-002-e', 'demo-prd-002', '响应时间', 'T90 ≤ 30s'),
('demo-attr-002-f', 'demo-prd-002', '输出信号', '4-20mA / RS485'),
('demo-attr-002-g', 'demo-prd-002', '防爆等级', 'Ex d IIC T6'),
('demo-attr-002-h', 'demo-prd-002', '防护等级', 'IP65'),
('demo-attr-002-i', 'demo-prd-002', '工作温度', '-20℃ ~ +55℃'),
('demo-attr-002-j', 'demo-prd-002', '电源', '24VDC'),
('demo-attr-002-k', 'demo-prd-002', '质保', '2年');

-- 产品 3：便携式多气体检测仪
INSERT INTO t_product (product_uuid, name, description, status, cover_image, category_uuid) VALUES
('demo-prd-003', 'SenseGuard P-200 便携式多气体检测仪',
 'SenseGuard P-200 是一款四合一便携式气体检测仪，可同时检测可燃气体(LEL)、氧气(O₂)、一氧化碳(CO)、硫化氢(H₂S)。整机仅重 280g，配备 2.4 寸彩屏和 LED 声光震动三重报警，通过 USB-C 充电，续航长达 18 小时。内置数据记录器可存储 10 万条数据，支持蓝牙上传至手机 APP。广泛用于受限空间作业、管道巡检、应急救援等场景。',
 'PUBLISHED', '/uploads/demo/product-p200.jpg', 'demo-cat-002');

INSERT INTO t_product_image (image_id, product_uuid, url, alt_text, sort_order) VALUES
('demo-img-003-a', 'demo-prd-003', '/uploads/demo/product-p200-01.jpg', 'P-200 正面', 1),
('demo-img-003-b', 'demo-prd-003', '/uploads/demo/product-p200-02.jpg', 'P-200 佩戴示意', 2),
('demo-img-003-c', 'demo-prd-003', '/uploads/demo/product-p200-03.jpg', 'P-200 APP 界面', 3);

INSERT INTO t_product_attribute (attr_id, product_uuid, attr_key, attr_val) VALUES
('demo-attr-003-a', 'demo-prd-003', '检测气体', 'LEL / O₂ / CO / H₂S（四合一）'),
('demo-attr-003-b', 'demo-prd-003', '检测原理', '催化燃烧 + 电化学'),
('demo-attr-003-c', 'demo-prd-003', '量程', 'LEL: 0-100%, O₂: 0-30%, CO: 0-1000ppm, H₂S: 0-100ppm'),
('demo-attr-003-d', 'demo-prd-003', '报警方式', '声(95dB) / 光(LED) / 震动 三重报警'),
('demo-attr-003-e', 'demo-prd-003', '续航时间', '≥ 18 小时'),
('demo-attr-003-f', 'demo-prd-003', '数据存储', '10 万条（支持蓝牙导出）'),
('demo-attr-003-g', 'demo-prd-003', '防护等级', 'IP67'),
('demo-attr-003-h', 'demo-prd-003', '重量', '280g'),
('demo-attr-003-i', 'demo-prd-003', '充电方式', 'USB-C，支持快充'),
('demo-attr-003-j', 'demo-prd-003', '防爆等级', 'Ex ia IIC T4'),
('demo-attr-003-k', 'demo-prd-003', '工作温度', '-20℃ ~ +50℃'),
('demo-attr-003-l', 'demo-prd-003', '质保', '2年');

-- 产品 4：便携式单气体检测仪
INSERT INTO t_product (product_uuid, name, description, status, cover_image, category_uuid) VALUES
('demo-prd-004', 'SenseGuard P-100 便携式单气体检测仪',
 'SenseGuard P-100 是一款超紧凑型个人单气体检测仪，仅重 120g，夹在衣领即可工作。专注于单一气体类型的检测，可选 CO、H₂S、O₂、SO₂ 等多种传感器型号。操作极其简单——开机即用，无需校准，2 年免维护。IP68 防水防尘，适合一线工人日常佩戴。',
 'PUBLISHED', '/uploads/demo/product-p100.jpg', 'demo-cat-002');

INSERT INTO t_product_image (image_id, product_uuid, url, alt_text, sort_order) VALUES
('demo-img-004-a', 'demo-prd-004', '/uploads/demo/product-p100-01.jpg', 'P-100 正面', 1),
('demo-img-004-b', 'demo-prd-004', '/uploads/demo/product-p100-02.jpg', 'P-100 佩戴示意', 2);

INSERT INTO t_product_attribute (attr_id, product_uuid, attr_key, attr_val) VALUES
('demo-attr-004-a', 'demo-prd-004', '检测气体', 'CO / H₂S / O₂ / SO₂（可选单气体）'),
('demo-attr-004-b', 'demo-prd-004', '检测原理', '电化学'),
('demo-attr-004-c', 'demo-prd-004', '量程', '视传感器型号'),
('demo-attr-004-d', 'demo-prd-004', '报警方式', '声(95dB) / 光(LED) / 震动'),
('demo-attr-004-e', 'demo-prd-004', '续航时间', '≥ 2 年（不可充电电池）'),
('demo-attr-004-f', 'demo-prd-004', '防护等级', 'IP68'),
('demo-attr-004-g', 'demo-prd-004', '重量', '120g'),
('demo-attr-004-h', 'demo-prd-004', '维护周期', '2 年免维护'),
('demo-attr-004-i', 'demo-prd-004', '防爆等级', 'Ex ia IIC T4'),
('demo-attr-004-j', 'demo-prd-004', '工作温度', '-30℃ ~ +50℃'),
('demo-attr-004-k', 'demo-prd-004', '质保', '2年');

-- 产品 5：气体报警控制器（旗舰）
INSERT INTO t_product (product_uuid, name, description, status, cover_image, category_uuid) VALUES
('demo-prd-005', 'SenseAlarm C-5000 气体报警控制器',
 'SenseAlarm C-5000 是旗舰级多通道气体报警控制器，支持最多 128 路检测器接入。配备 10.1 寸工业触摸屏，实时显示各通道浓度、报警状态和趋势曲线。支持 RS485 总线组网，内置 Web Server 可远程监控。具有三级报警联动输出（预报警/报警/紧急），可驱动风机、电磁阀、消防系统。适用于大型化工厂、储罐区、综合管廊等需要集中监控的场景。',
 'PUBLISHED', '/uploads/demo/product-c5000.jpg', 'demo-cat-003');

INSERT INTO t_product_image (image_id, product_uuid, url, alt_text, sort_order) VALUES
('demo-img-005-a', 'demo-prd-005', '/uploads/demo/product-c5000-01.jpg', 'C-5000 正面面板', 1),
('demo-img-005-b', 'demo-prd-005', '/uploads/demo/product-c5000-02.jpg', 'C-5000 机柜安装', 2);

INSERT INTO t_product_attribute (attr_id, product_uuid, attr_key, attr_val) VALUES
('demo-attr-005-a', 'demo-prd-005', '通道数', '最大 128 路'),
('demo-attr-005-b', 'demo-prd-005', '显示屏', '10.1 寸工业触摸屏'),
('demo-attr-005-c', 'demo-prd-005', '输入信号', '4-20mA / RS485'),
('demo-attr-005-d', 'demo-prd-005', '报警输出', '三级联动（预报警/报警/紧急），8 路继电器'),
('demo-attr-005-e', 'demo-prd-005', '通讯接口', 'RS485 / 以太网 / 4G（可选）'),
('demo-attr-005-f', 'demo-prd-005', '数据存储', '≥ 1 年历史数据'),
('demo-attr-005-g', 'demo-prd-005', '远程监控', '内置 Web Server + 手机 APP'),
('demo-attr-005-h', 'demo-prd-005', '电源', '220VAC ±15%'),
('demo-attr-005-i', 'demo-prd-005', '工作温度', '-10℃ ~ +55℃'),
('demo-attr-005-j', 'demo-prd-005', '质保', '3年');

-- 产品 6：区域报警控制器
INSERT INTO t_product (product_uuid, name, description, status, cover_image, category_uuid) VALUES
('demo-prd-006', 'SenseAlarm C-3000 区域报警控制器',
 'SenseAlarm C-3000 是面向中小型场所的 16 通道气体报警控制器，性价比极高。采用 7 寸触摸屏，操作直观，支持即插即用的检测器自动识别。标配 RS485 上行接口，可接入 C-5000 或第三方 SCADA 系统。内置备用电池可在断电后继续工作 4 小时。适用于小型化工厂、实验室、冷库、加油站等场所。',
 'PUBLISHED', '/uploads/demo/product-c3000.jpg', 'demo-cat-003');

INSERT INTO t_product_image (image_id, product_uuid, url, alt_text, sort_order) VALUES
('demo-img-006-a', 'demo-prd-006', '/uploads/demo/product-c3000-01.jpg', 'C-3000 正面面板', 1);

INSERT INTO t_product_attribute (attr_id, product_uuid, attr_key, attr_val) VALUES
('demo-attr-006-a', 'demo-prd-006', '通道数', '16 路（可扩展至 32 路）'),
('demo-attr-006-b', 'demo-prd-006', '显示屏', '7 寸触摸屏'),
('demo-attr-006-c', 'demo-prd-006', '输入信号', '4-20mA / RS485'),
('demo-attr-006-d', 'demo-prd-006', '报警输出', '三级联动，4 路继电器'),
('demo-attr-006-e', 'demo-prd-006', '通讯接口', 'RS485 / 以太网'),
('demo-attr-006-f', 'demo-prd-006', '备用电池', '内置，续航 4 小时'),
('demo-attr-006-g', 'demo-prd-006', '电源', '220VAC ±15%'),
('demo-attr-006-h', 'demo-prd-006', '工作温度', '-10℃ ~ +55℃'),
('demo-attr-006-i', 'demo-prd-006', '质保', '2年');

-- 产品 7：甲烷传感器模块
INSERT INTO t_product (product_uuid, name, description, status, cover_image, category_uuid) VALUES
('demo-prd-007', 'SenseSensor MQ-4 甲烷传感器模块',
 'SenseSensor MQ-4 是专为工业气体检测设备设计的甲烷催化燃烧传感器模块，输出标准 4-20mA 信号，可直接对接各类报警控制器和 PLC。灵敏度高、零点稳定、寿命长达 5 年，年漂移量控制在 ±2% 以内。模块化封装，支持热插拔，方便现场快速更换。',
 'PUBLISHED', '/uploads/demo/product-mq4.jpg', 'demo-cat-004');

INSERT INTO t_product_image (image_id, product_uuid, url, alt_text, sort_order) VALUES
('demo-img-007-a', 'demo-prd-007', '/uploads/demo/product-mq4-01.jpg', 'MQ-4 传感器模块', 1);

INSERT INTO t_product_attribute (attr_id, product_uuid, attr_key, attr_val) VALUES
('demo-attr-007-a', 'demo-prd-007', '检测气体', '甲烷 (CH₄) / 天然气'),
('demo-attr-007-b', 'demo-prd-007', '检测原理', '催化燃烧'),
('demo-attr-007-c', 'demo-prd-007', '量程', '0-100% LEL'),
('demo-attr-007-d', 'demo-prd-007', '输出信号', '4-20mA'),
('demo-attr-007-e', 'demo-prd-007', '响应时间', 'T90 ≤ 10s'),
('demo-attr-007-f', 'demo-prd-007', '使用寿命', '≥ 5 年'),
('demo-attr-007-g', 'demo-prd-007', '年漂移', '≤ ±2%'),
('demo-attr-007-h', 'demo-prd-007', '工作温度', '-40℃ ~ +70℃'),
('demo-attr-007-i', 'demo-prd-007', '防爆等级', 'Ex d IIC T6'),
('demo-attr-007-j', 'demo-prd-007', '质保', '3年');

-- 产品 8：一氧化碳传感器模块
INSERT INTO t_product (product_uuid, name, description, status, cover_image, category_uuid) VALUES
('demo-prd-008', 'SenseSensor CO-200 一氧化碳传感器模块',
 'SenseSensor CO-200 采用进口电化学传感器核心，专为高湿度、高粉尘的工业环境优化。内置温湿度补偿算法，在 -20℃~50℃范围内精度保持 ±3%。标准 4-20mA 输出，即插即用。适用于钢铁厂、焦化厂、地下车库、隧道等 CO 浓度监测场景。',
 'PUBLISHED', '/uploads/demo/product-co200.jpg', 'demo-cat-004');

INSERT INTO t_product_image (image_id, product_uuid, url, alt_text, sort_order) VALUES
('demo-img-008-a', 'demo-prd-008', '/uploads/demo/product-co200-01.jpg', 'CO-200 传感器模块', 1);

INSERT INTO t_product_attribute (attr_id, product_uuid, attr_key, attr_val) VALUES
('demo-attr-008-a', 'demo-prd-008', '检测气体', '一氧化碳 (CO)'),
('demo-attr-008-b', 'demo-prd-008', '检测原理', '电化学（进口传感器）'),
('demo-attr-008-c', 'demo-prd-008', '量程', '0-1000ppm'),
('demo-attr-008-d', 'demo-prd-008', '分辨率', '1ppm'),
('demo-attr-008-e', 'demo-prd-008', '输出信号', '4-20mA'),
('demo-attr-008-f', 'demo-prd-008', '响应时间', 'T90 ≤ 30s'),
('demo-attr-008-g', 'demo-prd-008', '使用寿命', '≥ 3 年'),
('demo-attr-008-h', 'demo-prd-008', '工作温度', '-20℃ ~ +50℃'),
('demo-attr-008-i', 'demo-prd-008', '湿度范围', '15% ~ 95%RH（无冷凝）'),
('demo-attr-008-j', 'demo-prd-008', '质保', '2年');

-- 产品 9：硫化氢传感器模块
INSERT INTO t_product (product_uuid, name, description, status, cover_image, category_uuid) VALUES
('demo-prd-009', 'SenseSensor H2S-100 硫化氢传感器模块',
 'SenseSensor H2S-100 是高灵敏度的硫化氢电化学传感器模块，检测下限低至 0.1ppm，可及时发现微量 H₂S 泄漏。采用特殊抗中毒配方电极，在含硫环境下寿命不打折。适用于石油天然气开采、炼化、造纸、污水处理等 H₂S 高风险行业。',
 'PUBLISHED', '/uploads/demo/product-h2s100.jpg', 'demo-cat-004');

INSERT INTO t_product_image (image_id, product_uuid, url, alt_text, sort_order) VALUES
('demo-img-009-a', 'demo-prd-009', '/uploads/demo/product-h2s100-01.jpg', 'H2S-100 传感器模块', 1);

INSERT INTO t_product_attribute (attr_id, product_uuid, attr_key, attr_val) VALUES
('demo-attr-009-a', 'demo-prd-009', '检测气体', '硫化氢 (H₂S)'),
('demo-attr-009-b', 'demo-prd-009', '检测原理', '电化学（抗中毒电极）'),
('demo-attr-009-c', 'demo-prd-009', '量程', '0-100ppm'),
('demo-attr-009-d', 'demo-prd-009', '分辨率', '0.1ppm'),
('demo-attr-009-e', 'demo-prd-009', '输出信号', '4-20mA'),
('demo-attr-009-f', 'demo-prd-009', '响应时间', 'T90 ≤ 25s'),
('demo-attr-009-g', 'demo-prd-009', '使用寿命', '≥ 2 年'),
('demo-attr-009-h', 'demo-prd-009', '工作温度', '-20℃ ~ +50℃'),
('demo-attr-009-i', 'demo-prd-009', '防爆等级', 'Ex d IIC T6'),
('demo-attr-009-j', 'demo-prd-009', '质保', '2年');

-- ================================================================
-- 3. 解决方案 (t_content, type=SOLUTION)
-- ================================================================
INSERT INTO t_content (content_uuid, title, summary, body, cover_image, type, status, category_uuid) VALUES
('demo-sol-001',
 '石油化工储罐区气体安全监测方案',
 '针对大型原油/成品油储罐区，提供固定式可燃/有毒气体检测+红外对射+区域报警控制器的三位一体监测方案，满足 GB 50493 规范要求。',
 '<h3>方案背景</h3><p>石油化工储罐区是重大危险源，储存介质多为易燃易爆或有毒化学品。一旦发生泄漏，可能引发火灾、爆炸或人员中毒。国家强制性标准 GB 50493《石油化工可燃气体和有毒气体检测报警设计标准》对储罐区的气体检测布点、报警设定、联动控制提出了明确要求。</p><h3>方案架构</h3><p>本方案采用"点-线-面"三级监测体系：</p><ul><li><strong>点级监测</strong>：储罐呼吸阀、阀门法兰、机泵密封处安装 SenseGuard F-800 固定式可燃气体检测仪，每个潜在泄漏点不少于 1 台。</li><li><strong>线级监测</strong>：储罐区边界安装红外对射式可燃气体探测器，形成周界监测防线。</li><li><strong>面级监控</strong>：中央控制室部署 SenseAlarm C-5000 气体报警控制器，集中显示所有检测点状态。</li></ul><h3>联动控制</h3><p>三级报警联动机制：</p><ol><li>一级报警（20% LEL）：中控室声光提示，操作员确认</li><li>二级报警（40% LEL）：自动启动事故风机排风，关闭相关阀门</li><li>三级报警（60% LEL）：启动消防喷淋系统，触发全厂应急广播</li></ol>',
 '/uploads/demo/solution-petrochemical.jpg', 'SOLUTION', 'PUBLISHED', 'demo-cat-101'),

('demo-sol-002',
 '冶金钢铁高炉煤气安全监测方案',
 '针对钢铁企业高炉、转炉、焦炉等煤气高风险区域，提供 CO 全覆盖监测+人员随身检测+应急联动的综合解决方案。',
 '<h3>方案背景</h3><p>钢铁冶炼过程中产生的高炉煤气、转炉煤气、焦炉煤气富含一氧化碳（CO），浓度可达 20%-30%。CO 无色无味，人员吸入后迅速与血红蛋白结合导致缺氧窒息，是冶金行业的"隐形杀手"。</p><h3>方案要点</h3><ul><li><strong>固定监测</strong>：高炉炉顶、除尘器、TRT 发电机组、煤气管网阀门组等关键位置安装 SenseGuard F-600 一氧化碳检测仪。</li><li><strong>人员防护</strong>：一线作业人员每人配备 SenseGuard P-100 便携式 CO 检测仪，夹在衣领即可实时监测。</li><li><strong>巡检装备</strong>：安全巡检班组配备 SenseGuard P-200 多气体检测仪，可同时监测 CO、O₂、H₂S、LEL。</li><li><strong>集中控制</strong>：全厂部署 3 台 SenseAlarm C-5000 控制器，按高炉区/转炉区/焦炉区分区监控。</li></ul>',
 '/uploads/demo/solution-metallurgy.jpg', 'SOLUTION', 'PUBLISHED', 'demo-cat-102'),

('demo-sol-003',
 '天然气场站及管道泄漏监测方案',
 '针对天然气分输站、门站、调压站及长输管道，提供激光甲烷遥测+固定点检测+无人机巡检的立体化泄漏监测方案。',
 '<h3>方案背景</h3><p>天然气场站和长输管道是城市能源供应的"大动脉"。由于天然气主要成分为甲烷，爆炸极限 5%-15%，泄漏后极易形成爆炸性混合气体。近年来国内外多起天然气爆炸事故表明，传统的定期人工巡检已无法满足安全需求。</p><h3>方案亮点</h3><ul><li><strong>激光甲烷遥测</strong>：在无法靠近的区域（如高空管道、河底穿越段），采用激光甲烷遥测仪进行远距离扫描，检测距离可达 150 米。</li><li><strong>固定点检测</strong>：在压缩机组、调压撬、过滤分离器、排污池等关键部位安装 SenseGuard F-800 固定式可燃气体检测仪。</li><li><strong>智能巡检</strong>：配备无人机搭载甲烷激光检测模块，对长输管道进行周期性巡检，效率是人工的 20 倍。</li><li><strong>SCADA 集成</strong>：SenseAlarm C-5000 通过 Modbus TCP 接入场站 SCADA 系统，实现远程集中监控。</li></ul>',
 '/uploads/demo/solution-energy.jpg', 'SOLUTION', 'PUBLISHED', 'demo-cat-103'),

('demo-sol-004',
 '城市综合管廊气体安全监测方案',
 '针对城市地下综合管廊（含燃气舱、污水舱、电力舱），提供多气体在线监测+智能通风联动+消防报警集成的综合管廊安全解决方案。',
 '<h3>方案背景</h3><p>城市综合管廊将电力、通信、燃气、给排水等管线集于一体，是城市运行的"生命线"。管廊属于地下密闭空间，一旦发生燃气泄漏或电缆过热产生有毒气体，检修人员面临极大的安全风险。</p><h3>监测对象</h3><ul><li><strong>燃气舱</strong>：甲烷 (CH₄) — SenseGuard F-800 可燃气体检测仪</li><li><strong>污水舱</strong>：硫化氢 (H₂S)、氨气 (NH₃) — SenseGuard F-600 有毒气体检测仪</li><li><strong>电力舱</strong>：一氧化碳 (CO)、氧气 (O₂) — 多参数检测</li><li><strong>综合舱</strong>：温湿度、氧气 (O₂)</li></ul><h3>智能联动</h3><p>当任一舱室气体浓度超标，系统自动：① 启动对应区段防爆风机强制通风；② 关闭相邻防火分区防火阀；③ 管廊入口声光报警器启动，禁止人员进入；④ C-5000 通过 4G 向管理平台和值班人员手机 APP 推送告警。</p>',
 '/uploads/demo/solution-municipal.jpg', 'SOLUTION', 'PUBLISHED', 'demo-cat-104');

-- ================================================================
-- 4. 新闻动态 (t_content, type=NEWS)
-- ================================================================
INSERT INTO t_content (content_uuid, title, summary, body, cover_image, type, status, category_uuid) VALUES
('demo-news-001',
 'InterSense 新一代 F-800 系列通过 ATEX 国际防爆认证',
 '公司旗舰产品 SenseGuard F-800 固定式可燃气体检测仪正式获得欧盟 ATEX 防爆指令认证，为拓展海外市场奠定基础。',
 '<p>2026 年 4 月，InterSense（英森思）新一代固定式可燃气体检测仪 SenseGuard F-800 系列正式通过 TÜV 莱茵颁发的 ATEX 防爆指令认证（证书编号：TÜV 24 ATEX 8935 X），标志着该产品满足欧盟市场准入要求，可在欧洲及认可 ATEX 标准的地区销售和使用。</p><p>ATEX 认证是欧盟对在潜在爆炸性环境中使用的设备和保护系统的强制性要求。F-800 系列在 TÜV 实验室完成了火花点燃试验、温度等级测试、冲击测试、IP 防护等级测试等 20 余项严格检测，全部一次性通过。</p><p>公司总经理表示："ATEX 认证是 InterSense 国际化战略的重要里程碑。我们计划在今年下半年启动东南亚和中东市场的渠道建设，F-800 系列将作为主力产品推向海外。"</p>',
 '/uploads/demo/news-atex.jpg', 'NEWS', 'PUBLISHED', 'demo-cat-201'),

('demo-news-002',
 'InterSense 中标某大型石化基地气体监测系统项目',
 '中标金额超 2000 万元，将为 12 平方公里的石化基地提供共计 1800 余台气体检测设备及集中监控平台。',
 '<p>近日，InterSense 成功中标华东某国家级石化产业基地气体安全监测系统项目，合同总金额超过 2000 万元。该项目覆盖 12 平方公里的化工园区，包括炼化一体化装置区、储运罐区、污水处理厂和公用工程区，共计需要安装 1800 余台各类气体检测设备。</p><p>项目将采用 F-800 固定式可燃/有毒气体检测仪、C-5000 大型报警控制器、以及 InterSense 自主研发的 iSafe 园区级气体安全监控平台。系统建成后将接入园区应急指挥中心，实现整个石化基地的气体安全"一张图"管理。</p><p>该项目是 InterSense 成立以来承接的最大单体项目，标志着公司在大型化工园区整体解决方案方面的能力获得了行业认可。</p>',
 '/uploads/demo/news-project.jpg', 'NEWS', 'PUBLISHED', 'demo-cat-201'),

('demo-news-003',
 '工信部发布《工业气体检测仪行业发展指导意见》',
 '政策利好：要求到 2028 年，化工园区气体在线监测覆盖率达到 100%，存量设备数字化改造率达到 80%。',
 '<p>2026 年 3 月，工业和信息化部正式发布《关于加快推进工业气体检测仪行业高质量发展的指导意见》，明确提出三大目标：到 2028 年，全国化工园区危险气体在线监测覆盖率应达到 100%；现有气体检测设备的数字化、网络化改造率应达到 80%；培育 3-5 家具有国际竞争力的气体检测仪骨干企业。</p><p>《意见》还鼓励企业加大研发投入，重点突破红外气体传感器、激光气体遥测、MEMS 气体传感器芯片等关键技术，并在智慧化工园区、城市生命线安全工程等领域开展应用示范。</p><p>行业分析师认为，该政策将直接拉动工业气体检测仪市场每年 15% 以上的增速，对 InterSense 等掌握核心传感器技术的企业是重大利好。</p>',
 '/uploads/demo/news-policy.jpg', 'NEWS', 'PUBLISHED', 'demo-cat-202'),

('demo-news-004',
 '红外气体传感技术的最新进展与工业应用趋势',
 '红外气体传感器凭借"免校准、长寿命、抗中毒"三大优势，正在逐步替代传统催化燃烧传感器，成为可燃气体检测领域的新主流。',
 '<p>红外（NDIR）气体传感技术近年来发展迅速，在工业可燃气体检测领域的渗透率已从 2020 年的 15% 提升至 2026 年的 35%。相比传统催化燃烧传感器，红外传感器具有三大核心优势：</p><ul><li><strong>免校准</strong>：红外传感器基于光学吸收原理，出厂后无需频繁校准，年漂移量 < 1%，大幅降低了现场维护工作量。</li><li><strong>长寿命</strong>：红外光源和探测器的设计寿命通常为 5-8 年，是催化燃烧传感器的 2-3 倍。</li><li><strong>抗中毒</strong>：催化燃烧传感器在含硫、含硅环境中容易"中毒"失效，红外传感器不受这些干扰。</li></ul><p>InterSense 研发团队在红外甲烷传感器的小型化和低成本化方面取得了突破，新一代微型红外甲烷传感器模块体积仅为此前的 1/3，成本下降了 40%，预计 2026 年底实现量产。</p>',
 '/uploads/demo/news-tech.jpg', 'NEWS', 'PUBLISHED', 'demo-cat-203');

-- ================================================================
-- 5. 演示用户 + 员工（demo- 前缀，与上方正式账号共存）
-- ================================================================

-- 演示客户（密码 demo123 即 123456，BCrypt）
INSERT INTO t_admin_user (user_uuid, username, password_hash, phone, company, role) VALUES
('demo-usr-001', 'demo', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '13800001111', '中石化工程建设有限公司', 'USER'),
('demo-usr-002', 'zhangsan', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '13900002222', '宝钢工程技术集团', 'USER'),
('demo-usr-003', 'lisi', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '13700003333', '华能国际电力', 'USER');

-- 演示员工（供工单指派）
INSERT INTO t_staff (staff_uuid, name, phone, email, role, status) VALUES
('demo-stf-001', '张建国', '13810001111', 'zhangjg@intersense.com', 'FIELD_TECH', 'WORKING'),
('demo-stf-002', '李明华', '13810002222', 'limh@intersense.com', 'CUSTOMER_SERVICE', 'WORKING'),
('demo-stf-003', '王磊',   '13810003333', 'wanglei@intersense.com', 'TECH_SUPPORT', 'STANDBY'),
('demo-stf-004', '赵丽',   '13810004444', 'zhaoli@intersense.com', 'AFTER_SALES', 'WORKING'),
('demo-stf-005', '陈强',   '13810005555', 'chenqiang@intersense.com', 'FIELD_TECH', 'BUSINESS_TRIP');

-- ================================================================
-- 6. 客户留言 (t_contact_message)
-- ================================================================
INSERT INTO t_contact_message (message_uuid, name, phone, content, ip, status, submitted_at) VALUES
('demo-msg-001', '刘工', '13912345678',
 '你好，我们公司是做化工厂设备维护的，最近需要采购一批固定式可燃气体检测仪，大约需要 50 台。现场环境温度较高（约 60℃），有少量腐蚀性气体，麻烦推荐合适的型号并报个价。另外想了解一下你们的售后服务和质保政策。',
 '192.168.1.100', 'PENDING', '2026-05-15 10:30:00'),

('demo-msg-002', '王经理', '18612345678',
 '我们天然气门站目前在用某品牌的气体检测设备，但故障率较高，半年内坏了 3 台。听说贵公司的产品口碑不错，想了解一下 F-800 和 C-5000 的组合方案，希望能安排技术人员上门做个现场勘察。',
 '10.0.5.23', 'IN_PROGRESS', '2026-05-18 14:20:00'),

('demo-msg-003', '赵安全', '13312345678',
 '请问贵公司的便携式气体检测仪 P-200 是否支持定制气体组合？我们井下作业需要同时检测甲烷、氧气、一氧化碳和二氧化硫，标准四合一不包含 SO₂。如果可以定制，最小起订量是多少？交货周期多久？',
 '112.45.67.89', 'PENDING', '2026-05-19 09:15:00'),

('demo-msg-004', '孙主任', '17712345678',
 '我们钢铁厂新上了一套高炉煤气回收装置，需要配套一套完整的 CO 监测系统，大约覆盖 30 个检测点。希望你们的方案能满足以下几个要求：1) 传感器需具备抗粉尘能力；2) 控制器需接入厂区现有的 DCS 系统；3) 需要提供安装调试培训一条龙服务。',
 '58.213.45.67', 'PROCESSED', '2026-05-10 16:00:00'),

('demo-msg-005', '周工', '15212345678',
 '咨询一下，你们有没有针对地下车库的 CO 监测方案？我们新建的小区地下三层车库面积约 2 万平米，需要做气体监测联动排风系统。',
 '218.94.12.56', 'PENDING', '2026-05-20 11:45:00');

-- ================================================================
-- 7. 工单 (t_work_order)
-- ================================================================
INSERT INTO t_work_order (work_order_uuid, title, type, description, status, priority, assigned_staff_uuid, assigned_staff_name, customer_name, customer_phone) VALUES
('demo-wo-001', '中石化储罐区 F-800 安装调试', 'TECH_SUPPORT',
 '客户新采购 50 台 F-800 固定式可燃气体检测仪，需要现场安装指导、接线调试、与现有 C-5000 控制器对接配置。工期预计 5 天。',
 'IN_PROGRESS', 'HIGH', 'demo-stf-001', '张建国', '刘工', '13912345678'),

('demo-wo-002', '宝钢焦化厂 CO 传感器校准', 'TECH_SUPPORT',
 '宝钢焦化厂 2024 年安装的 120 台 CO 传感器已到年度校准周期，需安排人员赴现场逐台标定并更换部分到期传感器模块。',
 'PENDING', 'MEDIUM', 'demo-stf-003', '王磊', '孙主任', '17712345678'),

('demo-wo-003', '华能电厂 F-600 故障排查', 'AFTER_SALES',
 '客户反馈 3 台 F-600 H₂S 检测仪频繁误报，要求上门排查。初步判断可能是环境湿度偏高导致传感器漂移，需携带替换传感器和校准设备。',
 'PENDING', 'HIGH', 'demo-stf-004', '赵丽', '周工', '15212345678'),

('demo-wo-004', '天然气门站现场勘察', 'TECH_SUPPORT',
 '意向客户天然气门站需要整体气体安全评估，包括现有检测点布局诊断、新增 F-800 布点建议、C-5000 集成方案设计。',
 'PENDING', 'MEDIUM', 'demo-stf-005', '陈强', '王经理', '18612345678'),

('demo-wo-005', '中石化三期项目方案设计', 'TECH_SUPPORT',
 '中石化新厂区三期扩建项目需要全套气体安全方案设计，包括：罐区、装卸区、管廊三个区域的产品选型、布线设计、控制系统集成方案。',
 'COMPLETED', 'MEDIUM', 'demo-stf-001', '张建国', '刘工', '13912345678');

-- 已完成的工单补上完成时间
UPDATE t_work_order SET completed_at = '2026-05-20 17:00:00' WHERE work_order_uuid = 'demo-wo-005';

-- ================================================================
-- 8. 设备 (t_device)
--    注：api_token 为演示占位值，生产环境需替换为真实 SHA256
-- ================================================================
INSERT INTO t_device (device_uuid, serial_number, api_token, name, model, customer_uuid, install_location, install_date, gas_type, range_min, range_max, alert_threshold, status) VALUES
-- 中石化 (demo-usr-001) 的 3 台设备
('demo-dev-001', 'SN-F800-2024-001', 'demo-token-placeholder-001', '1号罐区甲烷检测仪',  'F-800', 'demo-usr-001', 'A区原油储罐区',   '2024-03-15', 'CH4', 0, 100,  20, 'NORMAL'),
('demo-dev-002', 'SN-F800-2024-002', 'demo-token-placeholder-002', '2号罐区硫化氢检测仪', 'F-800', 'demo-usr-001', 'B区脱硫装置区',   '2024-03-20', 'H2S', 0, 100,  10, 'ABNORMAL'),
('demo-dev-003', 'SN-F600-2024-001', 'demo-token-placeholder-003', '锅炉房CO检测仪',      'F-600', 'demo-usr-001', 'C区燃气锅炉房',   '2024-04-01', 'CO',  0, 1000, 50, 'NORMAL'),
-- 宝钢 (demo-usr-002) 的 3 台设备
('demo-dev-004', 'SN-F600-2024-002', 'demo-token-placeholder-004', '1号高炉CO检测仪',     'F-600', 'demo-usr-002', '1号高炉炉顶',     '2024-02-10', 'CO',  0, 1000, 100,'NORMAL'),
('demo-dev-005', 'SN-F800-2024-003', 'demo-token-placeholder-005', '焦炉煤气甲烷检测仪',  'F-800', 'demo-usr-002', '焦炉煤气管道阀门组','2024-02-15','CH4', 0, 100,  20, 'OFFLINE'),
('demo-dev-006', 'SN-P200-2024-001', 'demo-token-placeholder-006', '维修车间便携检测仪',   'P-200', 'demo-usr-002', '维修车间（共享设备）','2024-05-01','CH4',0, 100,  20, 'MAINTENANCE'),
-- 华能 (demo-usr-003) 的 2 台设备
('demo-dev-007', 'SN-F600-2024-003', 'demo-token-placeholder-007', 'FGD硫化氢检测仪',    'F-600', 'demo-usr-003', '烟气脱硫装置出口', '2024-06-10', 'H2S', 0, 100,  10, 'NORMAL'),
('demo-dev-008', 'SN-F800-2024-004', 'demo-token-placeholder-008', '燃机入口甲烷检测仪',  'F-800', 'demo-usr-003', '燃气轮机组天然气入口','2024-06-12','CH4',0, 100,  20, 'NORMAL');

-- 模拟器设备（device-simulator 实时上报，UUID 需与 application.yml 一致）
INSERT INTO t_device (device_uuid, serial_number, api_token, name, model, customer_uuid, install_location, install_date, gas_type, range_min, range_max, alert_threshold, status) VALUES
('demo-sim-001', 'SIM-CH4-2026-001', 'demo-token-sim-001', '模拟器-甲烷检测仪(CH4)',  'F-800', 'demo-usr-001', '模拟器虚拟设备', '2026-06-01', 'CH4', 0, 100,  20, 'NORMAL'),
('demo-sim-002', 'SIM-H2S-2026-001', 'demo-token-sim-002', '模拟器-硫化氢检测仪(H2S)', 'F-600', 'demo-usr-002', '模拟器虚拟设备', '2026-06-01', 'H2S', 0, 100,  10, 'NORMAL'),
('demo-sim-003', 'SIM-CO-2026-001',  'demo-token-sim-003', '模拟器-一氧化碳检测仪(CO)', 'F-600', 'demo-usr-003', '模拟器虚拟设备', '2026-06-01', 'CO',  0, 1000, 50, 'NORMAL');

-- ================================================================
-- 9. 报警规则 (t_alert_rule)
-- ================================================================
INSERT INTO t_alert_rule (rule_uuid, name, device_uuid, rule_type, gas_type, threshold, duration_seconds, severity, auto_create_work_order, enabled) VALUES
('demo-rule-001', '全局甲烷阈值超限报警', NULL,            'THRESHOLD',   'CH4', 20,  60, 'CRITICAL', 1, 1),
('demo-rule-002', '2号罐区H2S阈值报警',  'demo-dev-002',  'THRESHOLD',   'H2S', 10,  30, 'WARNING',   0, 1),
('demo-rule-003', '全局设备离线报警',     NULL,            'OFFLINE',     NULL,  NULL,300, 'WARNING',   1, 1),
('demo-rule-004', '全局低电量报警',       NULL,            'LOW_BATTERY', NULL,  NULL,60,  'INFO',      0, 1),
('demo-rule-005', '1号高炉CO阈值报警',    'demo-dev-004',  'THRESHOLD',   'CO',  100,  30, 'CRITICAL',  1, 1),
('demo-rule-006', '全局硫化氢阈值超限报警', NULL,            'THRESHOLD',   'H2S', 10,  30, 'WARNING',   1, 1),
('demo-rule-007', '全局一氧化碳阈值超限报警', NULL,          'THRESHOLD',   'CO',  50,  30, 'CRITICAL',  1, 1);

-- ================================================================
-- 11. 报警记录 (t_alert)
--     注意：confirmed_by / resolved_by 使用 UUID 而非姓名
--     admin = a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d
-- ================================================================
INSERT INTO t_alert (alert_uuid, device_uuid, rule_uuid, alert_type, severity, concentration, threshold, message, status, triggered_at, confirmed_at, confirmed_by, resolved_at, resolved_by, work_order_uuid) VALUES
-- PENDING：demo-dev-001 甲烷超限
('demo-alt-001', 'demo-dev-001', 'demo-rule-001', 'THRESHOLD', 'CRITICAL', 35.5, 20, '甲烷浓度严重超标：当前值 35.5% LEL，阈值 20% LEL', 'PENDING', '2026-05-27 09:15:00', NULL, NULL, NULL, NULL, NULL),
-- CONFIRMED：demo-dev-002 H2S 偏高，admin 已确认
('demo-alt-002', 'demo-dev-002', 'demo-rule-002', 'THRESHOLD', 'WARNING', 15.2, 10, '硫化氢浓度超标：当前值 15.2ppm，阈值 10ppm', 'CONFIRMED', '2026-05-27 08:30:00', '2026-05-27 08:45:00', 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', NULL, NULL, NULL),
-- RESOLVED：demo-dev-003 CO 报警，张建国已解决
('demo-alt-003', 'demo-dev-003', 'demo-rule-001', 'THRESHOLD', 'WARNING', 65.0, 50, '一氧化碳浓度超标：当前值 65ppm，阈值 50ppm', 'RESOLVED', '2026-05-26 14:00:00', '2026-05-26 14:15:00', 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', '2026-05-26 17:30:00', 'demo-stf-001', NULL),
-- PENDING：demo-dev-005 离线
('demo-alt-004', 'demo-dev-005', 'demo-rule-003', 'OFFLINE', 'WARNING', NULL, NULL, '设备「焦炉煤气甲烷检测仪」离线超过 5 分钟，最后上报时间 2026-05-27 07:00:00', 'PENDING', '2026-05-27 07:05:00', NULL, NULL, NULL, NULL, NULL),
-- PENDING：demo-dev-006 低电量
('demo-alt-005', 'demo-dev-006', 'demo-rule-004', 'LOW_BATTERY', 'INFO', NULL, NULL, '设备「维修车间便携检测仪」电量低于 20%，当前电量 15%，请及时充电', 'PENDING', '2026-05-27 10:00:00', NULL, NULL, NULL, NULL, NULL),
-- CLOSED：demo-dev-004 历史报警
('demo-alt-006', 'demo-dev-004', 'demo-rule-005', 'THRESHOLD', 'CRITICAL', 120.0, 100, '一氧化碳浓度严重超标：当前值 120ppm，阈值 100ppm', 'CLOSED', '2026-05-25 16:30:00', '2026-05-25 16:35:00', 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', '2026-05-25 18:00:00', 'demo-stf-002', NULL);

-- ================================================================
-- 12. 通知记录 (t_notification)
-- ================================================================
INSERT INTO t_notification (notification_uuid, alert_uuid, recipient, channel, content, status, sent_at) VALUES
-- demo-alt-001 的短信+邮件通知
('demo-not-001', 'demo-alt-001', '13800001111',          'SMS',   '【InterSense报警】1号罐区甲烷检测仪：甲烷浓度严重超标 35.5% LEL（阈值20%），请立即处理！', 'SENT', '2026-05-27 09:15:30'),
('demo-not-002', 'demo-alt-001', 'liugong@sinopec.com',  'EMAIL', '甲烷浓度严重超标报警…（邮件正文）', 'SENT', '2026-05-27 09:15:35'),
-- demo-alt-002 短信通知
('demo-not-003', 'demo-alt-002', '13800001111',          'SMS',   '【InterSense报警】2号罐区硫化氢检测仪：H2S浓度超标 15.2ppm（阈值10ppm）', 'SENT', '2026-05-27 08:30:10'),
-- demo-alt-004 离线通知（待发送）
('demo-not-004', 'demo-alt-004', '13900002222',          'SMS',   '【InterSense报警】焦炉煤气甲烷检测仪 离线超过5分钟，请检查设备', 'PENDING', NULL),
-- demo-alt-005 低电量通知
('demo-not-005', 'demo-alt-005', '13900002222',          'SMS',   '【InterSense提示】维修车间便携检测仪 电量低于20%，请及时充电', 'SENT', '2026-05-27 10:00:15'),
-- demo-alt-003 历史通知
('demo-not-006', 'demo-alt-003', '13800001111',          'SMS',   '【InterSense报警】锅炉房CO检测仪：CO浓度超标 65ppm（阈值50ppm）', 'SENT', '2026-05-26 14:00:30'),
-- demo-alt-006 历史通知
('demo-not-007', 'demo-alt-006', '13900002222',          'SMS',   '【InterSense报警】1号高炉CO检测仪：CO浓度严重超标 120ppm（阈值100ppm）', 'SENT', '2026-05-25 16:30:20');

-- ================================================================
-- 13. 报警自动生成工单 (t_work_order, type=ALERT)
-- ================================================================
INSERT INTO t_work_order (work_order_uuid, title, type, description, status, priority, assigned_staff_uuid, assigned_staff_name, customer_name, customer_phone) VALUES
('demo-wo-006', '1号罐区甲烷超限紧急排查', 'ALERT',
 '报警平台自动生成：demo-dev-001 甲烷浓度达 35.5% LEL，超过阈值 20% LEL，需立即赴现场排查泄漏源。',
 'PENDING', 'HIGH', 'demo-stf-001', '张建国', '刘工', '13912345678'),
('demo-wo-007', '焦炉煤气检测仪离线检修', 'ALERT',
 '报警平台自动生成：demo-dev-005 焦炉煤气甲烷检测仪离线超过 5 分钟，需现场检查设备供电和通讯线路。',
 'IN_PROGRESS', 'HIGH', 'demo-stf-005', '陈强', '王经理', '18612345678');

-- ================================================================
-- 14. 评论 (t_comment) — V2 新增
-- ================================================================
INSERT INTO t_comment (comment_uuid, target_type, target_uuid, author_type, author_uuid, author_name, content) VALUES
-- 工单评论
('demo-cmt-001', 'WORK_ORDER', 'demo-wo-001', 'ADMIN', 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'admin',
 '已联系客户确认安装日期为 6 月 5 日，请张工提前准备好 F-800 调试工具和校准气体。'),
('demo-cmt-002', 'WORK_ORDER', 'demo-wo-001', 'STAFF', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a', '张工',
 '收到，已备好设备清单：F-800 × 50、C-5000 × 1、RS485 中继器 × 3。'),
-- 产品评论
('demo-cmt-003', 'PRODUCT', 'demo-prd-001', 'CUSTOMER', 'demo-usr-001', 'demo',
 'F-800 在我们储罐区运行半年了，非常稳定，催化燃烧传感器还没校准过，点赞！'),
('demo-cmt-004', 'PRODUCT', 'demo-prd-003', 'CUSTOMER', 'demo-usr-002', 'zhangsan',
 'P-200 的续航确实强，连续用了两天都没充电。就是 APP 界面字有点小，建议优化。'),
-- 解决方案评论
('demo-cmt-005', 'CONTENT', 'demo-sol-002', 'CUSTOMER', 'demo-usr-002', 'zhangsan',
 '这个高炉煤气方案写得很专业，我们焦化厂情况类似，能否安排技术人员来现场看看？');

-- ================================================================
-- 15. 下载文件 (t_download_file) — V2 新增
-- ================================================================
INSERT INTO t_download_file (download_uuid, display_name, original_name, file_size, content_type, stored_path) VALUES
('demo-dl-001', 'F-800 产品规格书', 'F-800_Datasheet_V3.2.pdf', 2458624, 'application/pdf', '/uploads/demo/F-800_Datasheet_V3.2.pdf'),
('demo-dl-002', 'C-5000 安装手册', 'C-5000_Installation_Guide.pdf', 1843200, 'application/pdf', '/uploads/demo/C-5000_Installation_Guide.pdf'),
('demo-dl-003', 'P-200 快速入门指南', 'P-200_QuickStart.pdf', 1024000, 'application/pdf', '/uploads/demo/P-200_QuickStart.pdf'),
('demo-dl-004', 'InterSense 产品目录 2026', 'InterSense_Catalog_2026.pdf', 5242880, 'application/pdf', '/uploads/demo/InterSense_Catalog_2026.pdf'),
('demo-dl-005', '气体检测仪选型指南', 'Gas_Detector_Selection_Guide.xlsx', 327680, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '/uploads/demo/Gas_Detector_Selection_Guide.xlsx');

-- ================================================================
-- 16. 补充数据 — 更多客户、设备、数据点、告警历史
-- ================================================================

-- 补充客户（各行业代表）
INSERT INTO t_admin_user (user_uuid, username, password_hash, phone, company, role) VALUES
('demo-usr-004', 'wangwu',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '13600004444', '万华化学集团',        'USER'),
('demo-usr-005', 'zhaoliu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '13500005555', '国家管网集团',        'USER'),
('demo-usr-006', 'sunqi',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '13400006666', '延长石油集团',        'USER');

-- 补充设备（覆盖更多状态和气体类型）
INSERT INTO t_device (device_uuid, serial_number, api_token, name, model, customer_uuid, install_location, install_date, gas_type, range_min, range_max, alert_threshold, status) VALUES
-- 万华化学 (demo-usr-004)
('demo-dev-009', 'SN-F600-2025-001', 'demo-token-placeholder-009', 'MDI装置氯气检测仪',   'F-600', 'demo-usr-004', 'MDI 装置反应釜区', '2025-01-20', 'NH3', 0, 100,  25, 'NORMAL'),
('demo-dev-010', 'SN-F800-2025-002', 'demo-token-placeholder-010', '液氨罐区甲烷检测仪',  'F-800', 'demo-usr-004', '液氨球罐区',       '2025-02-10', 'CH4', 0, 100,  20, 'NORMAL'),
-- 国家管网 (demo-usr-005)
('demo-dev-011', 'SN-F800-2025-003', 'demo-token-placeholder-011', '压气站甲烷检测仪A',   'F-800', 'demo-usr-005', '1号压缩机组',      '2025-03-05', 'CH4', 0, 100,  20, 'NORMAL'),
('demo-dev-012', 'SN-F800-2025-004', 'demo-token-placeholder-012', '分输站甲烷检测仪B',   'F-800', 'demo-usr-005', '分输计量区',       '2025-03-10', 'CH4', 0, 100,  15, 'ABNORMAL'),
-- 延长石油 (demo-usr-006)
('demo-dev-013', 'SN-F600-2025-005', 'demo-token-placeholder-013', '炼化厂H2S检测仪',     'F-600', 'demo-usr-006', '常减压装置区',     '2025-04-01', 'H2S', 0, 50,   10, 'NORMAL'),
('demo-dev-014', 'SN-P200-2025-002', 'demo-token-placeholder-014', '巡检班组便携检测仪',   'P-200', 'demo-usr-006', '安全巡检组（共享）','2025-04-15', 'CH4', 0, 100,  20, 'NORMAL');

-- 补充告警：多样化的历史告警（用于仪表盘时间线和统计图表）
INSERT INTO t_alert (alert_uuid, device_uuid, rule_uuid, alert_type, severity, concentration, threshold, message, status, triggered_at, confirmed_at, confirmed_by, resolved_at, resolved_by) VALUES
-- demo-dev-013 触发阈值告警（H2S 缓慢上升超限 → CONFIRMED）
('demo-alt-007', 'demo-dev-013', 'demo-rule-002', 'THRESHOLD', 'WARNING', 11.2, 10, '硫化氢浓度缓慢上升至超限：当前值 11.2ppm，阈值 10ppm，建议检查密封件', 'CONFIRMED', '2026-05-27 09:50:00', '2026-05-27 10:05:00', 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', NULL, NULL),
-- demo-dev-012 离线告警（分输站设备离线 → PENDING）
('demo-alt-008', 'demo-dev-012', 'demo-rule-003', 'OFFLINE', 'WARNING', NULL, NULL, '设备「分输站甲烷检测仪B」离线超过 10 分钟，最后上报时间 2026-05-27 09:00:00', 'PENDING', '2026-05-27 09:10:00', NULL, NULL, NULL, NULL),
-- demo-dev-003 CO 持续上升已超阈值 → 新报警
('demo-alt-009', 'demo-dev-003', 'demo-rule-005', 'THRESHOLD', 'CRITICAL', 68.0, 50, '一氧化碳浓度持续上升至危险水平：当前值 68ppm，阈值 50ppm，锅炉燃烧不充分风险', 'PENDING', '2026-05-27 10:00:00', NULL, NULL, NULL, NULL),
-- demo-dev-001 历史已解决告警（用于展示 RESOLVED 趋势）
('demo-alt-010', 'demo-dev-001', 'demo-rule-001', 'THRESHOLD', 'WARNING', 18.0, 20, '甲烷浓度波动接近阈值：当前值 18% LEL，请关注', 'RESOLVED', '2026-05-26 15:00:00', '2026-05-26 15:10:00', 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', '2026-05-26 16:30:00', 'demo-stf-001'),
-- demo-dev-004 历史低电量告警
('demo-alt-011', 'demo-dev-004', 'demo-rule-004', 'LOW_BATTERY', 'INFO', NULL, NULL, '设备「1号高炉CO检测仪」电量低于 20%，当前电量 18%', 'CLOSED', '2026-05-24 08:00:00', '2026-05-24 08:30:00', 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', '2026-05-24 12:00:00', 'demo-stf-003');

-- 补充通知
INSERT INTO t_notification (notification_uuid, alert_uuid, recipient, channel, content, status, sent_at) VALUES
('demo-not-008', 'demo-alt-007', '13400006666', 'SMS', '【InterSense报警】炼化厂H2S检测仪：H2S浓度缓慢上升 11.2ppm（阈值10ppm），请检查密封件', 'SENT', '2026-05-27 09:50:30'),
('demo-not-009', 'demo-alt-009', '13800001111', 'SMS', '【InterSense报警】锅炉房CO检测仪：CO浓度持续上升 68ppm（阈值50ppm），锅炉燃烧不充分风险！', 'PENDING', NULL),
('demo-not-010', 'demo-alt-009', 'liugong@sinopec.com', 'EMAIL', '一氧化碳浓度持续上升危险…（邮件正文）', 'PENDING', NULL),
('demo-not-011', 'demo-alt-008', '13500005555', 'SMS', '【InterSense报警】分输站甲烷检测仪B 离线超过10分钟，请检查设备供电和网络', 'SENT', '2026-05-27 09:10:30');

-- 补充工单
INSERT INTO t_work_order (work_order_uuid, title, type, description, status, priority, assigned_staff_uuid, assigned_staff_name, customer_name, customer_phone) VALUES
('demo-wo-008', '万华化学 MDI 装置 NH3 传感器年度标定', 'TECH_SUPPORT',
 '万华化学 MDI 装置区安装的 12 台 NH3 检测仪（F-600）已到年度标定周期，需安排人员携带标准气体赴现场逐台标定，预计工期 3 天。',
 'PENDING', 'MEDIUM', 'demo-stf-001', '张建国', '王五', '13600004444'),
('demo-wo-009', '延长石油炼化厂 H2S 报警排查', 'ALERT',
 '报警平台自动生成：demo-dev-013 H2S 浓度从 08:00 开始缓慢上升，09:50 达到 11.2ppm 超阈值，怀疑管道微量泄漏，需现场排查。',
 'IN_PROGRESS', 'HIGH', 'demo-stf-005', '陈强', '孙七', '13400006666'),
('demo-wo-010', '国家管网压气站巡检维护', 'AFTER_SALES',
 '季度例行巡检：检查压气站 6 台 F-800 检测仪运行状态、校准传感器、清洁防爆外壳、测试 RS485 通讯。',
 'PENDING', 'LOW', 'demo-stf-003', '王磊', '赵六', '13500005555');

UPDATE t_work_order SET completed_at = '2026-05-22 16:00:00' WHERE work_order_uuid = 'demo-wo-009';

-- 补充评论
INSERT INTO t_comment (comment_uuid, target_type, target_uuid, author_type, author_uuid, author_name, content) VALUES
('demo-cmt-006', 'PRODUCT', 'demo-prd-005', 'CUSTOMER', 'demo-usr-005', 'zhaoliu',
 'C-5000 在压气站运行一年了，128 路接入非常稳定，Web 远程监控功能是亮点，值班人员不需要 24 小时盯着屏幕。就是触摸屏在强光下可视性稍差，建议下一代升级屏幕亮度。'),
('demo-cmt-007', 'WORK_ORDER', 'demo-wo-006', 'STAFF', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a', '张工',
 '已到达现场，正在排查 1号罐区阀门组。初步判断是阀门密封垫老化导致微漏，已联系客户安排停气更换。'),
('demo-cmt-008', 'CONTENT', 'demo-sol-004', 'CUSTOMER', 'demo-usr-006', 'sunqi',
 '管廊方案很全面。我们市新建的综合管廊准备采用贵公司方案，请问燃气舱和电力舱是否可以共用一台 C-5000 控制器？'),
('demo-cmt-009', 'PRODUCT', 'demo-prd-002', 'ADMIN', 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'admin',
 'F-600 传感器热插拔功能已在 V2.3 固件中优化，更换后自动识别气体类型和量程，无需手动配置。建议所有用户升级。');

-- ================================================================
-- 验证
-- ================================================================
SELECT '=== 演示数据插入完毕 ===' AS '';
SELECT '分类' AS '表', COUNT(*) AS '条数' FROM t_category WHERE category_uuid LIKE 'demo-%'
UNION ALL SELECT '产品', COUNT(*) FROM t_product WHERE product_uuid LIKE 'demo-%'
UNION ALL SELECT '产品图片', COUNT(*) FROM t_product_image WHERE product_uuid LIKE 'demo-%'
UNION ALL SELECT '产品属性', COUNT(*) FROM t_product_attribute WHERE product_uuid LIKE 'demo-%'
UNION ALL SELECT '解决方案', COUNT(*) FROM t_content WHERE content_uuid LIKE 'demo-sol-%'
UNION ALL SELECT '新闻', COUNT(*) FROM t_content WHERE content_uuid LIKE 'demo-news-%'
UNION ALL SELECT '演示用户', COUNT(*) FROM t_admin_user WHERE user_uuid LIKE 'demo-%'
UNION ALL SELECT '员工', COUNT(*) FROM t_staff WHERE staff_uuid LIKE 'demo-%'
UNION ALL SELECT '留言', COUNT(*) FROM t_contact_message WHERE message_uuid LIKE 'demo-%'
UNION ALL SELECT '工单', COUNT(*) FROM t_work_order WHERE work_order_uuid LIKE 'demo-%'
UNION ALL SELECT '设备', COUNT(*) FROM t_device WHERE device_uuid LIKE 'demo-%'
UNION ALL SELECT '报警规则', COUNT(*) FROM t_alert_rule WHERE rule_uuid LIKE 'demo-%'
UNION ALL SELECT '报警记录', COUNT(*) FROM t_alert WHERE alert_uuid LIKE 'demo-%'
UNION ALL SELECT '通知记录', COUNT(*) FROM t_notification WHERE notification_uuid LIKE 'demo-%'
UNION ALL SELECT '评论', COUNT(*) FROM t_comment WHERE comment_uuid LIKE 'demo-%'
UNION ALL SELECT '下载文件', COUNT(*) FROM t_download_file WHERE download_uuid LIKE 'demo-%'
UNION ALL SELECT '系统配置', COUNT(*) FROM t_system_config;

-- ============================================================
-- System Config 系统配置默认值
-- ============================================================
INSERT IGNORE INTO t_system_config (config_key, config_value, description) VALUES
('site.name', 'Intersense 工业气体检测', '网站名称'),
('site.logo', '', '网站Logo URL'),
('site.footer', '© 2024 Intersense. All rights reserved.', '页脚版权信息'),
('contact.phone', '400-888-8888', '联系电话'),
('contact.email', 'sales@intersense.cn', '联系邮箱'),
('contact.address', '深圳市南山区科技园', '公司地址'),
('alert.default_threshold', '1.0', '默认报警阈值'),
('alert.retention_days', '90', '报警保留天数');
