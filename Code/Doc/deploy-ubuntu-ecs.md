# 阿里云 ECS (Ubuntu) 部署指南

## 部署架构

```
Nginx (80/443)
├── /api/*        → localhost:8080   (app 微服务)
├── /ws/*         → localhost:8080   (WebSocket)
├── /uploads/*    → localhost:8080   (上传文件)
└── /*            → /var/www/html    (前端静态文件)

App (8080)        → MySQL: industrial_gas_alarm_corp     + Redis(db 0) + RabbitMQ
Collector (8081)  → MySQL: industrial_gas_alarm_collector + Redis(db 1) + RabbitMQ
Simulator (8082)  → HTTP POST → Collector (8081)
```

---

## 前置条件

- 阿里云 ECS 实例（Ubuntu 22.04/24.04）
- 安全组已开放端口：`22`（SSH）、`80`（HTTP）、`443`（HTTPS，可选）
- 本项目代码已推送到 Gitee/GitHub（即使是私有仓库，服务器也能通过 Personal Access Token 拉取，**不必公开**）

---

## 一、代码传输

### 方式 A：服务器上 git clone（推荐，方便后续 `git pull` 更新）

```bash
apt update && apt install -y git

# Gitee（国内高速）
git clone https://gitee.com/yue-a/industrial-gas-alarm-corporate.git /opt/industrial-gas-alarm

# GitHub（国外稳定）
git clone https://github.com/Altolia-P/IndustrialGasAlarmCorporate.git /opt/industrial-gas-alarm
```

如果仓库是私有的，使用 Personal Access Token 替代密码：

```bash
# Gitee 私有仓库
git clone https://你的Gitee用户名:AccessToken@gitee.com/yue-a/industrial-gas-alarm-corporate.git /opt/industrial-gas-alarm

# GitHub 私有仓库
git clone https://你的GitHub用户名:ghp_xxxxxxxx@github.com/Altolia-P/IndustrialGasAlarmCorporate.git /opt/industrial-gas-alarm
```

> **获取 Access Token**：Gitee → 设置 → 私人令牌；GitHub → Settings → Developer settings → Personal access tokens → Tokens(classic)，勾选 `repo` 权限。

### 方式 B：本地 scp 上传

```powershell
# Windows 本地执行
scp -r "D:\BaiduSyncdisk\Project(IndustrialGasAlarmCorporate)\Code" root@<ECS公网IP>:/opt/industrial-gas-alarm/
```

### 方式 C：本地打包 + 上传

```bash
# 本地 Git Bash 打包（排除 node_modules / target / .git）
cd /d/BaiduSyncdisk/Project\(IndustrialGasAlarmCorporate\)/Code
tar -czf /tmp/project.tar.gz \
  --exclude='frontend/node_modules' \
  --exclude='frontend/dist' \
  --exclude='backend/**/target' \
  --exclude='.git' \
  .
scp /tmp/project.tar.gz root@<ECS_IP>:/opt/
# 服务器上解压
ssh root@<ECS_IP> "mkdir -p /opt/industrial-gas-alarm && tar -xzf /opt/project.tar.gz -C /opt/industrial-gas-alarm"
```

---

## 二、环境安装

### 2.1 一键安装所有依赖

```bash
apt update && apt install -y \
  openjdk-17-jdk \
  maven \
  mysql-server \
  redis-server \
  rabbitmq-server \
  nginx \
  curl

# 验证
java --version          # 17.x
mvn --version           # 3.x
mysql --version         # 8.0.x
redis-cli ping          # PONG
systemctl status rabbitmq-server
nginx -v                # 1.x
```

### 2.2 启动基础服务

```bash
systemctl enable mysql redis-server rabbitmq-server nginx
systemctl start mysql redis-server rabbitmq-server nginx
```

---

## 三、数据库初始化

### 3.1 创建库和用户

```bash
mysql -u root <<'SQL'
CREATE DATABASE IF NOT EXISTS industrial_gas_alarm_corp
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS industrial_gas_alarm_collector
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'gas_admin'@'localhost' IDENTIFIED BY '<替换为你的密码>';
GRANT ALL PRIVILEGES ON industrial_gas_alarm_corp.* TO 'gas_admin'@'localhost';
GRANT ALL PRIVILEGES ON industrial_gas_alarm_collector.* TO 'gas_admin'@'localhost';
FLUSH PRIVILEGES;
SQL
```

### 3.2 导入表结构

```bash
mysql -u root industrial_gas_alarm_corp \
  < /opt/industrial-gas-alarm/Code/backend/IndustrialGasAlarmCorporate/app/src/main/resources/Gas-ToB-SQL.sql

mysql -u root industrial_gas_alarm_collector \
  < /opt/industrial-gas-alarm/Code/backend/IndustrialGasAlarmCorporate/device-collector/src/main/resources/Gas-ToB-SQL.sql
```

---

## 四、环境变量配置

```bash
cat > /opt/industrial-gas-alarm/env.sh <<'EOF'
#!/bin/bash

# ==== 数据库 ====
export DB_URL="jdbc:mysql://localhost:3306/industrial_gas_alarm_corp?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai"
export DB_USERNAME="gas_admin"
export DB_PASSWORD="<替换为你的数据库密码>"

# ==== Redis ====
export REDIS_HOST="localhost"
export REDIS_PORT="6379"
export REDIS_PASSWORD=""

# ==== RabbitMQ ====
export RABBITMQ_HOST="localhost"
export RABBITMQ_PORT="5672"
export RABBITMQ_USERNAME="guest"
export RABBITMQ_PASSWORD="guest"

# ==== JWT（用 openssl rand -base64 64 生成）====
export JWT_SECRET="<替换为随机生成的密钥>"

# ==== API Token（服务间通信，用 openssl rand -hex 32 生成）====
export API_TOKEN_SHARED_KEY="<替换为随机生成的密钥>"

# ==== CORS（替换为实际域名或 ECS 公网 IP）====
export CORS_ORIGINS="http://<ECS公网IP>,https://<你的域名>"

# ==== 文件上传路径 ====
export UPLOAD_PATH="/data/industrial-gas-alarm/uploads"

# ==== DeepSeek AI（可选）====
export DEEPSEEK_API_KEY=""
EOF

chmod 600 /opt/industrial-gas-alarm/env.sh
mkdir -p /data/industrial-gas-alarm/uploads
```

---

## 五、构建

```bash
cd /opt/industrial-gas-alarm/Code/backend/IndustrialGasAlarmCorporate

source /opt/industrial-gas-alarm/env.sh
export SPRING_PROFILES_ACTIVE=prod

mvn clean package -DskipTests
```

构建完成后，JAR 位置：

| 模块 | JAR 路径 |
|------|---------|
| app | `app/target/app-0.0.1-SNAPSHOT.jar` |
| device-collector | `device-collector/target/device-collector-0.0.1-SNAPSHOT.jar` |
| device-simulator | `device-simulator/target/device-simulator-0.0.1-SNAPSHOT.jar` |

---

## 六、systemd 服务（后台运行 + 开机自启）

### 6.1 app（端口 8080）

```bash
cat > /etc/systemd/system/industrial-gas-app.service <<'EOF'
[Unit]
Description=Industrial Gas Alarm App (port 8080)
After=mysql.service redis-server.service rabbitmq-server.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/industrial-gas-alarm
EnvironmentFile=/opt/industrial-gas-alarm/env.sh
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DB_URL=jdbc:mysql://localhost:3306/industrial_gas_alarm_corp?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai"
ExecStart=/usr/bin/java -jar /opt/industrial-gas-alarm/Code/backend/IndustrialGasAlarmCorporate/app/target/app-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF
```

### 6.2 collector（端口 8081）

```bash
cat > /etc/systemd/system/industrial-gas-collector.service <<'EOF'
[Unit]
Description=Industrial Gas Alarm Collector (port 8081)
After=mysql.service redis-server.service rabbitmq-server.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/industrial-gas-alarm
EnvironmentFile=/opt/industrial-gas-alarm/env.sh
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DB_URL=jdbc:mysql://localhost:3306/industrial_gas_alarm_collector?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai"
Environment="COLLECTOR_PORT=8081"
ExecStart=/usr/bin/java -jar /opt/industrial-gas-alarm/Code/backend/IndustrialGasAlarmCorporate/device-collector/target/device-collector-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF
```

### 6.3 simulator（端口 8082）

```bash
cat > /etc/systemd/system/industrial-gas-simulator.service <<'EOF'
[Unit]
Description=Industrial Gas Alarm Simulator (port 8082)
After=industrial-gas-collector.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/industrial-gas-alarm
ExecStart=/usr/bin/java -jar /opt/industrial-gas-alarm/Code/backend/IndustrialGasAlarmCorporate/device-simulator/target/device-simulator-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF
```

### 6.4 启动

```bash
systemctl daemon-reload
systemctl enable industrial-gas-app industrial-gas-collector industrial-gas-simulator
systemctl start industrial-gas-app industrial-gas-collector industrial-gas-simulator

# 检查状态
systemctl status industrial-gas-app industrial-gas-collector

# 查看实时日志
journalctl -u industrial-gas-app -f
```

---

## 七、前端构建 + Nginx

### 7.1 安装 Node.js + 构建

```bash
curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
apt install -y nodejs

cd /opt/industrial-gas-alarm/Code/frontend
npm install
npm run build
```

### 7.2 部署静态文件 + 配置 Nginx

```bash
cp -r dist/* /var/www/html/
```

```bash
cat > /etc/nginx/sites-available/industrial-gas <<'NGINX'
server {
    listen 80;
    server_name _;                     # 有域名则替换为域名

    root /var/www/html;
    index index.html;

    # API 反向代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # WebSocket
    location /ws/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # 上传文件
    location /uploads/ {
        proxy_pass http://127.0.0.1:8080;
    }

    # Vue Router history 模式
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 静态资源长期缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf)$ {
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
NGINX

ln -sf /etc/nginx/sites-available/industrial-gas /etc/nginx/sites-enabled/
rm -f /etc/nginx/sites-enabled/default
nginx -t && systemctl reload nginx
```

---

## 八、验证

```bash
# 后端直连
curl http://localhost:8080/api/v1/public/products

# 通过 Nginx
curl http://localhost/api/v1/public/products

# 浏览器访问
# http://<ECS公网IP>
```

---

## 九、日常更新

```bash
cd /opt/industrial-gas-alarm
git pull

# 后端
cd Code/backend/IndustrialGasAlarmCorporate
source /opt/industrial-gas-alarm/env.sh
mvn clean package -DskipTests
systemctl restart industrial-gas-app industrial-gas-collector industrial-gas-simulator

# 前端
cd /opt/industrial-gas-alarm/Code/frontend
npm run build
cp -r dist/* /var/www/html/
```

---

## 十、常用运维命令

```bash
# 查看服务状态
systemctl status industrial-gas-app
systemctl status industrial-gas-collector

# 查看日志
journalctl -u industrial-gas-app -n 100 -f     # 实时跟踪
journalctl -u industrial-gas-app --since today  # 今日日志

# 重启服务
systemctl restart industrial-gas-app

# 停止服务
systemctl stop industrial-gas-app

# 检查端口监听
ss -tlnp | grep -E '808[0-2]|80'
```

---

## 附录：关于私有仓库

本项目的 Gitee/GitHub 仓库当前为**私有**，两种选择：

### 选项一：保持私有，服务器用 Access Token 克隆

无需公开仓库，在服务器上通过 Personal Access Token 认证即可拉取代码（见第一章"方式 A"），后续 `git pull` 也没问题。

### 选项二：改为公开仓库

代码本身不含敏感信息（`application-local.yml`、`.env.production` 等文件已通过 `.gitignore` 排除，API Key 和数据库密码均不对外暴露），改为公开无安全风险。

**GitHub**：仓库 → Settings → General → Danger Zone → Change repository visibility → Make public

**Gitee**：仓库 → 管理 → 基本信息 → 是否开源 → 公开（需先完成实名认证）
