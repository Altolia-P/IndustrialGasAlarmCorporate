# IntelliJ IDEA Database 面板连接 MySQL 修复方案

## 问题根因

IntelliJ IDEA 的 **Database** 工具窗口尝试从 JetBrains CDN 自动下载 MySQL JDBC 驱动：

```
https://download.jetbrains.com/idea/jdbc-drivers/MySQL/9.5/LICENSE.txt → 404
```

JetBrains 服务器上缺少 MySQL 9.5 版本的驱动文件，导致 IDE 内置的数据库浏览器无法获取驱动。

> ⚠️ **这与你的 Maven 项目无关**。`pom.xml` 中声明的 `mysql-connector-j` 会从 Maven Central 正常下载，应用运行时不受影响。

---

## 解决方案：手动指定本地 JDBC 驱动

> ✅ **Maven 本地仓库已有** 4 个版本的驱动 JAR。
>
> ⚠️ **9.5.0 导致 "没有此对象的驱动程序类: @localhost" 错误** →
> **改用 8.0.33**（MySQL 8.0 官方推荐版本，IntelliJ 兼容性最好）。

### 步骤 1：清理旧的驱动配置 + 切换为 8.0.33

1. 在 Database 面板中，**删除**当前已创建的 MySQL 数据源（右键 → Remove）
2. 进入 **Drivers** 标签页，选中 MySQL
3. 在 Driver Files 中，**移除** 9.5.0 的 JAR（选中 → 点 `-`）
4. 点 **+** → **Custom JARs...** → 选择：

```
C:\Users\Altolia\.m2\repository\com\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar
```

5. 确认 **Class** 下拉框显示：`com.mysql.cj.jdbc.Driver`
6. 点击 **Apply**

---

### 步骤 2：在 IntelliJ IDEA 中配置驱动

1. 打开 IntelliJ IDEA
2. 点击右侧边栏的 **Database** 标签（或通过菜单 `View → Tool Windows → Database`）
3. 点击 **+** → **Data Source** → **MySQL**

   此时会弹出配置窗口，如果自动下载失败，会看到红色的错误提示。

4. 在 **Data Sources and Drivers** 窗口中，切换到 **Drivers** 标签页
5. 选中 **MySQL** 驱动
6. 在右侧面板中：
   - 找到 **Driver Files** 区域
   - 点击 **+** 按钮 → 选择 **Custom JARs...**
   - 浏览并选择你在步骤 1 中获取的 `mysql-connector-j-*.jar` 文件
7. 确保 **Class** 下拉框显示：`com.mysql.cj.jdbc.Driver`
8. 点击 **Apply**，然后切换到 **Data Sources** 标签页

---

### 步骤 3：配置数据库连接

回到数据源配置页面：

| 字段 | 值 |
|------|-----|
| **Host** | `localhost` |
| **Port** | `3306` |
| **User** | `root` |
| **Password** | 你的 MySQL root 密码 |
| **Database** | `industrial_gas_alarm_corp` |

> ⚠️ 注意：数据库 `industrial_gas_alarm_corp` 可能还未创建。如果测试连接时提示数据库不存在，请先用 MySQL 客户端执行建库 SQL，或先在 Database 面板中不指定数据库名，连接成功后再创建。

6. 点击左下角 **Test Connection** 测试连接
7. 如果提示 **"Download missing driver files"**，选择 **"Use existing driver"** 或取消，因为你已经手动指定了驱动
8. 测试成功后，点击 **OK** 保存

---

### 步骤 4：初始化数据库（如尚未执行 DDL）

如果数据库还未创建，需要执行 SQL 建库建表脚本。你的 DDL 文件位于：

```
D:\BaiduSyncdisk\Project(IndustrialGasAlarmCorporate)\Code\Doc\04_系统设计\3.数据库设计\Gas-ToB-SQL.sql
```

连接成功后，可以在 IntelliJ 的 Database 面板中：
1. 右键数据库连接 → **New** → **Query Console**
2. 将 `Gas-ToB-SQL.sql` 的内容粘贴进去
3. 执行（`Ctrl + Enter` 或点击执行按钮）

---

## 快速诊断清单

如果测试连接仍然失败，按以下顺序排查：

| 现象 | 可能原因 | 解决方案 |
|------|---------|---------|
| `Communications link failure` | MySQL 服务未启动 | 启动 MySQL 服务（`services.msc` 中找到 MySQL80 启动） |
| `Access denied for user 'root'@'localhost'` | 密码错误 | 检查 `application.yml` 中 `${DB_PASSWORD}` 的实际值 |
| `Unknown database 'industrial_gas_alarm_corp'` | 数据库不存在 | 先执行 `CREATE DATABASE` 语句 |
| `The driver has not been found` | 驱动 JAR 路径不对 | 重新选择正确的 JAR 文件 |
| `SSL connection error` | MySQL 要求 SSL | 在 URL 后加 `?useSSL=false&allowPublicKeyRetrieval=true` |

---

## 补充：项目 application.yml 中的数据库密码配置

当前 [`application.yml:11`](../src/main/resources/application.yml:11) 配置为：

```yaml
password: ${DB_PASSWORD:}
```

默认值为空字符串。启动应用前，需要设置环境变量 `DB_PASSWORD`，或在 IntelliJ Run Configuration 中配置：

1. Run → Edit Configurations
2. 找到 Spring Boot 运行配置
3. Environment variables 中添加：`DB_PASSWORD=你的MySQL密码`
