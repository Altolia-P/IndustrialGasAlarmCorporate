# 阿里云 ECS Docker 部署记录

> **部署日期**: 2026-06-03 ~ 2026-06-04
> **服务器**: 阿里云 ECS (Ubuntu 22.04, 2 vCPU, 2GB RAM)
> **IP**: 139.224.190.132
> **访问地址**: http://139.224.190.132:8088

---

## 服务架构

```
docker compose (7 容器)
├── mysql       (8.0)      — 数据库
├── redis       (7-alpine) — 缓存 + 分布式锁
├── rabbitmq    (3.13)     — 消息队列
├── app         (8080)     — Spring Boot 主应用
├── collector   (8081)     — 数据采集服务
├── simulator   (8082)     — 设备模拟器 (已停用以省内存)
└── nginx       (80→8088)  — 前端 + 反向代理
```

端口 8088 对外（避免 ICP 备案端口冲突），nginx 内部代理到 app:8080。

---

## SSH 连接方式

**方式一：密钥登录（推荐）**

本机 ED25519 公钥已添加至服务器：
```
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAII6TQnBzvcKq218f7f8dN6Dbzwk3P/CdDQNzS0E4j/Ou altolia@DESKTOP-34QCKUR
```

连接命令：
```bash
ssh root@139.224.190.132
```

如果本机密钥丢失，重新生成后通过**阿里云 ECS 控制台 → 远程连接 (VNC)** 添加新公钥：
```bash
mkdir -p ~/.ssh && echo '新公钥内容' >> ~/.ssh/authorized_keys
```

**方式二：密码登录**
```bash
ssh root@139.224.190.132  # 密码: yueyang20041202
```

---

## 服务器路径

```
/opt/igas/Code/
├── docker-compose.yml
├── .env                          # 环境变量（DB密码、JWT密钥等）
├── docker/
│   ├── mysql/init.sql            # 建库建表 + 默认管理员
│   └── config/
│       ├── app/app-docker.yml    # app 外部配置（禁用 Nacos）
│       └── collector/app-docker.yml
├── backend/
│   └── IndustrialGasAlarmCorporate/
│       ├── app/Dockerfile
│       ├── app/target/*.jar
│       └── device-collector/
└── frontend/
    ├── nginx.conf
    ├── Dockerfile
    └── dist/                     # 预构建的前端静态文件
```

---

## 常用运维命令

```bash
# 查看所有容器状态
cd /opt/igas/Code && docker compose ps

# 查看内存
free -h

# 查看日志
docker compose logs --tail 50 app
docker compose logs --tail 50 collector

# 重启某个服务
docker compose restart app

# 全部重启
docker compose down && docker compose up -d

# 本地构建前端后上传
cd Code/frontend && npm run build
scp -r dist root@139.224.190.132:/opt/igas/Code/frontend/
ssh root@139.224.190.132 "cd /opt/igas/Code && docker compose up -d --force-recreate nginx"
```

---

## JVM 内存配置（针对 2GB ECS 优化）

| 容器 | Xmx | Xms |
|------|-----|-----|
| app | 256m | 128m |
| collector | 192m | 96m |
| simulator | — (已停用) | — |

simulator 按需启动：`docker compose up -d simulator`

---

## 关键修复记录

### 1. Nginx Docker DNS 解析
- **问题**: nginx 启动时报 `host not found in upstream "app"`
- **原因**: nginx 在启动时解析 upstream，但 app 容器尚未就绪
- **修复**: nginx.conf 添加 `resolver 127.0.0.11`（Docker 内部 DNS），使用变量延迟解析

### 2. Spring Boot application-prod.yml 配置错误
- **问题**: `InvalidConfigDataPropertyException: Property 'spring.profiles.active' is invalid in a profile specific resource`
- **原因**: Spring Boot 3.x 不允许在 `application-prod.yml`（profile 特定文件）内设置 `spring.profiles.active`
- **修复**: 删除 `application-prod.yml` 中的 `spring.profiles.active: prod` 行

### 3. 内存不足导致服务崩溃
- **问题**: 2GB ECS 运行 7 个容器，Java 堆 ~900MB，系统 OOM
- **修复**: 降低 JVM 堆（app 512→256m, collector 384→192m），停用 simulator

---

## 默认管理员账号

- 用户名: `admin`
- 密码: `admin123`
- 登录路径: `/api/v1/admin/login`

---

## 数据库

- 数据库 1: `industrial_gas_alarm_corp` (主业务)
- 数据库 2: `industrial_gas_alarm_collector` (采集)
- 连接: `docker exec -it igas-mysql mysql -uroot -pyueyang20041202`

---

## Alibaba Cloud ECS 控制台操作备忘

| 操作 | 路径 |
|------|------|
| 强制重启 | 实例列表 → 选中实例 → 重启 → 强制重启 |
| 远程连接 (VNC) | 实例列表 → 远程连接 → 通过 VNC 登录 |
| 安全组（防火墙） | 实例详情 → 安全组 → 配置规则 |

当前开放端口：22 (SSH), 3306 (MySQL), 8080, 8081, 8082, 8088 (Web)

---

## 安全提示

- `.env` 文件和 `init.sql` 包含敏感信息，不要提交到公开仓库
- 已在 `.gitignore` 中排除
