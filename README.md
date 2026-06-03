# 工业气体报警企业官网系统

### 最后更新：2025-06-03

> 面向 ToB 的工业气体报警企业门户网站，支持产品展示、解决方案展示、客户留言收集、后台内容管理、设备监测平台、AI 智能客服。

---

## 环境要求

| 组件 | 最低版本 | 说明 |
|------|---------|------|
| **JDK** | 17+ | 推荐 Eclipse Temurin 17 LTS |
| **Maven** | 3.8+ | 多模块构建 |
| **MySQL** | 8.0+ | InnoDB、utf8mb4，2 个数据库 |
| **Redis** | 7.0+ | 缓存、防刷、JWT 黑名单 |
| **Nacos** | 2.3+ | 服务注册发现 + 配置中心 |
| **RabbitMQ** | 3.12+ | 微服务异步消息 |
| **Node.js** | 18+ | 前端构建 |
| **pnpm** | 8+ | 前端包管理 |

> RabbitMQ 非必须 — 如未安装，设备数据采集模块可暂时跳过。

---

## 快速启动

### 1. 克隆仓库

```bash
git clone <repo-url>
cd IndustrialGasAlarmCorporate
```

### 2. 启动基础设施

确保 **MySQL**、**Redis**、**Nacos**、**RabbitMQ** 已启动。

- Nacos 默认地址：`localhost:8848`，使用 standalone 模式即可：`startup.cmd -m standalone`
- RabbitMQ 默认地址：`localhost:5672`，管理后台 `localhost:15672`

### 3. 初始化数据库

按顺序执行 4 个 SQL 脚本：

```bash
# 主库（19 张业务表 + 演示数据）
mysql -u root -p < Code/backend/IndustrialGasAlarmCorporate/app/src/main/resources/Gas-ToB-SQL.sql

# 主库演示数据（产品、内容、分类、留言、管理员）
mysql -u root -p < Code/backend/IndustrialGasAlarmCorporate/app/src/main/resources/demo-data.sql

# 采集库（设备数据点表）
mysql -u root -p < Code/backend/IndustrialGasAlarmCorporate/device-collector/src/main/resources/Gas-ToB-SQL.sql

# 采集库演示数据（历史数据点）
mysql -u root -p < Code/backend/IndustrialGasAlarmCorporate/device-collector/src/main/resources/demo-data.sql
```

> SQL 脚本已包含 `DROP DATABASE IF EXISTS` → `CREATE DATABASE`，可重复执行。

### 4. 配置环境变量

以下变量**必须配置**，否则启动失败：

| 变量 | 说明 | 示例 |
|------|------|------|
| `JWT_SECRET` | JWT 签名密钥（至少 32 字符） | `your-256-bit-secret-key-here-change-me` |
| `DB_PASSWORD` | MySQL 密码 | `root` |
| `REDIS_PASSWORD` | Redis 密码（无密码则留空） | 空或密码 |

以下变量为可选项（不影响核心功能启动）：

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `DEEPSEEK_API_KEY` | DeepSeek API Key（AI 客服） | 空（不启用 AI） |
| `RABBITMQ_HOST` | RabbitMQ 地址 | `localhost` |
| `RABBITMQ_PORT` | RabbitMQ 端口 | `5672` |
| `RABBITMQ_USERNAME` | RabbitMQ 用户名 | `guest` |
| `RABBITMQ_PASSWORD` | RabbitMQ 密码 | `guest` |
| `CORS_ORIGINS` | 允许的跨域来源 | `*` |

Windows PowerShell 示例：
```powershell
$env:JWT_SECRET = "your-256-bit-secret-key-here-change-me"
$env:DB_PASSWORD = "root"
$env:REDIS_PASSWORD = ""
```

### 5. 启动后端服务

在 `Code/backend/IndustrialGasAlarmCorporate/` 目录下，依次启动 3 个服务：

```bash
# 编译全部模块
mvnw.cmd clean install -DskipTests

# 终端 1 — 主应用（端口 8080）
mvnw.cmd -pl app spring-boot:run

# 终端 2 — 设备采集（端口 8081）  
mvnw.cmd -pl device-collector spring-boot:run

# 终端 3 — 设备模拟器（端口 8082，可选）
mvnw.cmd -pl device-simulator spring-boot:run
```

> 如未安装 RabbitMQ，仅启动 app 即可。device-collector 和 device-simulator 会因 RabbitMQ 连接失败而报错，不影响主应用。

### 6. 启动前端

```bash
cd Code/frontend

pnpm install
pnpm dev
```

前端开发服务器默认 `http://localhost:5173`，自动代理 API 请求到后端 8080。

---

## 登录账号（演示数据）

| 角色 | 用户名 | 密码 | 权限 |
|------|--------|------|------|
| 超级管理员 | `admin` | `123456` | 全部管理功能 |
| 内容编辑 | `editor` | `123456` | 产品/内容管理 |
| 客服人员 | `staff` | `123456` | 留言处理 |

后台登录地址：`http://localhost:5173/admin/login`

---

## 技术栈

| 类别 | 技术选型 |
|------|----------|
| **后端框架** | Spring Boot 3.5.14 + Java 17 |
| **持久化** | MyBatis-Plus 3.5.5 + MySQL 8.0 |
| **缓存** | Redis（Lettuce 连接池） |
| **安全** | Spring Security + BCrypt + JJWT 0.12.5 |
| **服务治理** | Nacos（注册发现 + 配置中心） |
| **RPC** | OpenFeign + Spring Cloud LoadBalancer |
| **消息队列** | RabbitMQ（Spring AMQP） |
| **实时通信** | WebSocket（大屏数据推送） |
| **AI** | LangChain4j + DeepSeek API |
| **前端** | Vue 3 + Vite + TypeScript + Pinia + Vue Router |
| **构建** | Maven（后端）/ pnpm（前端） |

---

## 架构

### 后端架构（DDD 四层 + Assembler 隔离）

```
Controller → Service 接口 → Assembler + 聚合根 + Repository 接口
                ↑ 依赖接口        ↑ 依赖倒置
              ServiceImpl       RepositoryImpl → Mapper → PO
```

| 层 | 包路径 | 职责 |
|----|--------|------|
| **Interface** | `interfaces/` | 路由分发、参数校验、返回 `Result<T>` |
| **Application** | `application/` | 业务编排、DTO↔Domain 转换、事务管理 |
| **Assembler** | `assembler/` | 纯 DTO↔Domain↔VO 映射，无业务逻辑 |
| **Domain** | `domain/` | 聚合根、值对象、Repository 接口，零框架注解 |
| **Infrastructure** | `infrastructure/` | Repository 实现、PO/Mapper、Redis、Security |

### 微服务模块

| 模块 | 端口 | 说明 |
|------|------|------|
| `app` | 8080 | 主应用：官网、后台管理、AI 客服、告警引擎 |
| `device-collector` | 8081 | 设备数据采集：接收上报、持久化到 collector 库 |
| `device-simulator` | 8082 | 设备模拟器：按 YAML 配置生成模拟数据点 |

---

## API 规范

- 前缀：`/api/v1/public/**`（免认证）、`/api/v1/admin/**`（JWT 认证）
- 统一响应：`Result<T>` `{code: 0, message: "success", data: T, success: boolean}`
- 分页：前端传 `page=1`，后端转 `page-1` 传给 MyBatis-Plus
- 时间格式：`YYYY-MM-DD HH:mm:ss`

---

## 项目结构

```
IndustrialGasAlarmCorporate/
├── Code/
│   ├── backend/IndustrialGasAlarmCorporate/     # Maven 多模块后端
│   │   ├── app/                                  # 主应用模块
│   │   │   └── src/main/
│   │   │       ├── java/com/niit/industrialgasalarmcorporate/
│   │   │       │   ├── common/                   # Result、异常、JWT、枚举
│   │   │       │   ├── domain/                   # 聚合根、值对象、Repository 接口
│   │   │       │   ├── application/              # Service 接口与实现
│   │   │       │   ├── assembler/                # DTO↔Domain↔VO 映射
│   │   │       │   ├── interfaces/               # Controller 层
│   │   │       │   └── infrastructure/           # PO、Mapper、Redis、Security
│   │   │       └── resources/
│   │   │           ├── Gas-ToB-SQL.sql           # 建库 + 建表 DDL
│   │   │           └── demo-data.sql             # 演示数据
│   │   ├── device-collector/                     # 设备采集微服务
│   │   │   └── src/main/resources/
│   │   │       ├── Gas-ToB-SQL.sql               # collector 库 DDL
│   │   │       └── demo-data.sql                 # 设备数据点演示数据
│   │   ├── device-simulator/                     # 设备模拟器
│   │   └── pom.xml                               # 父 POM
│   └── frontend/                                 # Vue 3 前端
│       ├── src/
│       │   ├── api/                              # Axios 请求封装
│       │   ├── components/                       # 公共组件
│       │   ├── composables/                      # 可复用逻辑
│       │   ├── router/                           # 路由配置
│       │   ├── stores/                           # Pinia 状态
│       │   ├── types/                            # TypeScript 类型
│       │   ├── utils/                            # 工具函数
│       │   └── views/                            # 页面组件
│       └── package.json
├── .claude/                                      # Claude Code 配置
├── CLAUDE.md                                     # 项目指令（AI 编码规范）
└── README.md                                     # 本文件
```

---

## 关键约束

- **Domain 层**：零框架注解（无 Spring、Lombok、Jackson）
- **Assembler 层**：DTO↔Domain↔VO 转换必须走 Assembler，禁止 `BeanUtils.copyProperties`
- **Repository**：单对象查询返回 `Optional<T>`，不返回 null
- **Controller**：仅做参数校验 + Service 调用 + 返回 `Result<T>`，无业务逻辑
- **构造器注入**：使用 `@RequiredArgsConstructor`，不用 `@Autowired` 字段注入
- **PO**：仅用 `@Getter` + `@Setter`（不用 `@Data`，避免 toString 循环）
- **图片上传**：仅允许 `jpg/png/webp`，单文件 ≤ 5MB
- **全表 UUID v4 主键**：`CHAR(36)`，应用层生成，无自增

---

## 文档

设计文档位于 `Code/Doc/` 目录（不在 Git 仓库中，需从内部知识库获取）：

- `03_需求分析/` — PRD 需求文档
- `04_系统设计/1.架构设计文档/` — 架构设计
- `04_系统设计/2.类图设计文档/` — 类图设计
- `04_系统设计/3.数据库设计文档/` — 数据库设计
- `04_系统设计/4.接口设计文档/` — API 接口设计
