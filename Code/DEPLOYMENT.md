# 工业气体报警企业系统 — 阿里云 ECS 部署全记录

> 部署日期：2026-06-04 | 目标服务器：139.224.190.132 (4GB RAM Alibaba Cloud ECS)

---

## 目录

1. [环境概览](#1-环境概览)
2. [Step 1 — 配置文件准备](#2-step-1--配置文件准备)
3. [Step 2 — 启动服务栈](#3-step-2--启动服务栈)
4. [Step 3 — Collector Redis AUTH 崩溃](#4-step-3--collector-redis-auth-崩溃)
5. [Step 4 — 服务器内存不足](#5-step-4--服务器内存不足)
6. [Step 5 — Dashboard 历史页面无数据](#6-step-5--dashboard-历史页面无数据)
7. [Step 6 — 时区不匹配（1h/6h 无数据）](#7-step-6--时区不匹配1h6h-无数据)
8. [Step 7 — 部署缓存问题（页面变纯文字）](#8-step-7--部署缓存问题页面变纯文字)
9. [Step 8 — 历史告警面板无数据](#9-step-8--历史告警面板无数据)
10. [常用运维命令](#10-常用运维命令)
11. [最终状态](#11-最终状态)

---

## 1. 环境概览

### 服务器

| 项目 | 值 |
|------|-----|
| 类型 | 阿里云 ECS |
| IP | 139.224.190.132 |
| OS | Ubuntu (Docker 环境) |
| 内存 | 4GB（从 2GB 升级） |
| 项目路径 | `/opt/igas/Code/` |

### Docker Compose 服务栈（7 容器）

```
┌──────────┐  ┌──────────┐  ┌──────────┐
│  MySQL   │  │  Redis   │  │ RabbitMQ │
│  8.0     │  │  7-alpine│  │  3.13    │
│  :3306   │  │  :6379   │  │  :5672   │
└────┬─────┘  └────┬─────┘  └────┬─────┘
     │             │             │
     └─────────────┼─────────────┘
                   │
     ┌─────────────┼─────────────┐
     │             │             │
┌────┴─────┐ ┌────┴─────┐ ┌────┴─────┐
│   App    │ │Collector │ │Simulator │
│  :8080   │ │  :8081   │ │  :8082   │
│ 主应用   │ │ 数据采集 │ │ 设备模拟 │
└────┬─────┘ └──────────┘ └──────────┘
     │
┌────┴─────┐
│  Nginx   │
│  :8088→80│
│ 前端+代理│
└──────────┘
```

### 关键文件清单

| 文件 | 作用 |
|------|------|
| `Code/docker-compose.yml` | 7 容器编排定义 |
| `Code/.env` | 数据库密码、JWT 密钥等敏感配置 |
| `Code/docker/config/collector/application-docker.yml` | Collector Redis 覆盖配置 |
| `Code/docker/config/app/application-docker.yml` | App 覆盖配置 |
| `Code/backend/.../app/Dockerfile` | App 多阶段构建 |
| `Code/backend/.../device-collector/Dockerfile` | Collector 构建 |
| `Code/backend/.../device-simulator/Dockerfile` | Simulator 构建 |
| `Code/frontend/Dockerfile` | 前端多阶段构建（Node build → Nginx） |
| `Code/frontend/nginx.conf` | Nginx 静态文件 + API 反向代理 |

---

## 2. Step 1 — 配置文件准备

### 2.1 `.env` 环境变量

```bash
DB_ROOT_PASSWORD=yueyang20041202
JWT_SECRET=RBli/+6j4X8V8u/kmAl/i0K8etWTsgblBWWL9zP08i2jPajge+iAvwhrh7KlZkm8
API_TOKEN_SHARED_KEY=38376fd8f4224ad974587accb18136cbbb81a700abbbf640a66b775f04c5c478
DEEPSEEK_API_KEY=sk-1eb87cbbcbcd4eb29a4312ebd359e005
RABBITMQ_USER=guest
RABBITMQ_PASS=guest
```

### 2.2 Collector Docker 配置

**文件**：`Code/docker/config/collector/application-docker.yml`

覆盖默认配置，关键点：
- 关闭 Nacos（Docker 环境不需要服务发现）
- 显式指定 Redisson 单节点模式（解决 AUTH 问题，见 Step 3）

```yaml
spring:
  cloud:
    nacos:
      discovery:
        enabled: false
      config:
        enabled: false
  redis:
    host: ${REDIS_HOST:redis}
    port: ${REDIS_PORT:6379}
    database: 1
    redisson:
      config: |
        singleServerConfig:
          address: "redis://${REDIS_HOST:redis}:${REDIS_PORT:6379}"
          database: 1
```

### 2.3 Nginx 配置

**文件**：`Code/frontend/nginx.conf`

```nginx
server {
    listen 80;
    resolver 127.0.0.11 valid=30s ipv6=off;  # Docker 内部 DNS

    # API 反向代理 → App 容器
    location /api/ {
        set $app app:8080;
        proxy_pass http://$app;
    }

    # Vue Router history 模式 — HTML 禁止缓存
    location / {
        try_files $uri $uri/ /index.html;
        add_header Cache-Control "no-cache, no-store, must-revalidate";
    }

    # 静态资源长期缓存（带 hash，文件名变即失效）
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf)$ {
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
```

---

## 3. Step 2 — 启动服务栈

### 命令

```bash
# 在 /opt/igas/Code/ 目录下
docker compose up -d
```

### 启动顺序（由 depends_on + healthcheck 保证）

```
MySQL (healthcheck: mysqladmin ping)
  ↓
Redis (healthcheck: redis-cli ping)
  ↓
RabbitMQ (healthcheck: rabbitmq-diagnostics ping)
  ↓
App (8080) → Collector (8081)
  ↓
Simulator (8082) → Nginx (80→8088)
```

### 验证

```bash
# 检查所有容器状态
docker ps --format '{{.Names}} {{.Status}}'

# 预期输出：
# igas-mysql    Up XX minutes (healthy)
# igas-redis    Up XX minutes (healthy)
# igas-rabbitmq Up XX minutes (healthy)
# igas-app      Up XX minutes
# igas-collector Up XX minutes
# igas-simulator Up XX minutes
# igas-nginx    Up XX minutes
```

---

## 4. Step 3 — Collector Redis AUTH 崩溃

### 现象

Collector 容器反复崩溃重启，日志报：

```
ERR AUTH <password> called without any password configured for the default user
```

### 排查方法

```bash
# 查看 collector 日志
docker logs igas-collector --tail 50

# 检查 Redis 是否有密码
docker exec igas-redis redis-cli CONFIG GET requirepass
# 返回空 → 无密码
```

### 根因分析

`redisson-spring-boot-starter` 3.40.2 从 `spring.redis.*` 命名空间读取配置。Collector 的 `application-docker.yml` 原来只配了 `spring.data.redis.*`，没有显式 password 字段。在某些条件下 Redisson 错误地向无密码 Redis 发送了 AUTH 命令。

**关键点**：Redisson 的配置命名空间是 `spring.redis.*`，不是 `spring.data.redis.*`。两者的区分很重要：
- `spring.data.redis.*` → Spring Data Redis（Lettuce/Jedis）
- `spring.redis.*` → Redisson starter

### 修复

在 `Code/docker/config/collector/application-docker.yml` 中添加显式 Redisson YAML 配置：

```yaml
spring:
  redis:
    redisson:
      config: |
        singleServerConfig:
          address: "redis://${REDIS_HOST:redis}:${REDIS_PORT:6379}"
          database: 1
```

### 部署

```bash
# 本地编辑后上传
scp Code/docker/config/collector/application-docker.yml root@139.224.190.132:/opt/igas/Code/docker/config/collector/

# 重启 collector
ssh root@139.224.190.132 "cd /opt/igas/Code && docker compose up -d collector"
```

### 验证

```bash
# Collector 日志应有 "24 connections initialized"
docker logs igas-collector 2>&1 | grep -i "connection"
```

---

## 5. Step 4 — 服务器内存不足

### 现象

- SSH 连接超时，无法登录
- 容器状态异常

### 内存评估

| 组件 | 预估内存 |
|------|----------|
| MySQL 8.0 | ~400 MB |
| Redis 7 | ~30 MB（maxmemory 32mb） |
| RabbitMQ 3.13 | ~200 MB |
| App JVM | ~300 MB |
| Collector JVM | ~250 MB |
| Simulator JVM | ~200 MB |
| Nginx | ~20 MB |
| OS 开销 | ~300 MB |
| **总计** | **~1.8 GB** |

2GB RAM 仅余 ~200MB，无 buffer/cache 空间，swap 颠簸导致系统无响应。

### 结论

不是代码质量问题，是 **JVM 微服务架构的固定成本**。Java 17 + Spring Boot 3.x 每个实例基础内存 ~200-300MB，3 个 JVM + 3 个中间件 = 1.8GB 底线。建议最低 4GB。

### 解决

用户从阿里云控制台升级到 4GB → 重启服务器 → 所有容器自动恢复。

---

## 6. Step 5 — Dashboard 历史页面无数据

### 现象

`http://139.224.190.132:8088/dashboard/history` 页面图表区域显示"请选择设备和时间范围查询"，无任何数据。Dashboard 概览页 (`/dashboard`) 正常。

### 排查过程

#### 6.1 验证数据是否存在

```bash
docker exec igas-mysql mysql -uroot -p<password> \
  -e "SELECT COUNT(*) FROM industrial_gas_alarm_collector.t_device_data_point WHERE device_uuid='demo-sim-001'"
# 结果：870 条 → 数据存在
```

#### 6.2 验证 Collector API

```bash
# 从宿主机直接访问 collector
curl http://localhost:8081/api/v1/collect/device-data?deviceUuid=demo-sim-001
# 结果：返回 JSON 数据 → Collector 正常
```

#### 6.3 验证 App → Collector Feign 通信

```bash
# 从 app 容器内部访问 collector
docker exec igas-app wget -q -O- \
  'http://collector:8081/api/v1/collect/device-data?deviceUuid=demo-sim-001'
# 结果：返回数据 → Docker 内部网络正常
```

#### 6.4 验证 Auth 配置

- `/api/v1/dashboard/**` 需要认证但不需要 ADMIN 角色
- 前端 axios 拦截器自动附加 JWT `Authorization: Bearer <token>`
- Dashboard 概览页正常工作 → JWT 认证正常

#### 6.5 定位前端问题

检查 `HistoryView.vue` 的 `loadHistory()` 函数：

```javascript
// 原代码 — 错误被静默吞掉
}).catch((e: any) => {
    loading.value = false
    // errorMsg 不存在！用户看不到任何错误信息
})
```

### 修复：添加前端错误可见性

**文件**：`Code/frontend/src/views/dashboard/HistoryView.vue`

**改动 1**：添加 errorMsg 响应式变量
```javascript
const errorMsg = ref('')
```

**改动 2**：onMounted 添加错误日志
```javascript
onMounted(async () => {
  try {
    const devs = await dashboardApi.getDevices()
    // ...
  } catch (e: any) {
    console.error('加载设备列表失败:', e)
    errorMsg.value = '加载设备列表失败: ' + (e?.message || '未知错误')
  }
})
```

**改动 3**：loadHistory catch 块设置错误信息
```javascript
}).catch((e: any) => {
    loading.value = false
    errorMsg.value = e?.message || String(e) || '加载失败'
    console.error('历史数据加载失败:', e)
})
```

**改动 4**：模板显示错误
```html
<div v-else class="chart-empty">
  <p v-if="loading">加载中...</p>
  <p v-else-if="errorMsg" style="color: #ef4444;">{{ errorMsg }}</p>
  <p v-else>请选择设备和时间范围查询</p>
</div>
```

### 部署前端

```bash
# 构建
cd Code/frontend && npm run build

# 打包上传
tar czf dist.tar.gz -C dist .
scp dist.tar.gz root@139.224.190.132:/tmp/

# 部署到 nginx 容器
ssh root@139.224.190.132 "
  docker cp /tmp/dist.tar.gz igas-nginx:/tmp/
  docker exec igas-nginx tar xzf /tmp/dist.tar.gz -C /usr/share/nginx/html
  docker exec igas-nginx rm /tmp/dist.tar.gz
  rm /tmp/dist.tar.gz
"
```

**注意**：由于 Dockerfile 用 `COPY --from=builder /app/dist` 而非 volume mount，更新需要 `docker cp` 到容器内。后续 rebuild 镜像会覆盖这些变更。

---

## 7. Step 6 — 时区不匹配（1h/6h 无数据）

### 现象

前端修复后，历史图表"最近24小时"有数据，但"最近1小时"和"最近6小时"无数据。

### 根因分析

前端 `formatLocalTime()` 函数用浏览器本地时间生成查询参数：

```javascript
// 原代码 — 使用本地时间
function formatLocalTime(date: Date): string {
  return `${date.getFullYear()}-...-${date.getHours()}:${date.getMinutes()}:${date.getSeconds()}`
}
```

假设北京时间 15:00 (UTC+8) 选"最近1小时"：
- 前端发送：`from=2026-06-04 14:00:00` `to=2026-06-04 15:00:00`（本地时间）
- 数据库存储：`recorded_at=2026-06-04 07:00:00`（UTC）
- `07:00` 不在 `14:00~15:00` 范围内 → 查询结果为空

"最近24小时"侥幸命中是因为 8 小时偏移在 24 小时窗口内仍有交集。

### 修复

**文件**：`Code/frontend/src/views/dashboard/HistoryView.vue`

```javascript
// 改为 UTC 时间
function formatLocalTime(date: Date): string {
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${date.getUTCFullYear()}-${pad(date.getUTCMonth() + 1)}-${pad(date.getUTCDate())} ${pad(date.getUTCHours())}:${pad(date.getUTCMinutes())}:${pad(date.getUTCSeconds())}`
}

// 自定义日期范围也转为 UTC
function toUtcTime(localDateTimeStr: string): string | undefined {
  if (!localDateTimeStr) return undefined
  const d = new Date(localDateTimeStr)
  if (isNaN(d.getTime())) return undefined
  return formatLocalTime(d)
}
```

在 `loadHistory()` 中将自定义日期也转换：
```javascript
if (timeRange.value === 'custom') {
    from = toUtcTime(customFrom.value)  // 原来是 customFrom.value || undefined
    to = toUtcTime(customTo.value)       // 原来是 customTo.value || undefined
}
```

---

## 8. Step 7 — 部署缓存问题（页面变纯文字）

### 现象

部署新前端后，页面变成纯文字无样式，Console 报错：

```
Failed to load resource: the server responded with a status of 404 (Not Found)
  → HistoryView-lntVD0Jq.css
Error: Unable to preload CSS for /assets/HistoryView-lntVD0Jq.css
```

### 根因分析

**两个并发原因**：

1. **误删新版 CSS**：每次构建产生新的 chunk 文件名（如 `HistoryView-LTqpP19u.js` + `HistoryView-lntVD0Jq.css`）。清理旧文件时误删了当次构建的 CSS，导致 JS 动态 import CSS 时 404。

2. **浏览器缓存旧 index.html**：HTML 文件默认无 `Cache-Control` 头，浏览器可能使用缓存的旧版 `index.html`，它引用的旧 entry JS 尝试加载已被清理的旧 chunk。

### 修复

**立即修复**：从本地 dist 单独上传缺失的 CSS 文件：
```bash
scp dist/assets/HistoryView-lntVD0Jq.css root@139.224.190.132:/tmp/
ssh root@139.224.190.132 "
  docker cp /tmp/HistoryView-lntVD0Jq.css igas-nginx:/usr/share/nginx/html/assets/
"
```

**长期修复**：nginx 配置中 HTML location 添加禁止缓存头：
```nginx
location / {
    try_files $uri $uri/ /index.html;
    add_header Cache-Control "no-cache, no-store, must-revalidate";
}
```

部署 nginx 配置变更：
```bash
scp nginx.conf root@139.224.190.132:/tmp/
ssh root@139.224.190.132 "
  docker cp /tmp/nginx.conf igas-nginx:/etc/nginx/conf.d/default.conf
  docker exec igas-nginx nginx -s reload
"
```

**教训**：清理容器内旧 chunk 时必须确认新 chunk 文件名，只删明确不匹配当前 `index.html` 引用的文件。

---

## 9. Step 8 — 历史告警面板无数据

### 现象

图表正常显示数据，但右侧"历史告警"面板始终显示"选择设备后显示告警记录"。

### 排查过程

#### 9.1 检查告警规则

```bash
docker exec igas-mysql mysql -uroot -p<password> \
  -e "SELECT rule_uuid, name, gas_type, threshold, severity FROM t_alert_rule"
```

发现的规则及覆盖率：

| 规则 | 气体 | 阈值 | 模拟器范围 | 能否触发？ |
|------|------|------|-----------|-----------|
| demo-rule-001 | CH4 | 20.0 | 0–5 | 永不触发 |
| demo-rule-006 | H2S | 10.0 | 0–8 | 永不触发 |
| demo-rule-007 | CO | 50.0 | 0–45 | 永不触发 |

三个模拟器的浓度上限全部低于告警阈值。

#### 9.2 验证告警管道

告警评估全链路：

```
Simulator → Collector (ingest)
  → EventPublisher.publishAlertEvaluate()
    → RabbitMQ alert.exchange / alert.evaluate.key
      → App AlertEvaluateConsumer (@RabbitListener)
        → AlertEngineService.evaluate()
          → 匹配规则 → RabbitMQ alert.triggered
            → AlertConsumer → 写入 t_alert
```

验证 RabbitMQ 连接：
```bash
# 检查队列
docker exec igas-rabbitmq rabbitmqctl list_queues name messages

# 检查 app 日志
docker logs igas-app | grep -i "alert\|告警"
```

### 修复

插入三条匹配模拟器范围的低阈值规则：

```sql
INSERT INTO t_alert_rule (rule_uuid, name, device_uuid, rule_type, gas_type, threshold, duration_seconds, severity, enabled)
VALUES
('sim-rule-001', '模拟器-CH4阈值告警', 'demo-sim-001', 'THRESHOLD', 'CH4', 3.5000, 10, 'WARNING', 1),
('sim-rule-002', '模拟器-H2S阈值告警', 'demo-sim-002', 'THRESHOLD', 'H2S', 5.5000, 10, 'WARNING', 1),
('sim-rule-003', '模拟器-CO阈值告警', 'demo-sim-003', 'THRESHOLD', 'CO', 32.0000, 10, 'WARNING', 1);
```

- 阈值设在模拟器范围 70% 处，约 20-30% 数据点触发
- 告警冷却 5 分钟（`AlertSuppressRepository`），避免刷屏
- `duration_seconds=10` 缩短验证窗口（生产建议 60s）

### 验证

```bash
# 等约 20 秒后检查
docker exec igas-mysql mysql -uroot -p<password> \
  -e "SELECT device_uuid, severity, message, triggered_at FROM t_alert WHERE device_uuid LIKE 'demo-sim-%' ORDER BY triggered_at DESC"

# 预期输出 3+ 条告警记录
```

---

## 10. 常用运维命令

### 日志查看

```bash
# 所有容器
docker compose logs --tail 100

# 单个容器
docker logs igas-app --tail 50
docker logs igas-collector --tail 50
docker logs igas-simulator --tail 50

# 实时跟踪
docker logs -f igas-collector

# 按时间过滤
docker logs igas-collector --since 5m
```

### 数据库查询

```bash
# MySQL 直连
docker exec igas-mysql mysql -uroot -p<password> -e "<SQL>"

# 业务库
docker exec igas-mysql mysql -uroot -p<password> industrial_gas_alarm_corp -e "<SQL>"

# Collector 库
docker exec igas-mysql mysql -uroot -p<password> industrial_gas_alarm_collector -e "<SQL>"

# 常用查询
# 数据点数量
SELECT COUNT(*) FROM industrial_gas_alarm_collector.t_device_data_point WHERE device_uuid='demo-sim-001';

# 数据时间范围
SELECT MIN(recorded_at), MAX(recorded_at) FROM industrial_gas_alarm_collector.t_device_data_point;

# 告警数量
SELECT COUNT(*) FROM industrial_gas_alarm_corp.t_alert;

# 设备列表
SELECT device_uuid, name, gas_type FROM industrial_gas_alarm_corp.t_device;

# 告警规则
SELECT rule_uuid, name, gas_type, threshold, severity FROM industrial_gas_alarm_corp.t_alert_rule WHERE enabled=1;
```

### RabbitMQ 管理

```bash
# 队列状态
docker exec igas-rabbitmq rabbitmqctl list_queues name messages

# 交换机列表
docker exec igas-rabbitmq rabbitmqctl list_exchanges

# 管理界面（浏览器）
# http://139.224.190.132:15672 (guest/guest)
# 注意：端口未对外暴露，需 SSH 隧道或临时暴露
```

### 容器管理

```bash
# 重启单个服务
docker compose restart app
docker compose restart collector

# 重建并重启（代码有变更时）
docker compose up -d --build app
docker compose up -d --build nginx

# 全部重启
docker compose down && docker compose up -d

# 查看资源占用
docker stats --no-stream

# 进入容器
docker exec -it igas-app sh
docker exec -it igas-nginx sh
```

### 前端部署（非重建方式）

```bash
# 1. 本地构建
cd Code/frontend && npm run build

# 2. 打包上传
tar czf dist.tar.gz -C dist .
scp dist.tar.gz root@139.224.190.132:/tmp/

# 3. 复制到 nginx 容器
ssh root@139.224.190.132 "
  docker cp /tmp/dist.tar.gz igas-nginx:/tmp/
  docker exec igas-nginx tar xzf /tmp/dist.tar.gz -C /usr/share/nginx/html
  docker exec igas-nginx rm /tmp/dist.tar.gz
  rm /tmp/dist.tar.gz
"

# 注意：此方式不会删除旧 chunk 文件，长期累积需清理
# 正式发布建议：同步源码到服务器 → docker compose up -d --build nginx
```

### Nginx 配置热更新

```bash
scp nginx.conf root@139.224.190.132:/tmp/
ssh root@139.224.190.132 "
  docker cp /tmp/nginx.conf igas-nginx:/etc/nginx/conf.d/default.conf
  docker exec igas-nginx nginx -t           # 先测试配置
  docker exec igas-nginx nginx -s reload    # 热重载
"
```

---

## 11. 最终状态

### 服务状态（2026-06-04）

| 服务 | 端口 | 状态 | 备注 |
|------|------|------|------|
| MySQL 8.0 | 3306 | healthy | 数据持久化到 volume |
| Redis 7 | 6379 | healthy | 32MB maxmemory |
| RabbitMQ 3.13 | 5672 | healthy | 告警管道 + 设备事件 |
| App | 8080 | running | 主业务 + Dashboard API |
| Collector | 8081 | running | 数据采集 + 告警评估 |
| Simulator | 8082 | running | 3 设备 / 10s 间隔上报 |
| Nginx | 8088→80 | running | 静态文件 + API 代理 |

### 功能验证

| 功能 | URL | 状态 |
|------|-----|------|
| 官网首页 | http://139.224.190.132:8088/ | 正常 |
| 产品/新闻/方案 | /products, /news, /solutions | 正常 |
| 用户登录/注册 | /login, /register | 正常 |
| 管理后台 | /admin/* | 正常 |
| Dashboard 大屏 | /dashboard | 正常 |
| 历史数据 1h/6h/24h | /dashboard/history | 正常（已修复时区） |
| 历史告警 | /dashboard/history | 正常（约 5 分钟/条） |

### 已知遗留问题

1. **DeviceIngestionServiceImpl lock.unlock() bug**：`finally` 块中直接 `lock.unlock()` 未检查 `isHeldByCurrentThread()`，在锁过期时会抛 `IllegalMonitorStateException`。仅影响异常恢复场景，正常运行时锁不会过期（业务逻辑 < 1s，锁租约 3s）。
2. **前端部署缓存**：通过 `docker cp` 方式部署不会删除容器内旧 chunk（累积 ~400 文件），不影响运行但浪费少量空间。正式流程应 `docker compose up -d --build nginx`。
3. **WebSocket 报错**：Dashboard 页面试图连接 `ws://.../ws/dashboard`，后端无对应端点，Console 有红色错误但不影响功能。
