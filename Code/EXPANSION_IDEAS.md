# 项目扩展想法

> 记录于 2026-05-27 | 当前版本 V1.4

---

## 背景

当前系统是 **Modular Monolith**：Spring Boot 单体 + Redis（6 个简单 KV Repository）+ MySQL。

DDD 四层架构 + outbox 表已预留了微服务拆分的空间，但当前没有实际用到分布式/微服务能力。

---

## 演进路线

```
当前（V1.4）             →    阶段 2（新增业务模块）    →    阶段 3（完整微服务）
Modular Monolith                  + 设备监控平台               全拆分 + 事件驱动
仅 Redis KV                       + Redis Stream/PubSub        + SkyWalking/Xxl-Job
工单/留言/内容管理                + 告警引擎/通知服务           + Gateway/Sentinel
```

---

## 新增业务模块（设备监控平台）

从「企业官网」扩展为「企业官网 + 设备运行监控平台」。

### 模块 1：设备数据采集（Device Ingestion）

- 设备每 30s 上报浓度读数/电池电量/在线状态 → MQTT Broker
- **用到**：Redis Stream（削峰填谷）、独立微服务（和官网隔离资源）

### 模块 2：实时告警引擎（Alert Engine）

- 浓度读数连续 30s 超阈值 → 自动生成告警 → 连接工单系统 → 指派值班员
- **用到**：Redis Pub/Sub（实时推送大屏）、Redis Sorted Set（滑动窗口）

### 模块 3：通知服务（Notification Service）

- 告警产生 → 短信/邮件/电话通知 → 重试 3 次 → 死信队列
- **用到**：Redis Stream + Consumer Group（可靠消费）、XXL-Job（定时重试统计）

### 模块 4：数据看板（Analytics Dashboard）

- 客户查看设备运行状态、历史告警、浓度趋势
- **用到**：Redis Bitmap（在线状态）、HyperLogLog（活跃设备数）、SkyWalking（跨服务追踪）

### 技术 → 模块对应

| 技术 | 模块1 | 模块2 | 模块3 | 模块4 |
|------|:-----:|:-----:|:-----:|:-----:|
| Redis Stream | ✓ | ✓ | ✓ | |
| Redis Pub/Sub | | ✓ | ✓ | ✓ |
| Redis Bitmap/HLL | | | | ✓ |
| Redis 滑动窗口 | | ✓ | | |
| 微服务拆分 | ✓ | ✓ | ✓ | ✓ |
| 事件驱动(outbox) | | ✓ | ✓ | |
| XXL-Job | | | ✓ | ✓ |
| SkyWalking | ✓ | ✓ | ✓ | ✓ |

### 建议起步

先做 **模块 2（实时告警引擎）**，因为它和当前工单系统连接最直接——告警 → 自动创建工单 → 指派员工 → 处理 → 通知客户。Redis Pub/Sub、Stream、WebSocket 都可以在现有单体里先试，不需要拆服务。

---

## Redis 深度使用（不改架构，当前就能做）

| 优化项 | 方案 | 收益 |
|--------|------|------|
| 产品详情缓存 | Cache-Aside，TTL 30min | 减少 DB 读 |
| 首页数据预热 | DataInitializer 预加载到 Redis | 首页零 DB 查询 |
| AI 流式输出 | Redis Stream 替代前端模拟打字 | 真流式 + 断线续读 |
| 防重复提交 | Redisson @RedissonLock | 留言/工单防双击 |
