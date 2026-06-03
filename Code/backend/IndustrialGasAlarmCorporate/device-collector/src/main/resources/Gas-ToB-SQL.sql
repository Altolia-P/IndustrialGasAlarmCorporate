-- ============================================================
-- 工业气体报警企业系统 — 采集器数据库建库脚本 V1.0
-- 数据库：industrial_gas_alarm_collector（device-collector 微服务）
-- MySQL 8.0 | 字符集：utf8mb4 | 主键：UUID v4 CHAR(36)
-- 生成依据：DeviceDataPointPO
-- 生成日期：2026-06-03
-- ============================================================

DROP DATABASE IF EXISTS industrial_gas_alarm_collector;
CREATE DATABASE industrial_gas_alarm_collector
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE industrial_gas_alarm_collector;

-- ============================================================
-- 1. 设备数据点表 (t_device_data_point) — 时序数据
-- PO: DeviceDataPointPO — 无 @Version, 无 @TableLogic, 无 updatedAt
-- ============================================================
CREATE TABLE t_device_data_point (
    data_point_id  CHAR(36)      PRIMARY KEY,
    device_uuid    CHAR(36)      NOT NULL,
    recorded_at    DATETIME      NOT NULL COMMENT '设备上报时间',
    concentration  DECIMAL(10,4) NULL COMMENT '气体浓度',
    battery        DECIMAL(5,2)  NULL COMMENT '电池电量（V）',
    temperature    DECIMAL(5,2)  NULL COMMENT '温度（℃）',
    humidity       DECIMAL(5,2)  NULL COMMENT '湿度（%）',
    signal_strength INT          NULL COMMENT '信号强度（0-100）',
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_dp_device_time (device_uuid, recorded_at DESC),
    INDEX idx_dp_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备数据点表（时序追加）';
