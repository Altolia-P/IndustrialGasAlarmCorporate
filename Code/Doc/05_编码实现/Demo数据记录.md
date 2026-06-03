# Demo 数据记录

> 最后更新：2026-05-28

## 1. 用户账号

| 账号 | 密码 | 角色 | 手机 | 关联员工 |
|------|------|------|------|----------|
| `admin` | `123456` | ADMIN | 13800000001 | — |
| `yueyang` | `123456` | STAFF | 13540846437 | 岳阳 |
| `zhangjianguo` | `123456` | STAFF | 13810001111 | 张建国 |
| `liminghua` | `123456` | STAFF | 13810002222 | 李明华 |
| `wanglei` | `123456` | STAFF | 13810003333 | 王磊 |
| `zhaoli` | `123456` | STAFF | 13810004444 | 赵丽 |
| `chenqiang` | `123456` | STAFF | 13810005555 | 陈强 |
| `zhanggong` | `123456` | STAFF | 13800000002 | 张工 |
| `staff` | `123456` | STAFF | 13800000002 | —（旧数据） |
| `demo` | `123456` | USER | 13800001111 | — |
| `zhangsan` | `123456` | USER | 13900002222 | — |
| `lisi` | `123456` | USER | 13700003333 | — |
| `user` | `123456` | USER | 13900000001 | — |

## 2. 员工档案

| UUID | 姓名 | 手机 | 邮箱 | 岗位 | 状态 |
|------|------|------|------|------|------|
| `9558de24-afb1-4ad5-8e56-d069b37e91e7` | 岳阳 | 13540846437 | 123445563@qq.com | 技术支持 | 待命 |
| `demo-stf-001` | 张建国 | 13810001111 | zhangjg@intersense.com | 现场技术 | 工作中 |
| `demo-stf-002` | 李明华 | 13810002222 | limh@intersense.com | 客服 | 待命 |
| `demo-stf-003` | 王磊 | 13810003333 | wanglei@intersense.com | 技术支持 | 待命 |
| `demo-stf-004` | 赵丽 | 13810004444 | zhaoli@intersense.com | 售后服务 | 待命 |
| `demo-stf-005` | 陈强 | 13810005555 | chenqiang@intersense.com | 现场技术 | 出差 |
| `d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a` | 张工 | 13800000002 | zhang@example.com | 技术支持 | 待命 |

### 岗位枚举

| 枚举值 | 含义 |
|--------|------|
| `FIELD_TECH` | 现场技术 |
| `TECH_SUPPORT` | 技术支持 |
| `CUSTOMER_SERVICE` | 客服 |
| `AFTER_SALES` | 售后服务 |

### 状态枚举

| 枚举值 | 含义 |
|--------|------|
| `STANDBY` | 待命 |
| `WORKING` | 工作中 |
| `BUSINESS_TRIP` | 出差 |

## 3. 产品分类

| UUID | 名称 | 类型 |
|------|------|------|
| `demo-cat-001` | 固定式气体检测仪 | PRODUCT_CATEGORY |
| `demo-cat-002` | 便携式气体检测仪 | PRODUCT_CATEGORY |
| `demo-cat-003` | 气体报警控制器 | PRODUCT_CATEGORY |
| `demo-cat-004` | 气体传感器模块 | PRODUCT_CATEGORY |
| `demo-cat-005` | 配件与耗材 | PRODUCT_CATEGORY |

## 4. 内容分类

| UUID | 名称 | 类型 |
|------|------|------|
| `demo-cat-101` | 石油化工安全 | CONTENT_CATEGORY |
| `demo-cat-102` | 冶金钢铁安全 | CONTENT_CATEGORY |
| `demo-cat-103` | 能源电力安全 | CONTENT_CATEGORY |
| `demo-cat-104` | 市政燃气安全 | CONTENT_CATEGORY |
| `demo-cat-201` | 公司动态 | CONTENT_CATEGORY |
| `demo-cat-202` | 行业资讯 | CONTENT_CATEGORY |
| `demo-cat-203` | 技术前沿 | CONTENT_CATEGORY |

## 5. 产品列表

| UUID | 名称 | 分类 | 状态 |
|------|------|------|------|
| `demo-prd-001` | SenseGuard F-800 固定式可燃气体检测仪 | 固定式气体检测仪 | 已上架 |
| `demo-prd-002` | SenseGuard F-600 固定式有毒气体检测仪 | 固定式气体检测仪 | 已上架 |
| `demo-prd-003` | SenseGuard P-200 便携式多气体检测仪 | 便携式气体检测仪 | 已上架 |
| `demo-prd-004` | SenseGuard P-100 便携式单气体检测仪 | 便携式气体检测仪 | 已上架 |
| `demo-prd-005` | SenseAlarm C-5000 气体报警控制器 | 气体报警控制器 | 已上架 |
| `demo-prd-006` | SenseAlarm C-3000 区域报警控制器 | 气体报警控制器 | 已上架 |
| `demo-prd-007` | SenseSensor MQ-4 甲烷传感器模块 | 气体传感器模块 | 已上架 |
| `demo-prd-008` | SenseSensor CO-200 一氧化碳传感器模块 | 气体传感器模块 | 已上架 |
| `demo-prd-009` | SenseSensor H2S-100 硫化氢传感器模块 | 气体传感器模块 | 已上架 |

## 6. 内容列表

### 解决方案（SOLUTION）

| UUID | 标题 | 分类 | 状态 |
|------|------|------|------|
| `demo-sol-001` | 石油化工储罐区气体安全监测方案 | 石油化工安全 | 已发布 |
| `demo-sol-002` | 冶金钢铁高炉煤气安全监测方案 | 冶金钢铁安全 | 已发布 |
| `demo-sol-003` | 天然气场站及管道泄漏监测方案 | 能源电力安全 | 已发布 |
| `demo-sol-004` | 城市综合管廊气体安全监测方案 | 市政燃气安全 | 已发布 |

### 新闻（NEWS）

| UUID | 标题 | 分类 | 状态 |
|------|------|------|------|
| `demo-news-001` | InterSense 新一代 F-800 系列通过 ATEX 国际防爆认证 | 公司动态 | 已发布 |
| `demo-news-002` | InterSense 中标某大型石化基地气体监测系统项目 | 公司动态 | 已发布 |
| `demo-news-003` | 工信部发布《工业气体检测仪行业发展指导意见》 | 行业资讯 | 已发布 |
| `demo-news-004` | 红外气体传感技术的最新进展与工业应用趋势 | 技术前沿 | 已发布 |

## 7. 下载中心

### 产品资料（9 份）

| 显示名称 | 文件大小 | 类型 |
|----------|----------|------|
| SenseGuard-F800-固定式可燃气体检测仪 | 1,094 B | text/markdown |
| SenseGuard-F600-固定式有毒气体检测仪 | 1,017 B | text/markdown |
| SenseGuard-P200-便携式多气体检测仪 | 1,209 B | text/markdown |
| SenseGuard-P100-便携式单气体检测仪 | 1,032 B | text/markdown |
| SenseAlarm-C5000-气体报警控制器 | 1,200 B | text/markdown |
| SenseAlarm-C3000-区域报警控制器 | 974 B | text/markdown |
| SenseSensor-MQ4-甲烷传感器模块 | 934 B | text/markdown |
| SenseSensor-CO200-一氧化碳传感器模块 | 918 B | text/markdown |
| SenseSensor-H2S100-硫化氢传感器模块 | 911 B | text/markdown |

### 方案资料（4 份）

| 显示名称 | 文件大小 | 类型 |
|----------|----------|------|
| 石油化工储罐区气体安全监测方案 | 1,649 B | text/markdown |
| 冶金钢铁高炉煤气安全监测方案 | 1,331 B | text/markdown |
| 天然气场站及管道泄漏监测方案 | 1,385 B | text/markdown |
| 城市综合管廊气体安全监测方案 | 1,453 B | text/markdown |

## 8. 客户留言

| UUID | 姓名 | 手机 | 内容（前40字） | 状态 |
|------|------|------|---------------|------|
| `demo-msg-001` | 刘工 | 13912345678 | 你好，我们公司是做化工厂设备维护的，最近需要采购一批固定式可燃气体检测仪... | 已处理 |
| `demo-msg-002` | 王经理 | 18612345678 | 我们天然气门站目前在用某品牌的气体检测设备，但故障率较高... | 待处理 |
| `demo-msg-003` | 赵安全 | 13312345678 | 请问贵公司的便携式气体检测仪 P-200 是否支持定制气体组合... | 待处理 |
| `demo-msg-004` | 孙主任 | 17712345678 | 我们钢铁厂新上了一套高炉煤气回收装置，需要配套一套完整的 CO 监测系统... | 已处理 |
| `demo-msg-005` | 周工 | 15212345678 | 咨询一下，你们有没有针对地下车库的 CO 监测方案... | 待处理 |

## 9. 通用约定

- **登录页**: `/login`
- **注册页**: `/register`（注册用户默认角色 `USER`）
- **默认密码**: `123456`
- **图片上传**: jpg/png/webp，≤5MB
- **UUID 格式**: 36 位 CHAR(36)，应用层生成
