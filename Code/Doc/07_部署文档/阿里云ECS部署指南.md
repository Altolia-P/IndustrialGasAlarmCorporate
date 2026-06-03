# 阿里云 ECS 部署指南

> 适用环境：工业气体报警企业官网系统（Spring Boot + Vue3 + MySQL + Redis + Nginx）

## 一、ECS 购买配置

| 配置项 | 推荐选择 | 说明 |
|--------|---------|------|
| 场景 | 网站搭建 | — |
| 实例规格 | 4 vCPU / 8 GiB 共享标准型 | Java 吃内存，≤4G 不建议 |
| 计费方式 | 按量付费（测试）/ 包年包月（正式） | — |
| 地域 | 华东1（杭州）或离用户最近的可用区 | — |
| 镜像 | Ubuntu 22.04 LTS 64位 | 不要选 24.04 或 Minimal 版 |
| 预装应用 | 无（纯净系统） | 不选宝塔/WordPress/Docker/LNMP |
| 系统盘 | 40 GiB ESSD Entry | OS 占 ~10G，余量够用 |
| 公网 IP | 分配公网 IPv4 地址 | 必须分配 |
| 带宽 | 固定带宽 3 Mbps（或按流量 100Mbps） | 初期 3M 够用 |
| 登录凭证 | 密钥对 或 密码 | 密码需含数字+大小写+特殊字符 |
| 安全组 | 22 / 80 / 443 | 其他端口一概不开 |

## 二、SSH 登录

```bash
# 密码登录
ssh root@<公网IP>

# 密钥对登录
chmod 400 <密钥文件>.pem
ssh -i <密钥文件>.pem root@<公网IP>
```

阿里云控制台 Workbench 也可以直接登录（浏览器终端），但建议从本地终端 SSH。

## 三、初始化服务器环境

### 3.1 更新系统

```bash
apt update
```

### 3.2 安装 JDK 17

```bash
apt install openjdk-17-jdk -y
java -version
```

### 3.3 安装 MySQL 8.0

```bash
apt install mysql-server -y

# 安全初始化
mysql_secure_installation
```

`mysql_secure_installation` 交互问答：

| 提示 | 输入 |
|------|------|
| VALIDATE PASSWORD 组件 | `N` |
| 移除匿名用户 | `Y` |
| 禁止 root 远程登录 | `Y` |
| 删除 test 数据库 | `Y` |
| 重载权限表 | `Y` |

> Ubuntu 22.04 的 MySQL 默认使用 `auth_socket` 认证，root 用户通过系统 root 身份直接登录，无需密码。后续 `mysql -u root` 或 `sudo mysql` 即可进入。

### 3.4 创建数据库

```bash
mysql -u root
```

```sql
CREATE DATABASE industrial_gas_alarm_corp
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
EXIT;
```

### 3.5 安装 Redis

```bash
apt install redis-server -y
redis-cli ping      # 返回 PONG 表示正常
```

### 3.6 安装 Nginx

```bash
apt install nginx -y
nginx -v
```

## 四、部署项目

### 4.1 后端构建

```bash
# 本地项目根目录
cd Code/backend/IndustrialGasAlarmCorporate
mvn clean package -DskipTests
```

产出的 jar 位于 `app/target/app-0.0.1-SNAPSHOT.jar`。

### 4.2 前端构建

```bash
cd Code/frontend
npm install
npm run build
```

产物在 `dist/` 目录。

### 4.3 导入 SQL

将 `Code/backend/IndustrialGasAlarmCorporate/app/src/main/resources/Gas-ToB-SQL.sql` 上传到服务器并执行：

```bash
scp Gas-ToB-SQL.sql root@<公网IP>:/tmp/
ssh root@<公网IP>
mysql -u root industrial_gas_alarm_corp < /tmp/Gas-ToB-SQL.sql
```

### 4.4 上传 & 启动后端

```bash
# 上传 jar
scp app-0.0.1-SNAPSHOT.jar root@<公网IP>:/opt/industrial-gas/

# SSH 到服务器
ssh root@<公网IP>
```

**启动命令（使用 application-prod.yml 配置文件）：**

```bash
java -jar \
  -Dspring.profiles.active=prod \
  -DDB_PASSWORD=<数据库密码> \
  -DREDIS_PASSWORD=<Redis密码> \
  -DJWT_SECRET=<至少256位随机字符串> \
  -DCORS_ORIGINS=https://<你的域名> \
  -DDEEPSEEK_API_KEY=<DeepSeek密钥> \
  -DAPI_TOKEN_SHARED_KEY=<内部API共享密钥> \
  /opt/industrial-gas/app-0.0.1-SNAPSHOT.jar
```

> `application-prod.yml` 已内置生产环境配置（连接池调优、INFO 日志级别、CORS 限制等）。所有敏感值通过 `-D` 参数或环境变量注入，不写配置文件。

### 4.5 前端构建注意事项

构建前确认 `.env.production` 中的 `VITE_API_BASE_URL` 已设置为实际域名：

```env
VITE_API_BASE_URL=https://<你的域名>/api/v1
VITE_AI_CHAT_TIMEOUT=30000
```

```bash
# 本地构建
cd Code/frontend
npm install
npm run build
```

产物在 `dist/` 目录。

### 4.6 上传 & 配置前端

```bash
# 上传前端 dist
scp -r dist/ root@<公网IP>:/var/www/industrial-gas/

# SSH 到服务器配置 Nginx
ssh root@<公网IP>
```

创建 Nginx 配置 `/etc/nginx/sites-available/industrial-gas`：

```nginx
server {
    listen 80;
    server_name <域名或IP>;

    root /var/www/industrial-gas;
    index index.html;

    # Vue Router history 模式
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 反向代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 上传文件
    location /uploads/ {
        proxy_pass http://127.0.0.1:8080;
    }

    # WebSocket 代理（大屏实时推送）
    location /ws/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "Upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}

# HTTP 自动跳转 HTTPS（申请证书后启用）
server {
    listen 80;
    server_name <域名或IP>;
    return 301 https://$host$request_uri;
}
```

```bash
# 启用站点
ln -s /etc/nginx/sites-available/industrial-gas /etc/nginx/sites-enabled/
rm /etc/nginx/sites-enabled/default

# 测试配置
nginx -t

# 重载
systemctl reload nginx
```

## 五、配置 systemd 服务（后端自启动）

创建环境变量文件 `/opt/industrial-gas/env`：

```bash
cat > /opt/industrial-gas/env <<'EOF'
SPRING_PROFILES_ACTIVE=prod
DB_PASSWORD=<数据库密码>
REDIS_PASSWORD=<Redis密码>
JWT_SECRET=<JWT密钥>
CORS_ORIGINS=https://<你的域名>
DEEPSEEK_API_KEY=<DeepSeek密钥>
API_TOKEN_SHARED_KEY=<内部API共享密钥>
APP_LOG_LEVEL=INFO
EOF
chmod 600 /opt/industrial-gas/env
```

创建 systemd 服务文件 `/etc/systemd/system/industrial-gas.service`：

```ini
[Unit]
Description=Industrial Gas Alarm Corporate Backend
After=network.target mysql.service redis-server.service
Wants=mysql.service redis-server.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/industrial-gas
EnvironmentFile=/opt/industrial-gas/env
ExecStart=/usr/bin/java -jar /opt/industrial-gas/app-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

```bash
systemctl daemon-reload
systemctl enable industrial-gas
systemctl start industrial-gas
systemctl status industrial-gas
```

## 六、部署后验证

### 6.1 健康检查

```bash
# 无需认证，检查后端、数据库、Redis 状态
curl https://<域名>/api/v1/public/health
```

成功返回：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "status": "UP",
    "version": "1.6",
    "timestamp": "2026-06-03T10:30:00",
    "uptimeSeconds": 3600,
    "db": "UP",
    "redis": "UP"
  },
  "success": true
}
```

### 6.2 功能验证清单

| 检查项 | 验证方式 |
|--------|---------|
| 前端首页 | 浏览器访问 `https://<域名>` — 首页/产品/解决方案页正常 |
| 管理后台 | `https://<域名>/admin/login` — 登录成功并跳转 |
| 设备大屏 | 管理后台 → 设备监控 → WebSocket 实时数据刷新 |
| AI 客服 | 右下角客服按钮 → 发送消息 → 收到回复 |
| 留言/工单 | 前端留言 → 后台处理 → 流程闭环 |
| 文件下载 | 管理后台 → 下载管理 → 上传/下载正常 |
| 操作日志 | 管理后台 → 操作日志 → 审计记录完整 |

## 七、HTTPS 证书（Let's Encrypt 免费证书）

```bash
# 安装 certbot
apt install certbot python3-certbot-nginx -y

# 自动配置 Nginx + 申请证书
certbot --nginx -d <你的域名>

# 自动续期测试（certbot 默认已添加 systemd timer）
certbot renew --dry-run
```

证书有效期 90 天，certbot 定时器自动续期。

## 八、常用运维命令

```bash
# 查看所有服务状态
systemctl status mysql redis-server nginx industrial-gas

# 重启服务
systemctl restart nginx
systemctl restart industrial-gas

# 查看后端日志
journalctl -u industrial-gas -f

# 查看 Nginx 访问日志
tail -f /var/log/nginx/access.log
```

## 九、完整环境变量清单

所有敏感值通过环境变量注入，不写入配置文件或代码：

| 环境变量 | 用途 | 必须 | 示例值 |
|---------|------|------|--------|
| `DB_PASSWORD` | 数据库密码 | **是** | `MyStr0ng!DB` |
| `DB_URL` | 数据库连接地址 | 否 | `jdbc:mysql://localhost:3306/...` |
| `DB_USERNAME` | 数据库用户名 | 否 | `root` |
| `REDIS_PASSWORD` | Redis 密码 | **是** | `MyStr0ng!Redis` |
| `REDIS_HOST` | Redis 地址 | 否 | `localhost` |
| `JWT_SECRET` | JWT 签名密钥（≥256 位） | **是** | `openssl rand -base64 64` 生成 |
| `CORS_ORIGINS` | 允许的跨域域名 | **是** | `https://your-domain.com` |
| `DEEPSEEK_API_KEY` | AI 客服 API 密钥 | 否 | `sk-...` |
| `API_TOKEN_SHARED_KEY` | 内部 API 预共享密钥 | 否 | `openssl rand -hex 32` 生成 |
| `UPLOAD_PATH` | 文件上传目录 | 否 | `/data/uploads` |
| `SERVER_PORT` | 后端监听端口 | 否 | `8080` |
| `APP_LOG_LEVEL` | 应用日志级别 | 否 | `INFO` |

> 标记"必须"的变量缺少会导致功能异常或安全风险。

## 十、网络安全注意事项

- 安全组只开放 22（SSH）、80（HTTP）、443（HTTPS）
- **绝对不要**开放 8080、3306、6379 端口到公网
- 后端 Java 服务只监听 `127.0.0.1:8080`，通过 Nginx 反向代理暴露
- MySQL 和 Redis 只允许本地连接（bind 127.0.0.1）
- JWT 密钥和 API Token 使用 `openssl rand -base64 64` 生成
