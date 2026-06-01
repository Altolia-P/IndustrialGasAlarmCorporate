# 设备监控平台 — 后端模块设计

> 基于 FUNCTIONAL_SPEC_DEVICE_PLATFORM.md | 2026-05-27

---

## 1. 架构决策

### 1.1 是否拆微服务

**当前阶段不拆**。设备相关模块作为新的领域上下文，与现有 8 个上下文并存于同一个 Spring Boot 单体中。

理由：
- 模拟器本身就要独立进程（方案 B），已经验证了进程隔离
- 告警引擎先在单体里跑通，后续拆出去只需改 Redis Stream 消费端部署位置
- 通知服务先做站内消息，不依赖外部 API，无需独立

### 1.2 模块结构

```
Code/backend/IndustrialGasAlarmCorporate/
├── pom.xml                                    ← 父 POM（改为多模块）
│
├── industrial-gas-web/                        ← 现有全部代码移入此子模块
│   └── pom.xml
│
├── industrial-gas-simulator/                  ← 设备模拟器（独立进程）
│   └── pom.xml
│
└── industrial-gas-common/                     ← 共享模型（可选，后期）
    └── pom.xml
```

多模块拆分是一次性的 POM 重构，改动集中且可逆。`industrial-gas-web` 是当前单模块改名，所有 Java 文件路径不变。

---

## 2. Domain 层（4 个新上下文）

### 2.1 Device 上下文

```
domain/device/
├── Device.java                # 聚合根
├── DeviceStatus.java          # 枚举: NORMAL / ABNORMAL / OFFLINE / MAINTENANCE
├── GasType.java               # 枚举: CH4 / H2S / CO / NH3 / O2 / OTHER
├── DeviceDataPoint.java       # 值对象: 一次上报数据
└── DeviceRepository.java      # 仓储接口
```

#### Device.java

```java
public class Device {
    // 标识
    private final String deviceUuid;        // UUID v4
    private final String serialNumber;      // 硬件序列号，唯一

    // 基本属性
    private String name;                    // 别名
    private String model;                   // 型号
    private String customerUuid;            // 所属客户
    private String installLocation;         // 安装位置
    private LocalDate installDate;          // 安装日期

    // 检测参数
    private GasType gasType;                // 气体类型
    private BigDecimal rangeMin;            // 量程下限 ppm
    private BigDecimal rangeMax;            // 量程上限 ppm
    private BigDecimal alertThreshold;      // 告警阈值 ppm

    // 运行时状态
    private DeviceStatus status;            // NORMAL / ABNORMAL / OFFLINE / MAINTENANCE

    // 审计
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── 构造器 ──
    // 工厂构造：new Device(serialNumber, model, customerUuid, gasType, ...)
    //    → deviceUuid = UUID.randomUUID(), status = NORMAL
    // 还原构造：全参（Repository 从 DB 还原用）

    // ── 领域行为 ──
    public void markAbnormal()      // status → ABNORMAL
    public void markNormal()        // status → NORMAL（告警解除后）
    public void markOffline()       // 超过 N 秒无数据 → OFFLINE
    public void markOnline()        // 恢复上报 → NORMAL
    public void startMaintenance()  // status → MAINTENANCE
    public void endMaintenance()    // status → NORMAL
    public void update(...)         // 编辑基本信息
}
```

#### DeviceDataPoint.java（值对象）

```java
public class DeviceDataPoint {
    private final String deviceUuid;
    private final LocalDateTime timestamp;
    private final BigDecimal concentration;
    private final BigDecimal battery;
    private final BigDecimal temperature;
    private final BigDecimal humidity;
    private final int signalStrength;
    // 全参构造 + getter，无 setter
}
```

#### DeviceRepository.java

```java
public interface DeviceRepository {
    Optional<Device> findById(String deviceUuid);
    Optional<Device> findBySerialNumber(String serialNumber);  // 唯一性校验
    void save(Device device);
    void deleteById(String deviceUuid);
    Page<Device> findAllWithFilter(String customerUuid, String model,
                                    String gasType, String status,
                                    int page, int size);
    List<Device> findByCustomerUuid(String customerUuid);      // 客户查看自己设备
    List<Device> findAllOnline();                              // 看板统计
    long countByStatus(DeviceStatus status);                   // 统计卡片
}
```

---

### 2.2 Alert 上下文

```
domain/alert/
├── Alert.java                 # 聚合根（告警记录）
├── AlertRule.java             # 实体（告警规则）
├── AlertSeverity.java         # 枚举: INFO / WARN / CRITICAL
├── AlertStatus.java           # 枚举: PENDING → CONFIRMED → RESOLVED / CLOSED
├── AlertRuleType.java         # 枚举: THRESHOLD / RAPID_RISE / OFFLINE / LOW_BATTERY
├── AlertRepository.java       # 告警记录仓储接口
└── AlertRuleRepository.java   # 告警规则仓储接口
```

#### Alert.java

```java
public class Alert {
    private final String alertUuid;
    private final String deviceUuid;
    private final String ruleUuid;
    private final AlertRuleType ruleType;
    private final AlertSeverity severity;
    private final BigDecimal concentration;    // 触发时的浓度值
    private final BigDecimal threshold;        // 触发时的阈值
    private final String message;              // 告警描述（由引擎生成）

    private AlertStatus status;                // PENDING → CONFIRMED → RESOLVED / CLOSED
    private String workOrderUuid;              // 关联工单，可为 null
    private String confirmedBy;                // 确认人
    private LocalDateTime confirmedAt;
    private String closedBy;                   // 关闭人
    private String closeReason;
    private LocalDateTime closedAt;
    private final LocalDateTime triggeredAt;

    // ── 领域行为 ──
    public void confirm(String username)       // PENDING → CONFIRMED
    public void resolve()                      // → RESOLVED（工单完成时联动）
    public void close(String username, String reason)  // → CLOSED
    public void linkWorkOrder(String workOrderUuid)    // 关联工单
}
```

#### AlertRule.java

```java
public class AlertRule {
    private final String ruleUuid;
    private String deviceUuid;                // null 表示全局默认规则
    private AlertRuleType ruleType;
    private BigDecimal threshold;             // 阈值（浓度/电压/增幅百分比）
    private int duration;                     // 持续多少秒后触发
    private AlertSeverity severity;
    private boolean autoCreateWorkOrder;      // 触发后是否自动创建工单
    private boolean enabled;                  // 是否启用
    // 构造器 + getter/setter
}
```

#### 仓储接口

```java
public interface AlertRepository {
    Optional<Alert> findById(String alertUuid);
    void save(Alert alert);
    Page<Alert> findAllWithFilter(String deviceUuid, String severity,
                                   String status, int page, int size);
    List<Alert> findByDeviceUuid(String deviceUuid, int limit);
    boolean existsSuppressed(String deviceUuid, String ruleUuid,
                              LocalDateTime since);  // 重复抑制查询
}

public interface AlertRuleRepository {
    List<AlertRule> findByDeviceUuid(String deviceUuid);
    List<AlertRule> findGlobalRules();                // deviceUuid == null
    Optional<AlertRule> findById(String ruleUuid);
    void save(AlertRule rule);
    void deleteById(String ruleUuid);
}
```

---

### 2.3 Notification 上下文

```
domain/notification/
├── Notification.java            # 聚合根
├── NotificationChannel.java     # 枚举: IN_APP / SMS / EMAIL
├── NotificationStatus.java      # 枚举: PENDING / SENT / DELIVERED / FAILED
└── NotificationRepository.java  # 仓储接口
```

#### Notification.java

```java
public class Notification {
    private final String notificationUuid;
    private final String alertUuid;
    private final String recipient;          // 接收人标识（手机号/邮箱/userUuid）
    private final NotificationChannel channel;
    private final String content;            // 通知内容
    private NotificationStatus status;
    private int retryCount;
    private String errorMessage;
    private LocalDateTime sentAt;
    private final LocalDateTime createdAt;

    // ── 领域行为 ──
    public void markSent()
    public void markFailed(String error)
    public void incrementRetry()
    public boolean canRetry()                // retryCount < 3
}
```

---

### 2.4 上下文关系图

```
Device ──1:N──→ DeviceDataPoint（值对象，Redis/时序库存储）
Device ──1:N──→ AlertRule（告警规则）
Device ──1:N──→ Alert（告警记录）
Alert  ──1:1──→ WorkOrder（已有模块，可选关联）
Alert  ──1:N──→ Notification（通知记录）
Alert  ──N:1──→ AlertRule（触发规则）
```

---

## 3. Application 层

### 3.1 Service 接口

```java
// ── 设备管理 ──
public interface DeviceService {
    DeviceVO createDevice(CreateDeviceDTO dto);
    DeviceVO updateDevice(String deviceUuid, UpdateDeviceDTO dto);
    void deleteDevice(String deviceUuid);
    DeviceDetailVO getDevice(String deviceUuid);
    Page<DeviceVO> findDevices(String customerUuid, String model,
                                String gasType, String status,
                                int page, int size);
    List<DeviceVO> findMyDevices(String customerUuid);
}

// ── 数据采集 ──
public interface IngestionService {
    void ingest(DeviceDataDTO dto);
    // 处理流程：
    //   1. 校验 deviceUuid 存在性
    //   2. 写入 Redis Stream: device:data:stream
    //   3. 更新 Redis Hash: device:latest:{uuid}
    //   4. 更新 Redis Sorted Set: device:window:{uuid}
    //   5. 调用 AlertEngine.evaluate(deviceUuid)
    //   6. 异步写 MySQL（批量或定时刷）
}

// ── 告警引擎 ──
public interface AlertEngine {
    void evaluate(String deviceUuid);       // 消费一条数据后触发评估
    AlertVO fireAlert(String deviceUuid, AlertRule rule,
                       BigDecimal concentration);  // 触发告警
    boolean isSuppressed(String deviceUuid, String ruleUuid);  // 重复抑制
}

// ── 告警管理 ──
public interface AlertService {
    Page<AlertVO> findAlerts(String deviceUuid, String severity,
                              String status, int page, int size);
    AlertDetailVO getAlert(String alertUuid);
    void confirmAlert(String alertUuid, String username);
    void closeAlert(String alertUuid, String username, String reason);
    void resolveByWorkOrder(String workOrderUuid);  // 工单完成时联动
    List<AlertVO> findMyDeviceAlerts(String deviceUuid, String customerUuid,
                                      int page, int size);
}

// ── 告警规则 ──
public interface AlertRuleService {
    List<AlertRuleVO> getRules(String deviceUuid);
    AlertRuleVO createRule(String deviceUuid, CreateAlertRuleDTO dto);
    AlertRuleVO updateRule(String ruleUuid, UpdateAlertRuleDTO dto);
    void deleteRule(String ruleUuid);
}

// ── 通知服务 ──
public interface NotificationService {
    void notifyAlertCreated(Alert alert);   // 告警触发 → 按严重级别选渠道
    void notifyAlertResolved(Alert alert);  // 告警解除 → 通知客户
    void retryFailedNotifications();        // 定时重试失败通知
    List<NotificationVO> findByAlertUuid(String alertUuid);
}

// ── 看板统计 ──
public interface DashboardService {
    DeviceOverviewStats getDeviceOverview();
    TodayStats getTodayStats();
    List<AlertTrendItem> getAlertTrend(int days);
    List<AlertVO> getRealtimeAlerts();
}
```

### 3.2 DTO / VO

```
application/device/
├── dto/
│   ├── CreateDeviceDTO.java
│   ├── UpdateDeviceDTO.java
│   └── DeviceDataDTO.java        # IngestionService 数据接收
├── vo/
│   ├── DeviceVO.java             # 列表项
│   ├── DeviceDetailVO.java       # 详情（含告警规则列表）
│   └── DeviceDataPointVO.java    # 数据点（给前端画图）

application/alert/
├── dto/
│   ├── CreateAlertRuleDTO.java
│   ├── UpdateAlertRuleDTO.java
│   └── CloseAlertDTO.java
├── vo/
│   ├── AlertVO.java              # 列表项
│   ├── AlertDetailVO.java        # 详情（含数据点 + 关联工单摘要）
│   └── AlertRuleVO.java

application/dashboard/
├── vo/
│   ├── DeviceOverviewStats.java   # { total, online, offline, abnormal }
│   ├── TodayStats.java            # { dataPointCount, alertCount, ... }
│   └── AlertTrendItem.java        # { date, infoCount, warnCount, criticalCount }
```

---

## 4. Infrastructure 层

### 4.1 持久化 — PO + Mapper + RepositoryImpl

```
infrastructure/repository/
├── po/
│   ├── DevicePO.java
│   ├── DeviceDataPointPO.java       # 也存 MySQL（近期数据），时序库后期替换
│   ├── AlertPO.java
│   ├── AlertRulePO.java
│   └── NotificationPO.java
├── mapper/
│   ├── DeviceMapper.java
│   ├── DeviceDataPointMapper.java
│   ├── AlertMapper.java
│   ├── AlertRuleMapper.java
│   └── NotificationMapper.java
└── impl/
    ├── DeviceRepositoryImpl.java
    ├── AlertRepositoryImpl.java
    ├── AlertRuleRepositoryImpl.java
    └── NotificationRepositoryImpl.java
```

PO 继承现有的审计字段 + `@Version` 乐观锁 + `@TableLogic` 逻辑删除模式。

### 4.2 Redis — 新增 5 个 Repository

```
infrastructure/redis/
├── DeviceStatusRepository.java    # device:online:{uuid}      — 在线状态
├── DeviceDataStreamRepository.java # device:data:stream       — Stream 写入/消费
├── DeviceWindowRepository.java    # device:window:{uuid}      — Sorted Set 滑动窗口
├── AlertSuppressRepository.java   # alert:suppress:{d}:{r}    — 重复抑制
└── DashboardStatsRepository.java  # dashboard:today:*         — 今日统计计数
```

#### 各 Repository 规格

**DeviceStatusRepository**
```java
// Key: device:online:{deviceUuid}
// Value: Hash { status, lastConcentration, lastBattery, lastReportTime }
// TTL: 无（持久）
// 方法：
//   updateStatus(deviceUuid, status, concentration, battery)
//   getStatus(deviceUuid) → Optional<DeviceOnlineStatus>
//   getAllOnlineDeviceUuids() → Set<String>
//   markOffline(deviceUuid)   // 超过 N 秒无数据时由定时任务调
```

**DeviceDataStreamRepository**
```java
// Key: device:data:stream
// 方法：
//   add(DeviceDataPoint)                   → StreamMessageId
//   readGroup(consumerGroup, consumerName)  → List<StreamMessage>
//   ack(consumerGroup, messageId)
//   trim(maxLen = 10000)                    // 保留最近 1 万条
// Consumer Group: alert-engine（告警引擎消费）
```

**DeviceWindowRepository**
```java
// Key: device:window:{deviceUuid}
// Value: Sorted Set, score = epoch秒, member = 浓度值
// TTL: 120s（key 自动过期）
// 方法：
//   addReading(deviceUuid, timestamp, concentration)
//   getWindow(deviceUuid, seconds) → List<Reading>  // 最近 N 秒读数
//   avgConcentration(deviceUuid, seconds) → BigDecimal
//   minConcentration(deviceUuid, seconds) → BigDecimal
```

**AlertSuppressRepository**
```java
// Key: alert:suppress:{deviceUuid}:{ruleUuid}
// Value: "1"
// TTL: 5min
// 方法：
//   setSuppress(deviceUuid, ruleUuid)     // 设置抑制标记
//   isSuppressed(deviceUuid, ruleUuid) → boolean
```

**DashboardStatsRepository**
```java
// Key: dashboard:today:alerts, dashboard:today:dataPoints, ...
// Value: String（INCR 原子递增）
// TTL: 当天 23:59:59
// 方法：
//   incrementAlertCount()
//   incrementDataPointCount()
//   getTodayAlertCount() → long
//   getTodayDataPointCount() → long
```

### 4.3 告警引擎实现 — `AlertEngineImpl.java`

核心逻辑（单进程内）：

```java
@Service
public class AlertEngineImpl implements AlertEngine {

    // 被 IngestionService.ingest() 调用
    @Override
    public void evaluate(String deviceUuid) {
        Device device = deviceRepository.findById(deviceUuid).orElseThrow();
        if (device.getStatus() == DeviceStatus.MAINTENANCE) return;  // 维护抑制

        List<AlertRule> rules = ruleRepository.findByDeviceUuid(deviceUuid);
        rules.addAll(ruleRepository.findGlobalRules());

        for (AlertRule rule : rules) {
            if (!rule.isEnabled()) continue;
            if (isSuppressed(deviceUuid, rule.getRuleUuid())) continue;

            switch (rule.getRuleType()) {
                case THRESHOLD   → evaluateThreshold(deviceUuid, rule);
                case RAPID_RISE  → evaluateRapidRise(deviceUuid, rule);
                case OFFLINE     → evaluateOffline(deviceUuid, rule);
                case LOW_BATTERY → evaluateLowBattery(deviceUuid, rule);
            }
        }
    }

    private void evaluateThreshold(String deviceUuid, AlertRule rule) {
        // 1. 从 Redis Sorted Set 取最近 duration 秒读数
        // 2. 如果所有读数 > threshold → 触发告警
        // 3. 否则不触发
        List<Reading> window = windowRepository.getWindow(deviceUuid, rule.getDuration());
        if (window.isEmpty()) return;

        boolean allAboveThreshold = window.stream()
            .allMatch(r -> r.concentration.compareTo(rule.getThreshold()) > 0);
        if (allAboveThreshold) {
            BigDecimal latestConcentration = window.get(window.size() - 1).concentration;
            fireAlert(deviceUuid, rule, latestConcentration);
        }
    }

    private void evaluateRapidRise(String deviceUuid, AlertRule rule) {
        // 窗口首尾浓度对比：(latest - earliest) / earliest > threshold/100
    }

    private void evaluateOffline(String deviceUuid, AlertRule rule) {
        // Redis device:online:{uuid} 的 lastReportTime 与当前时间差 > duration
    }

    private void evaluateLowBattery(String deviceUuid, AlertRule rule) {
        // Redis device:online:{uuid} 的 lastBattery < threshold
    }

    @Override
    public AlertVO fireAlert(String deviceUuid, AlertRule rule, BigDecimal concentration) {
        // 1. 创建 Alert 聚合根
        // 2. 保存到 DB
        // 3. 设置抑制标记（5min）
        // 4. 更新设备状态 → ABNORMAL
        // 5. 如果 rule.autoCreateWorkOrder → 创建工单 + 关联
        // 6. 发布 DomainEvent（AlertCreatedEvent）→ 通知服务监听 + WebSocket 推送
        // 7. 返回 AlertVO
    }
}
```

### 4.4 通知服务实现 — `NotificationServiceImpl.java`

```java
@Service
public class NotificationServiceImpl implements NotificationService {

    @EventListener
    public void onAlertCreated(AlertCreatedEvent event) {
        // 告警创建后异步通知
        Alert alert = alertRepository.findById(event.getAlertUuid()).orElseThrow();
        sendByChannel(alert);
    }

    private void sendByChannel(Alert alert) {
        Device device = deviceRepository.findById(alert.getDeviceUuid()).orElseThrow();

        // IN_APP: 直接写 Notification 表 → 前端轮询展示
        if (alert.getSeverity() != AlertSeverity.CRITICAL) {
            saveInAppNotification(alert, device);
            return;
        }

        // CRITICAL: 短信 + 站内
        // 短信：防骚扰检查 → 调 SMS 适配器 → 记录发送状态
        // 站内：同上
    }

    @Scheduled(fixedDelay = 60000)  // 每分钟重试
    @Override
    public void retryFailedNotifications() {
        List<Notification> failed = notificationRepository.findFailedAndRetryable();
        for (Notification n : failed) {
            n.incrementRetry();
            // 重新发送...
        }
    }
}
```

初期 SMS/Email 适配器用接口 + Stub 实现（打日志代替），后续接入真实 API。

### 4.5 新增配置

```yaml
# application.yml 新增段
device:
  simulator:
    enabled: false              # 生产环境必须为 false
  ingestion:
    stream-max-len: 10000       # Redis Stream 最大长度
  alert:
    suppress-minutes: 5         # 重复告警抑制时间
    offline-timeout-seconds: 120  # 离线判定超时
  notification:
    sms:
      provider: stub            # stub / aliyun / tencent
    email:
      provider: stub
```

---

## 5. Interface 层

### 5.1 Controller 一览

```
interfaces/
├── admin/
│   ├── AdminDeviceController.java       # /api/v1/admin/devices
│   ├── AdminAlertController.java        # /api/v1/admin/alerts
│   └── AdminDashboardController.java    # /api/v1/admin/dashboard（扩展）
├── pub/
│   └── IngestionController.java         # /api/v1/public/ingestion/device-data（设备上报）
├── user/
│   └── UserDeviceController.java        # /api/v1/user/devices（客户设备）
└── staff/
    └── StaffAlertController.java        # /api/v1/staff/alerts（员工告警）
```

### 5.2 端点清单

#### 设备上报（公开，设备/模拟器调用）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/public/ingestion/device-data` | 接收单条设备数据 |

> SecurityConfig 需放行此路径。通过 API Key / 设备 Token 鉴权（后期，初期 permitAll）。

#### 客户设备（USER 角色）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/user/devices` | 我的设备列表 |
| GET | `/api/v1/user/devices/{uuid}` | 设备详情 |
| GET | `/api/v1/user/devices/{uuid}/data` | 设备数据点（?range=1h\|6h\|24h\|7d） |
| GET | `/api/v1/user/devices/{uuid}/alerts` | 设备告警历史 |

#### 管理后台（ADMIN 角色）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/admin/devices` | 设备列表（筛选+分页） |
| POST | `/api/v1/admin/devices` | 录入设备 |
| GET | `/api/v1/admin/devices/{uuid}` | 设备详情 |
| PUT | `/api/v1/admin/devices/{uuid}` | 编辑设备 |
| DELETE | `/api/v1/admin/devices/{uuid}` | 删除设备 |
| GET | `/api/v1/admin/devices/{uuid}/rules` | 设备告警规则 |
| POST | `/api/v1/admin/devices/{uuid}/rules` | 新增规则 |
| PUT | `/api/v1/admin/alerts/rules/{uuid}` | 编辑规则 |
| DELETE | `/api/v1/admin/alerts/rules/{uuid}` | 删除规则 |
| GET | `/api/v1/admin/alerts` | 告警列表（筛选+分页） |
| GET | `/api/v1/admin/alerts/{uuid}` | 告警详情 |
| PUT | `/api/v1/admin/alerts/{uuid}/confirm` | 确认告警 |
| PUT | `/api/v1/admin/alerts/{uuid}/close` | 关闭告警 |
| GET | `/api/v1/admin/dashboard/device-overview` | 设备统计 |
| GET | `/api/v1/admin/dashboard/today-stats` | 今日统计 |
| GET | `/api/v1/admin/dashboard/alert-trend` | 告警趋势 |
| GET | `/api/v1/admin/dashboard/realtime-alerts` | 实时告警 |

#### 员工（STAFF 角色）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/staff/alerts` | 指派给我的告警（通过工单关联） |

---

## 6. 设备模拟器（独立模块）

### 6.1 模块结构

```
industrial-gas-simulator/
├── pom.xml
└── src/main/java/com/niit/simulator/
    ├── SimulatorApplication.java         # @SpringBootApplication + main()
    ├── SimulatorConfig.java              # 模拟参数配置
    ├── SimulatorProperties.java          # @ConfigurationProperties
    ├── DeviceSimulator.java              # @Scheduled 定时上报
    ├── DataGenerator.java                # 随机数据生成（含异常概率）
    ├── IngestionClient.java              # HTTP 客户端 → 调主服务
    └── controller/
        └── SimulatorController.java      # 手动操控接口
```

### 6.2 核心逻辑

```java
@Component
public class DeviceSimulator {

    private final SimulatorProperties props;      // 设备列表、间隔、概率
    private final DataGenerator generator;
    private final IngestionClient client;

    @Scheduled(fixedDelayString = "${simulator.interval-ms:30000}")
    public void simulateTick() {
        for (String deviceId : props.getDeviceIds()) {
            DeviceDataDTO data = generator.generate(deviceId, props.getAnomalyRate());
            try {
                client.send(data);
                log.info("Simulator: {} → concentration={}, battery={}",
                         deviceId, data.getConcentration(), data.getBattery());
            } catch (Exception e) {
                log.warn("Simulator: {} → FAILED ({})", deviceId, e.getMessage());
            }
        }
    }
}
```

### 6.3 手动操控接口（开发/测试用）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/simulator/device/{deviceId}/anomaly` | 下次上报生成异常浓度 |
| POST | `/simulator/device/{deviceId}/offline` | 模拟断线（暂停此设备上报） |
| POST | `/simulator/device/{deviceId}/online` | 恢复上报 |
| POST | `/simulator/batch` | 调整参数：数量、间隔、异常概率 |
| GET | `/simulator/status` | 当前状态：设备数、间隔、运行时长、上报次数 |

### 6.4 模拟器配置

```yaml
# simulator application.yml
simulator:
  device-count: 50
  device-id-prefix: DEV-
  interval-ms: 30000               # 30 秒上报间隔
  anomaly-rate: 0.05               # 5% 概率生成异常读数
  offline-rate: 0.02               # 2% 概率模拟断线
  concentration-normal-min: 0
  concentration-normal-max: 15     # 正常范围 0-15 ppm
  concentration-anomaly-factor: 2.5 # 异常时浓度 = 阈值 * factor
  ingestion-url: http://localhost:8080/api/v1/public/ingestion/device-data
```

---

## 7. 数据流总结

```
                设备模拟器 (独立进程)
                      │
                      │ HTTP POST /ingestion/device-data
                      ▼
              ┌─ IngestionController ─┐
              │  校验 deviceUuid       │
              │  ↓                     │
              │  IngestionService      │
              │   ├→ Redis Stream      │  ← 原始数据流
              │   ├→ Redis Hash        │  ← 最新读数缓存
              │   ├→ Redis SortedSet   │  ← 滑动窗口
              │   ├→ AlertEngine       │  ← 告警评估
              │   └→ MySQL（异步）     │  ← 持久化
              └──────────────────────┘
                      │
                      ▼ AlertEngine
              ┌──────────────────────┐
              │  滑动窗口判断         │
              │  重复抑制检查         │
              │  ↓ 触发               │
              │  Alert 保存 DB        │
              │  ↓ autoCreate         │
              │  WorkOrder 创建       │  ← 联动机工单模块
              │  ↓ 事件发布           │
              │  NotificationService  │  ← 异步通知
              │  ↓                    │
              │  WebSocket 推送       │  ← 前端实时弹窗
              └──────────────────────┘
```

---

## 8. 与现有代码的集成点

| 集成点 | 方式 | 说明 |
|--------|------|------|
| SecurityConfig | 新增放行 `/api/v1/public/ingestion/**` | 设备上报不需要认证 |
| WorkOrderService | 新增方法 `createFromAlert(...)` | 或复用现有 createWorkOrder |
| EventBus | 新增 `AlertCreatedEvent` / `AlertResolvedEvent` | 通知服务监听 |
| DataInitializer | 可插入演示设备数据 | 开发环境快速验证 |
| pom.xml | 改为多模块 POM | 父 POM → web + simulator 子模块 |

---

## 9. 新增文件清单（~45 个）

```
Domain 层（~15 个）：
  domain/device/  Device, DeviceStatus, GasType, DeviceDataPoint, DeviceRepository
  domain/alert/   Alert, AlertRule, AlertSeverity, AlertStatus, AlertRuleType,
                  AlertRepository, AlertRuleRepository
  domain/notification/  Notification, NotificationChannel, NotificationStatus,
                         NotificationRepository

Application 层（~20 个）：
  application/device/  dto/{CreateDeviceDTO, UpdateDeviceDTO, DeviceDataDTO},
                        vo/{DeviceVO, DeviceDetailVO, DeviceDataPointVO}
  application/alert/   dto/{CreateAlertRuleDTO, UpdateAlertRuleDTO, CloseAlertDTO},
                        vo/{AlertVO, AlertDetailVO, AlertRuleVO}
  application/dashboard/  vo/{DeviceOverviewStats, TodayStats, AlertTrendItem}
  service/  DeviceService, IngestionService, AlertEngine, AlertService,
             AlertRuleService, NotificationService, DashboardService

Infrastructure 层（~15 个）：
  po/  DevicePO, DeviceDataPointPO, AlertPO, AlertRulePO, NotificationPO
  mapper/  对应的 5 个 Mapper
  impl/  对应的 5 个 RepositoryImpl（Device/DeviceData/Alert/AlertRule/Notification）
  redis/  DeviceStatusRepository, DeviceDataStreamRepository, DeviceWindowRepository,
           AlertSuppressRepository, DashboardStatsRepository
  config/ AlertEngineConfig（可选）

Interface 层（5 个）：
  AdminDeviceController, AdminAlertController, AdminDashboardController（扩展）
  IngestionController, UserDeviceController

模拟器模块（~6 个）：
  SimulatorApplication, SimulatorConfig, SimulatorProperties,
  DeviceSimulator, DataGenerator, IngestionClient, SimulatorController

保留文件：
  pom.xml（改为多模块）
  SecurityConfig.java（新增放行路径）
  DataInitializer.java（可选：演示数据）
```
