# 接口设计文档（API）

## 文档信息

- **项目名称**：工业气体报警企业官网系统
- **设计依据**：《需求文档》V1.6、《架构设计文档》V1.6、《类图设计文档（最终版）》
- **接口风格**：RESTful JSON，统一 `Result<T>` 返回
- **Base URL**：`/api/v1`
- **版本**：V1.6
- **最后更新**：2026-06-03

---

## 一、接口总览

### 1.1 接口清单

| 区域 | 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|------|
| 前台 | `GET` | `/public/products` | 产品列表（分页） | 无 |
| 前台 | `GET` | `/public/products/{uuid}` | 产品详情 | 无 |
| 前台 | `GET` | `/public/contents` | 内容列表（分页） | 无 |
| 前台 | `GET` | `/public/contents/{uuid}` | 内容详情 | 无 |
| 前台 | `GET` | `/public/categories` | 分类列表 | 无 |
| 前台 | `POST` | `/public/messages` | 提交留言 | 无 |
| 前台 | `POST` | `/public/register` | 客户注册 | 无 |
| 前台 | `POST` | `/public/ai/chat` | AI 智能助手对话 | 无（IP 限流） |
| 后台 | `GET` | `/admin/captcha` | 获取图形验证码 | 无 |
| 后台 | `POST` | `/admin/login` | 管理员登录 | 无 |
| 后台 | `POST` | `/admin/logout` | 管理员登出 | JWT |
| 后台 | `GET` | `/admin/currentUser` | 当前用户信息 | JWT |
| 后台 | `POST` | `/admin/resetPassword` | 重置密码 | JWT |
| 后台 | `POST` | `/admin/products` | 新增产品 | JWT |
| 后台 | `GET` | `/admin/products` | 产品列表（分页） | JWT |
| 后台 | `PUT` | `/admin/products/{uuid}` | 编辑产品 | JWT |
| 后台 | `DELETE` | `/admin/products/{uuid}` | 删除产品 | JWT |
| 后台 | `POST` | `/admin/products/{uuid}/publish` | 上架产品 | JWT |
| 后台 | `POST` | `/admin/products/{uuid}/unpublish` | 下架产品 | JWT |
| 后台 | `POST` | `/admin/contents` | 新增内容 | JWT |
| 后台 | `GET` | `/admin/contents` | 内容列表（分页） | JWT |
| 后台 | `PUT` | `/admin/contents/{uuid}` | 编辑内容 | JWT |
| 后台 | `DELETE` | `/admin/contents/{uuid}` | 删除内容 | JWT |
| 后台 | `POST` | `/admin/contents/{uuid}/publish` | 发布内容 | JWT |
| 后台 | `GET` | `/admin/categories` | 分类列表（后台） | JWT |
| 后台 | `POST` | `/admin/categories` | 新增分类 | JWT |
| 后台 | `PUT` | `/admin/categories/{uuid}` | 编辑分类 | JWT |
| 后台 | `DELETE` | `/admin/categories/{uuid}` | 删除分类 | JWT |
| 后台 | `GET` | `/admin/messages` | 留言列表（分页） | JWT |
| 后台 | `PUT` | `/admin/messages/{uuid}/process` | 标记已处理 | JWT |
| 后台 | `PUT` | `/admin/messages/process/batch` | 批量标记已处理 | JWT |
| 后台 | `PUT` | `/admin/messages/{uuid}/assign` | 指派留言 | JWT |
| 后台 | `GET` | `/admin/staff` | 员工列表（分页） | JWT |
| 后台 | `GET` | `/admin/staff/{uuid}` | 员工详情 | JWT |
| 后台 | `POST` | `/admin/staff` | 新增员工 | JWT |
| 后台 | `PUT` | `/admin/staff/{uuid}` | 编辑员工 | JWT |
| 后台 | `DELETE` | `/admin/staff/{uuid}` | 删除员工 | JWT |
| 后台 | `GET` | `/admin/devices` | 设备列表（分页） | JWT |
| 后台 | `GET` | `/admin/devices/{uuid}` | 设备详情 | JWT |
| 后台 | `POST` | `/admin/devices` | 新增设备 | JWT |
| 后台 | `PUT` | `/admin/devices/{uuid}` | 编辑设备 | JWT |
| 后台 | `DELETE` | `/admin/devices/{uuid}` | 删除设备 | JWT |
| 后台 | `POST` | `/admin/devices/{uuid}/mark-abnormal` | 标记设备异常 | JWT |
| 后台 | `POST` | `/admin/devices/{uuid}/mark-normal` | 恢复设备正常 | JWT |
| 后台 | `POST` | `/admin/devices/{uuid}/mark-offline` | 标记设备离线 | JWT |
| 后台 | `POST` | `/admin/devices/{uuid}/start-maintenance` | 进入维护模式 | JWT |
| 后台 | `POST` | `/admin/devices/{uuid}/end-maintenance` | 结束维护 | JWT |
| 后台 | `GET` | `/admin/devices/{uuid}/data` | 设备历史数据 | JWT |
| 后台 | `GET` | `/admin/devices/{uuid}/latest` | 设备最新数据 | JWT |
| 后台 | `GET` | `/admin/alert-rules` | 报警规则列表 | JWT |
| 后台 | `GET` | `/admin/alert-rules/{uuid}` | 报警规则详情 | JWT |
| 后台 | `POST` | `/admin/alert-rules` | 新增报警规则 | JWT |
| 后台 | `PUT` | `/admin/alert-rules/{uuid}` | 编辑报警规则 | JWT |
| 后台 | `DELETE` | `/admin/alert-rules/{uuid}` | 删除报警规则 | JWT |
| 后台 | `POST` | `/admin/alert-rules/{uuid}/enable` | 启用规则 | JWT |
| 后台 | `POST` | `/admin/alert-rules/{uuid}/disable` | 禁用规则 | JWT |
| 后台 | `GET` | `/admin/alerts` | 报警记录列表（分页） | JWT |
| 后台 | `GET` | `/admin/alerts/{uuid}` | 报警详情 | JWT |
| 后台 | `POST` | `/admin/alerts/{uuid}/confirm` | 确认报警 | JWT |
| 后台 | `POST` | `/admin/alerts/{uuid}/resolve` | 解决报警 | JWT |
| 后台 | `POST` | `/admin/alerts/{uuid}/close` | 关闭报警 | JWT |
| 后台 | `GET` | `/admin/alerts/{uuid}/notifications` | 报警通知记录 | JWT |
| 后台 | `GET` | `/admin/dashboard/stats` | 大屏统计数据 | JWT |
| 前台 | `POST` | `/public/device-data` | 设备数据上报（device-collector） | API Token |
| 前台 | `GET` | `/public/downloads` | 公开文件列表 | 无 |
| 前台 | `GET` | `/public/comments` | 评论列表（按 targetType+targetUuid） | 无 |
| 前台 | `GET` | `/public/health` | 健康检查 | 无 |
| 前台 | `GET` | `/public/search` | 全文搜索 | 无 |
| 后台 | `GET` | `/admin/users` | 用户列表（分页） | JWT |
| 后台 | `GET` | `/admin/users/{uuid}` | 用户详情 | JWT |
| 后台 | `PUT` | `/admin/users/{uuid}/role` | 修改用户角色 | JWT |
| 后台 | `GET` | `/admin/workorders` | 工单列表（分页） | JWT |
| 后台 | `POST` | `/admin/workorders` | 新增工单 | JWT |
| 后台 | `PUT` | `/admin/workorders/{uuid}` | 编辑工单 | JWT |
| 后台 | `PUT` | `/admin/workorders/{uuid}/assign` | 指派工单 | JWT |
| 后台 | `PUT` | `/admin/workorders/{uuid}/complete` | 完成工单 | JWT |
| 后台 | `GET` | `/admin/comments` | 评论管理列表 | JWT |
| 后台 | `DELETE` | `/admin/comments/{uuid}` | 删除评论 | JWT |
| 后台 | `GET` | `/admin/customers/360` | 客户360全景 | JWT |
| 后台 | `GET` | `/admin/downloads` | 文件管理列表 | JWT |
| 后台 | `POST` | `/admin/downloads` | 上传文件 | JWT |
| 后台 | `PUT` | `/admin/downloads/{uuid}` | 编辑文件信息 | JWT |
| 后台 | `DELETE` | `/admin/downloads/{uuid}` | 删除文件 | JWT |
| 后台 | `GET` | `/admin/operation-logs` | 操作日志列表（分页） | JWT |
| 后台 | `GET` | `/admin/system-config` | 系统配置列表 | JWT |
| 后台 | `PUT` | `/admin/system-config/{key}` | 更新系统配置 | JWT |
| 后台 | `GET` | `/admin/notifications` | 通知记录列表 | JWT |
| 后台 | `POST` | `/admin/notifications/{uuid}/retry` | 重试通知发送 | JWT |
| 后台 | `GET` | `/admin/ai/chat` | AI 对话（SSE 流式） | JWT |
| 员工 | `GET` | `/staff/dashboard` | 员工工作台统计 | JWT |
| 员工 | `GET` | `/staff/workorders` | 我的工单列表 | JWT |
| 员工 | `PUT` | `/staff/workorders/{uuid}/complete` | 完成工单 | JWT |
| 员工 | `GET` | `/staff/inquiries` | 我的留言处理列表 | JWT |
| 员工 | `GET` | `/staff/notifications` | 我的通知列表 | JWT |
| 员工 | `PUT` | `/staff/profile` | 编辑个人信息 | JWT |
| 客户 | `GET` | `/user/profile` | 个人信息 | JWT |
| 客户 | `GET` | `/user/devices` | 我的设备列表 | JWT |
| 客户 | `GET` | `/user/devices/{uuid}/data` | 设备数据趋势 | JWT |
| 客户 | `GET` | `/user/alerts` | 我的告警记录 | JWT |
| 客户 | `GET` | `/user/workorders` | 我的工单 | JWT |
| 客户 | `POST` | `/user/workorders` | 提交工单 | JWT |
| 大屏 | `GET` | `/dashboard/overview` | 大屏概览统计 | JWT |
| 大屏 | `GET` | `/dashboard/devices` | 设备实时状态 | JWT |
| 大屏 | `GET` | `/dashboard/alerts` | 告警实时统计 | JWT |
| 大屏 | `WS` | `/dashboard/ws` | WebSocket 实时推送 | JWT |

---

## 二、通用约定

### 2.1 请求规范

| 项目 | 规范 |
|------|------|
| Content-Type | `application/json`；文件上传使用 `multipart/form-data` |
| 认证方式 | 后台接口在 Header 中携带 `Authorization: Bearer <token>` |
| 字符编码 | UTF-8 |
| 分页 | `page` 从 1 开始（后端内部转为 0-based），`size` 默认 20，最大 20 |
| 时间格式 | `YYYY-MM-DD HH:mm:ss`（字符串） |

### 2.2 统一返回格式

所有接口响应统一包裹为 `Result<T>`：

```json
{
  "code": 0,
  "message": "成功",
  "data": {},
  "success": true
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | `int` | `0` 成功，`4xxx` 客户端错误，`5xxx` 服务端错误 |
| `message` | `String` | 提示信息 |
| `data` | `T` | 业务数据，无数据时为 `null` |
| `success` | `boolean` | `true` 成功，`false` 失败 |

### 2.3 分页返回格式

分页查询统一使用 `Page<T>`：

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "content": [],
    "totalElements": 50,
    "totalPages": 3,
    "size": 20,
    "number": 1
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `content` | `T[]` | 数据列表 |
| `totalElements` | `long` | 总记录数 |
| `totalPages` | `int` | 总页数 |
| `size` | `int` | 每页条数 |
| `number` | `int` | 当前页码（后端返回从 1 开始） |

### 2.4 错误码表

| code | HTTP 状态码 | 含义 | 前端处理 |
|------|------------|------|---------|
| `0` | 200 | 成功 | 正常展示 |
| `4000` | 400 | 参数校验失败 | Toast 提示，标红对应字段 |
| `4001` | 404 | 产品不存在 | Toast 提示 |
| `4002` | 404 | 内容不存在 | Toast 提示 |
| `4003` | 404 | 分类不存在 | Toast 提示 |
| `4004` | 404 | 留言不存在 | Toast 提示 |
| `4005` | 404 | 管理员不存在 | 跳转登录页 |
| `4006` | 403 | 账户已锁定 | Toast 提示剩余解锁时间 |
| `4007` | 401 | 密码错误 | Toast 提示剩余尝试次数 |
| `4008` | 401 | 未认证 | 清除 Token，跳转登录页 |
| `4009` | 403 | 无权限 | Toast "无操作权限" |
| `5001` | 500 | 系统内部错误 | Toast "系统繁忙，请稍后重试" |

---

## 三、前台公开接口

> 以下接口均无需认证，Base URL 前缀 `/api/v1/public`

### 3.1 产品列表

```
GET /api/v1/public/products
```

**请求参数（Query）**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `categoryUuid` | `String` | 否 | 产品分类 ID，为空则查全部 |
| `page` | `int` | 否 | 页码，从 1 开始，默认 1 |
| `size` | `int` | 否 | 每页条数，默认 20，最大 20 |

**请求示例**：
```
GET /api/v1/public/products?categoryUuid=cat-001&page=1&size=20
```

**成功响应**（`Result<Page<ProductVO>>`）：

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "content": [
      {
        "productUuid": "uuid-xxx",
        "name": "气体探测器 A1",
        "description": "适用于石油化工场景的固定式可燃气体探测器，支持 4-20mA 和 RS485 输出。",
        "coverImage": "https://cdn.example.com/images/a1.jpg",
        "categoryUuid": "cat-001",
        "categoryName": "固定式探测器",
        "status": "PUBLISHED",
        "createdAt": "2026-01-15 10:30:00"
      }
    ],
    "totalElements": 50,
    "totalPages": 3,
    "size": 20,
    "number": 1
  },
  "success": true
}
```

**ProductVO 字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `productUuid` | `String` | 产品唯一标识 |
| `name` | `String` | 产品名称 |
| `description` | `String` | 简短描述（列表用，非富文本全文） |
| `coverImage` | `String` | 封面图 URL |
| `categoryUuid` | `String` | 所属分类 ID |
| `categoryName` | `String` | 所属分类名称（联表查询后填充） |
| `status` | `String` | 产品状态：`DRAFT` / `PUBLISHED` / `UNPUBLISHED` |
| `createdAt` | `String` | 创建时间，`YYYY-MM-DD HH:mm:ss` |

**业务规则**：
- 仅返回 `status = PUBLISHED` 的产品
- 默认按 `created_at DESC` 排序
- 后台逻辑删除（`deleted = 1`）的产品不展示

---

### 3.2 产品详情

```
GET /api/v1/public/products/{uuid}
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `uuid` | `String` | 产品唯一标识 |

**成功响应**（`Result<ProductDetailVO>`）：

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "productUuid": "uuid-xxx",
    "name": "气体探测器 A1",
    "description": "<p>富文本 HTML 内容...</p>",
    "coverImage": "https://cdn.example.com/images/a1.jpg",
    "images": [
      { "url": "https://cdn.example.com/1.jpg", "altText": "正面图", "sortOrder": 0 },
      { "url": "https://cdn.example.com/2.jpg", "altText": "侧面图", "sortOrder": 1 }
    ],
    "attributes": [
      { "attrKey": "检测气体", "attrVal": "甲烷" },
      { "attrKey": "测量范围", "attrVal": "0-100%LEL" }
    ],
    "categoryUuid": "cat-001",
    "categoryName": "固定式探测器",
    "status": "PUBLISHED",
    "createdAt": "2026-01-15 10:30:00"
  },
  "success": true
}
```

**ProductDetailVO 字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `productUuid` | `String` | 产品唯一标识 |
| `name` | `String` | 产品名称 |
| `description` | `String` | 产品描述（富文本 HTML） |
| `coverImage` | `String` | 封面图 URL |
| `images` | `ImageVO[]` | 产品多图列表 |
| `attributes` | `AttributeVO[]` | 产品参数键值对列表 |
| `categoryUuid` | `String` | 所属分类 ID |
| `categoryName` | `String` | 所属分类名称 |
| `status` | `String` | 产品状态 |
| `createdAt` | `String` | 创建时间 |

**ImageVO**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `url` | `String` | 图片 URL |
| `altText` | `String` | 替代文本 |
| `sortOrder` | `int` | 排序序号 |

**AttributeVO**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `attrKey` | `String` | 参数名称 |
| `attrVal` | `String` | 参数值 |

**错误响应**：

```json
{ "code": 4001, "message": "产品不存在", "data": null, "success": false }
```

---

### 3.3 内容列表

```
GET /api/v1/public/contents
```

**请求参数（Query）**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `type` | `String` | 是 | 内容类型，MVP 固定为 `SOLUTION` |
| `categoryUuid` | `String` | 否 | 内容分类 ID |
| `page` | `int` | 否 | 页码，默认 1 |
| `size` | `int` | 否 | 每页条数，默认 20 |

**请求示例**：
```
GET /api/v1/public/contents?type=SOLUTION&categoryUuid=cat-s01&page=1&size=20
```

**成功响应**（`Result<Page<ContentVO>>`）：

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "content": [
      {
        "contentUuid": "uuid-xxx",
        "title": "石油化工气体检测方案",
        "summary": "针对石油化工行业的可燃/有毒气体检测整体解决方案。",
        "coverImage": "https://cdn.example.com/images/s1.jpg",
        "type": "SOLUTION",
        "categoryUuid": "cat-s01",
        "categoryName": "石油化工",
        "status": "PUBLISHED",
        "createdAt": "2026-02-20 14:00:00"
      }
    ],
    "totalElements": 10,
    "totalPages": 1,
    "size": 20,
    "number": 1
  },
  "success": true
}
```

**ContentVO 字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `contentUuid` | `String` | 内容唯一标识 |
| `title` | `String` | 标题 |
| `summary` | `String` | 摘要 |
| `coverImage` | `String` | 封面图 URL |
| `type` | `String` | 内容类型：`SOLUTION` / `NEWS` |
| `categoryUuid` | `String` | 所属分类 ID |
| `categoryName` | `String` | 所属分类名称 |
| `status` | `String` | 状态：`DRAFT` / `PUBLISHED` |
| `createdAt` | `String` | 创建时间 |

**业务规则**：
- 仅返回 `status = PUBLISHED` 的内容
- MVP 阶段 `type` 固定为 `SOLUTION`
- `NEWS` 类型为后续版本预留

---

### 3.4 内容详情

```
GET /api/v1/public/contents/{uuid}
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `uuid` | `String` | 内容唯一标识 |

**成功响应**（`Result<ContentDetailVO>`）：

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "contentUuid": "uuid-xxx",
    "title": "石油化工气体检测方案",
    "body": "<p>富文本 HTML 内容...</p>",
    "coverImage": "https://cdn.example.com/images/s1.jpg",
    "type": "SOLUTION",
    "categoryUuid": "cat-s01",
    "categoryName": "石油化工",
    "status": "PUBLISHED",
    "createdAt": "2026-02-20 14:00:00",
    "updatedAt": "2026-03-01 09:00:00"
  },
  "success": true
}
```

**ContentDetailVO 字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `contentUuid` | `String` | 内容唯一标识 |
| `title` | `String` | 标题 |
| `body` | `String` | 正文（富文本 HTML） |
| `coverImage` | `String` | 封面图 URL |
| `type` | `String` | 内容类型 |
| `categoryUuid` | `String` | 所属分类 ID |
| `categoryName` | `String` | 所属分类名称 |
| `status` | `String` | 状态 |
| `createdAt` | `String` | 创建时间 |
| `updatedAt` | `String` | 更新时间 |

---

### 3.5 分类列表

```
GET /api/v1/public/categories
```

**请求参数（Query）**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `type` | `String` | 是 | `PRODUCT_CATEGORY` 或 `CONTENT_CATEGORY` |

**请求示例**：
```
GET /api/v1/public/categories?type=PRODUCT_CATEGORY
```

**成功响应**（`Result<List<CategoryVO>>`）：

```json
{
  "code": 0,
  "message": "成功",
  "data": [
    {
      "categoryUuid": "cat-001",
      "name": "固定式探测器",
      "type": "PRODUCT_CATEGORY",
      "parentUuid": null,
      "sortOrder": 1,
      "children": [
        {
          "categoryUuid": "cat-001-1",
          "name": "可燃气体",
          "type": "PRODUCT_CATEGORY",
          "parentUuid": "cat-001",
          "sortOrder": 1
        }
      ]
    }
  ],
  "success": true
}
```

**CategoryVO 字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `categoryUuid` | `String` | 分类唯一标识 |
| `name` | `String` | 分类名称 |
| `type` | `String` | 分类类型：`PRODUCT_CATEGORY` / `CONTENT_CATEGORY` |
| `parentUuid` | `String` | 父分类 ID，`null` 表示顶级分类 |
| `sortOrder` | `int` | 排序序号 |
| `children` | `CategoryVO[]` | 子分类列表（树形结构） |

**业务规则**：
- 返回树形结构，`parentUuid` 为 `null` 的为顶级分类
- 按 `sortOrder ASC` 排序
- 使用 Redis 缓存，TTL 1 小时

**错误响应**：

```json
{ "code": 4003, "message": "分类不存在", "data": null, "success": false }
```

---

### 3.6 提交留言

```
POST /api/v1/public/messages
```

**请求体**（`SubmitMessageDTO`）：

| 字段 | 类型 | 必填 | 校验规则 |
|------|------|------|---------|
| `name` | `String` | 是 | 不超过 50 字符 |
| `phone` | `String` | 是 | 11 位手机号（`1` 开头） |
| `content` | `String` | 是 | 5-500 字符 |

**请求示例**：

```json
{
  "name": "张三",
  "phone": "13800138000",
  "content": "我公司需要采购一批可燃气体探测器，请尽快联系。"
}
```

**成功响应**：

```json
{
  "code": 0,
  "message": "提交成功，我们将尽快联系您",
  "data": null,
  "success": true
}
```

**失败响应**：

```json
{ "code": 4000, "message": "手机号格式不正确", "data": null, "success": false }
{ "code": 4000, "message": "您已提交过，请稍后再试", "data": null, "success": false }
{ "code": 4000, "message": "操作过于频繁，请稍后重试", "data": null, "success": false }
{ "code": 5001, "message": "网络异常，请稍后重试", "data": null, "success": false }
```

**业务规则**：
- 手机号 60 秒内仅允许提交一次（Redis 防刷）
- 同一 IP 每分钟最多 3 次（Redis IP 限流）
- `ip` 字段由后端通过 `HttpServletRequest.getRemoteAddr()` 获取并存储，前端不传
- `email` 字段已移除（MVP 阶段不采集）
- 提交成功后留言状态默认为 `PENDING`

**Controller 层接收参数**：
- Controller 需要注入 `HttpServletRequest` 以获取客户端 IP

---

### 3.7 客户注册

```
POST /api/v1/public/register
```

**请求体**（`RegisterDTO`）：

| 字段 | 类型 | 必填 | 校验规则 |
|------|------|------|---------|
| `username` | `String` | 是 | 4-20 字符 |
| `password` | `String` | 是 | 6-20 字符 |
| `phone` | `String` | 否 | 11 位手机号 |
| `company` | `String` | 否 | 公司名称 |

**请求示例**：

```json
{
  "username": "customer001",
  "password": "Pass@123",
  "phone": "13800138000",
  "company": "某某石化有限公司"
}
```

**成功响应**：

```json
{ "code": 0, "message": "注册成功", "data": null, "success": true }
```

**业务规则**：
- 用户名全局唯一，重复返回 `4000`
- 密码通过 BCrypt 加密存储
- 注册后默认角色为 `USER`
- `phone` 和 `company` 为可选字段

---

### 3.8 AI 智能助手对话

```
POST /api/v1/public/ai/chat
```

**请求体**（`SendMessageDTO`）：

| 字段 | 类型 | 必填 | 校验规则 |
|------|------|------|---------|
| `sessionId` | `String` | 否 | 会话ID，首次为空则由服务端生成 |
| `message` | `String` | 是 | 1-500 字符 |

**请求示例**：

```json
{
  "sessionId": "uuid-session-xxx",
  "message": "我需要一款检测甲烷的气体探测器"
}
```

**成功响应**（`Result<ChatResponseVO>`）：

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "sessionId": "uuid-session-xxx",
    "reply": "根据您的需求，推荐以下产品：...",
    "recommendedProducts": [
      {
        "productUuid": "uuid-xxx",
        "name": "气体探测器 A1",
        "summary": "适用于石油化工场景的固定式可燃气体探测器...",
        "imageUrl": "https://cdn.example.com/images/a1.jpg"
      }
    ],
    "recommendedSolutions": [
      {
        "contentUuid": "uuid-xxx",
        "title": "石油化工气体检测方案",
        "summary": "针对石油化工行业的可燃/有毒气体检测整体解决方案...",
        "imageUrl": "https://cdn.example.com/images/s1.jpg"
      }
    ]
  },
  "success": true
}
```

**ChatResponseVO 字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `sessionId` | `String` | 会话ID（用于多轮对话） |
| `reply` | `String` | AI 回复内容 |
| `recommendedProducts` | `RecommendedProductVO[]` | 匹配的产品推荐（最多3个） |
| `recommendedSolutions` | `RecommendedSolutionVO[]` | 匹配的方案推荐（最多3个） |

**业务规则**：
- 基于 DeepSeek 大模型 + LangChain4j 框架
- 向量语义搜索优先 → 无结果回退关键字 LIKE → 都无则通用建议
- 产品/方案描述截断至 100 字控制 Token 成本
- 同一 IP 每分钟最多 10 次请求（Redis 限流）
- 会话历史保留 24 小时（Redis），最多 10 轮（5 问 5 答）
- `sessionId` 为空时服务端自动创建新会话

**失败响应**：

```json
{ "code": 4000, "message": "提问过于频繁，请稍后重试", "data": null, "success": false }
```

---

## 四、后台管理接口

> 以下接口需 JWT 认证（登录/验证码接口除外），Base URL 前缀 `/api/v1/admin`
> 认证 Header：`Authorization: Bearer <token>`

### 4.1 获取图形验证码

```
GET /api/v1/admin/captcha
```

> **安全配置**：此接口必须在 Spring Security 中 `permitAll()` 放行。

**请求参数**：无

**成功响应**（`Result<CaptchaVO>`）：

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "captchaToken": "captcha-token-xxx",
    "captchaImage": "data:image/png;base64,iVBORw0KGgo..."
  },
  "success": true
}
```

**CaptchaVO 字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `captchaToken` | `String` | 验证码Token，登录时需回传 |
| `captchaImage` | `String` | Base64 编码的验证码图片 |

**业务规则**：
- 验证码为 4 位数字
- Token 有效期 5 分钟（Redis 存储）
- 每次调用生成新的验证码，旧 Token 失效

---

### 4.2 管理员登录

```
POST /api/v1/admin/login
```

> **安全配置**：此接口必须在 Spring Security 中 `permitAll()` 放行。

**请求体**（`LoginDTO`）：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `username` | `String` | 是 | 用户名 |
| `password` | `String` | 是 | 明文密码 |
| `captcha` | `String` | 是 | 图形验证码（4位数字） |
| `captchaToken` | `String` | 是 | 验证码Token（从 `/admin/captcha` 获取） |

**请求示例**：

```json
{
  "username": "admin",
  "password": "Admin@123",
  "captcha": "4829",
  "captchaToken": "captcha-token-xxx"
}
```

**成功响应**：

```json
{
  "code": 0,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "userUuid": "user-xxx",
    "username": "admin",
    "role": "ADMIN"
  },
  "success": true
}
```

**失败响应**：

```json
{ "code": 4005, "message": "管理员不存在", "data": null, "success": false }
{ "code": 4007, "message": "用户名或密码错误，剩余尝试次数：3", "data": null, "success": false }
{ "code": 4006, "message": "账号已锁定，请30分钟后重试", "data": null, "success": false }
```

**业务规则**：
- 密码使用 BCrypt 校验
- 连续 5 次失败锁定 30 分钟
- 登录成功后生成 JWT Token，包含 `userUuid` 和 `role`
- 登录成功重置 `failCount` 为 0
- 若账号已锁定且未到解锁时间，直接返回锁定提示
- 验证码从 `/admin/captcha` 获取，登录时必须校验

---

### 4.3 管理员登出

```
POST /api/v1/admin/logout
```

**请求头**：`Authorization: Bearer <token>`

**请求体**：无

**成功响应**：

```json
{ "code": 0, "message": "已登出", "data": null, "success": true }
```

**业务规则**：
- 将当前 JWT Token 加入 Redis 黑名单
- 黑名单 TTL 与 Token 剩余有效期一致
- `JwtAuthFilter` 每次请求检查黑名单，命中则返回 401

---

### 4.4 重置密码

```
POST /api/v1/admin/resetPassword
```

**请求体**（`ResetPasswordDTO`）：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `username` | `String` | 是 | 目标用户名 |
| `newPassword` | `String` | 是 | 新密码，6-20 字符 |

**请求示例**：

```json
{
  "username": "admin",
  "newPassword": "NewPass@456"
}
```

**成功响应**：

```json
{ "code": 0, "message": "密码重置成功", "data": null, "success": true }
```

**业务规则**：
- 仅管理员可操作（需 JWT 认证）
- 新密码通过 BCrypt 加密后存储
- 重置后不清除锁定状态（需等待自动解锁）

---

### 4.5 当前用户信息

```
GET /api/v1/admin/currentUser
```

**成功响应**（`Result<UserVO>`）：

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "userUuid": "user-xxx",
    "username": "admin",
    "locked": false
  },
  "success": true
}
```

**UserVO 字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `userUuid` | `String` | 用户唯一标识 |
| `username` | `String` | 用户名 |
| `locked` | `boolean` | 是否锁定 |

**业务规则**：
- 从 JWT Token 中解析 `userUuid`，查询用户信息
- 不返回 `passwordHash`、`failCount` 等敏感字段

---

### 4.6 新增产品

```
POST /api/v1/admin/products
```

- Content-Type: `multipart/form-data`

**请求参数**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | `String` | 是 | 产品名称，≤100 字符，全局唯一 |
| `categoryUuid` | `String` | 是 | 所属分类 ID |
| `coverImage` | `File` | 是 | 封面图，仅允许 `jpg`/`png`/`webp`，≤5MB |
| `images` | `File[]` | 是 | 产品多图，至少 1 张，同上格式限制 |
| `description` | `String` | 是 | 富文本描述 |
| `attributes[0].attrKey` | `String` | 否 | 参数名 |
| `attributes[0].attrVal` | `String` | 否 | 参数值 |
| `status` | `String` | 否 | 默认 `PUBLISHED` |

**请求示例**（FormData）：

```
name: "气体探测器 A2"
categoryUuid: "cat-001"
coverImage: (binary)
images: (binary[])
description: "<p>新一代智能气体探测器...</p>"
attributes[0].attrKey: "检测气体"
attributes[0].attrVal: "甲烷"
status: "PUBLISHED"
```

**成功响应**（`Result<ProductVO>`）：

```json
{
  "code": 0,
  "message": "新增成功",
  "data": {
    "productUuid": "uuid-new",
    "name": "气体探测器 A2",
    "categoryUuid": "cat-001",
    "status": "PUBLISHED"
  },
  "success": true
}
```

**业务规则**：
- 产品名称全局唯一，重复返回 `4000`
- 图片上传到文件服务器/OSS，数据库仅保存 URL
- 图片上传前后端需校验：格式（仅 `jpg`/`png`/`webp`）、大小（≤5MB）
- `attributes` 为键值对数组，不限数量

---

### 4.7 产品列表（后台）

```
GET /api/v1/admin/products
```

**请求参数（Query）**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `name` | `String` | 产品名称模糊搜索 |
| `categoryUuid` | `String` | 分类筛选 |
| `status` | `String` | 状态筛选：`DRAFT` / `PUBLISHED` / `UNPUBLISHED` |
| `page` | `int` | 页码，默认 1 |
| `size` | `int` | 每页条数，默认 20 |

**成功响应**（`Result<Page<ProductVO>>`）：

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "content": [
      {
        "productUuid": "uuid-xxx",
        "name": "气体探测器 A1",
        "description": "简短描述...",
        "coverImage": "https://cdn.example.com/images/a1.jpg",
        "categoryUuid": "cat-001",
        "categoryName": "固定式探测器",
        "status": "PUBLISHED",
        "createdAt": "2026-01-15 10:30:00"
      }
    ],
    "totalElements": 50,
    "totalPages": 3,
    "size": 20,
    "number": 1
  },
  "success": true
}
```

**业务规则**：
- 展示所有状态（含 `DRAFT` 和 `UNPUBLISHED`），与前台仅展示 `PUBLISHED` 不同
- 默认按 `created_at DESC` 排序
- `deleted = 1` 的产品不展示

---

### 4.8 编辑产品

```
PUT /api/v1/admin/products/{uuid}
```

- Content-Type: `multipart/form-data`

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `uuid` | `String` | 产品唯一标识 |

**请求参数**：同 4.3 新增产品，所有字段可选填（仅提交需修改的字段）。

**成功响应**（`Result<ProductVO>`）：

```json
{ "code": 0, "message": "修改成功", "data": { ... }, "success": true }
```

---

### 4.9 删除产品

```
DELETE /api/v1/admin/products/{uuid}
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `uuid` | `String` | 产品唯一标识 |

**成功响应**：

```json
{ "code": 0, "message": "删除成功", "data": null, "success": true }
```

**业务规则**：
- **逻辑删除**：设置 `deleted = 1`，而非物理删除
- 删除后前台不再展示
- 删除操作需前端二次确认弹窗

---

### 4.10 上架产品

```
POST /api/v1/admin/products/{uuid}/publish
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `uuid` | `String` | 产品唯一标识 |

**请求体**：无

**成功响应**：

```json
{ "code": 0, "message": "上架成功", "data": null, "success": true }
```

**错误响应**：

```json
{ "code": 4001, "message": "产品不存在", "data": null, "success": false }
```

**业务规则**：
- 调用聚合根 `product.publish()`，领域内校验：
  - 若已为 `PUBLISHED` 状态，抛出 `ProductAlreadyPublishedException`
  - 若产品已被逻辑删除（`deleted = 1`），上架前需先恢复
- 上架后前台立即可见

---

### 4.11 下架产品

```
POST /api/v1/admin/products/{uuid}/unpublish
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `uuid` | `String` | 产品唯一标识 |

**请求体**：无

**成功响应**：

```json
{ "code": 0, "message": "下架成功", "data": null, "success": true }
```

**业务规则**：
- 调用聚合根 `product.unpublish()`，领域内校验：
  - 若当前非 `PUBLISHED` 状态，抛出异常
- 下架后前台不再展示，后台仍可查到

---

### 4.12 新增内容

```
POST /api/v1/admin/contents
```

- Content-Type: `multipart/form-data`

**请求参数**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `title` | `String` | 是 | 标题，≤200 字符 |
| `type` | `String` | 是 | 内容类型，MVP 固定传 `SOLUTION` |
| `categoryUuid` | `String` | 是 | 内容分类 ID |
| `body` | `String` | 是 | 富文本内容 |
| `coverImage` | `File` | 否 | 封面图，`jpg`/`png`/`webp`，≤5MB |
| `status` | `String` | 否 | 默认 `PUBLISHED` |

**成功响应**（`Result<ContentVO>`）：

```json
{
  "code": 0,
  "message": "新增成功",
  "data": {
    "contentUuid": "uuid-new",
    "title": "石油化工气体检测方案",
    "type": "SOLUTION",
    "status": "PUBLISHED"
  },
  "success": true
}
```

---

### 4.13 内容列表（后台）

```
GET /api/v1/admin/contents
```

**请求参数（Query）**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `title` | `String` | 标题模糊搜索 |
| `type` | `String` | 内容类型，MVP 固定传 `SOLUTION` |
| `categoryUuid` | `String` | 分类筛选 |
| `status` | `String` | 状态筛选：`DRAFT` / `PUBLISHED` |
| `page` | `int` | 页码，默认 1 |
| `size` | `int` | 每页条数，默认 20 |

**成功响应**（`Result<Page<ContentVO>>`）：

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "content": [
      {
        "contentUuid": "uuid-xxx",
        "title": "石油化工气体检测方案",
        "summary": "简短摘要...",
        "coverImage": "https://cdn.example.com/images/s1.jpg",
        "type": "SOLUTION",
        "categoryUuid": "cat-s01",
        "categoryName": "石油化工",
        "status": "PUBLISHED",
        "createdAt": "2026-02-20 14:00:00"
      }
    ],
    "totalElements": 10,
    "totalPages": 1,
    "size": 20,
    "number": 1
  },
  "success": true
}
```

---

### 4.14 编辑内容

```
PUT /api/v1/admin/contents/{uuid}
```

- Content-Type: `multipart/form-data`

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `uuid` | `String` | 内容唯一标识 |

**请求参数**：同 4.9 新增内容，所有字段可选填。

**成功响应**：

```json
{ "code": 0, "message": "修改成功", "data": { ... }, "success": true }
```

---

### 4.15 删除内容

```
DELETE /api/v1/admin/contents/{uuid}
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `uuid` | `String` | 内容唯一标识 |

**成功响应**：

```json
{ "code": 0, "message": "删除成功", "data": null, "success": true }
```

**业务规则**：
- 逻辑删除，设置 `deleted = 1`
- 删除后前台不再展示
- 需前端二次确认

---

### 4.16 发布内容

```
POST /api/v1/admin/contents/{uuid}/publish
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `uuid` | `String` | 内容唯一标识 |

**请求体**：无

**成功响应**：

```json
{ "code": 0, "message": "发布成功", "data": null, "success": true }
```

**业务规则**：
- 调用聚合根 `content.publish()`
- 若已为 `PUBLISHED` 状态，抛出异常

---

### 4.17 留言列表

```
GET /api/v1/admin/messages
```

**请求参数（Query）**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `name` | `String` | 姓名模糊搜索 |
| `phone` | `String` | 电话模糊搜索 |
| `status` | `String` | 状态筛选：`PENDING` / `PROCESSED` |
| `page` | `int` | 页码，默认 1 |
| `size` | `int` | 每页条数，默认 20 |

**成功响应**（`Result<Page<MessageVO>>`）：

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "content": [
      {
        "messageUuid": "uuid-xxx",
        "name": "张三",
        "phone": "138****8000",
        "content": "需要采购一批可燃气体探测器。",
        "status": "PENDING",
        "processor": null,
        "remark": null,
        "submittedAt": "2026-03-10 09:30:00",
        "processedAt": null
      }
    ],
    "totalElements": 25,
    "totalPages": 2,
    "size": 20,
    "number": 1
  },
  "success": true
}
```

**MessageVO 字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `messageUuid` | `String` | 留言唯一标识 |
| `name` | `String` | 联系人姓名 |
| `phone` | `String` | 联系电话（后台可脱敏中间 4 位） |
| `content` | `String` | 需求描述 |
| `status` | `String` | 状态：`PENDING` / `IN_PROGRESS` / `PROCESSED` |
| `processor` | `String` | 处理人（处理后才填充） |
| `remark` | `String` | 处理备注（处理后才填充） |
| `assignedStaffUuid` | `String` | 指派员工UUID（指派后填充） |
| `assignedStaffName` | `String` | 指派员工姓名（指派后填充） |
| `submittedAt` | `String` | 提交时间 |
| `processedAt` | `String` | 处理时间（处理后才填充） |

**状态流转**：

```
PENDING ──(指派)──→ IN_PROGRESS ──(处理)──→ PROCESSED
```

**业务规则**：
- 默认按 `submitted_at DESC` 排序
- 留言不涉及逻辑删除
- 不返回 `ip` 字段到前端

---

### 4.18 标记已处理

```
PUT /api/v1/admin/messages/{uuid}/process
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `uuid` | `String` | 留言唯一标识 |

**请求体**（`ProcessMessageDTO`）：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `remark` | `String` | 是 | 处理备注 |

**请求示例**：

```json
{
  "remark": "已联系客户，报价已发送"
}
```

**成功响应**：

```json
{ "code": 0, "message": "处理成功", "data": null, "success": true }
```

**业务规则**：
- `processor` 从 JWT Token 中获取当前登录用户名
- `processedAt` 设置为当前时间
- 调用聚合根 `contactMessage.markProcessed(processor, remark)`
- 仅支持 `PENDING → PROCESSED` 单向流转

---

### 4.19 批量标记已处理

```
PUT /api/v1/admin/messages/process/batch
```

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `uuids` | `String[]` | 是 | 留言 UUID 数组 |
| `remark` | `String` | 否 | 批量处理备注 |

**请求示例**：

```json
{
  "uuids": ["uuid-1", "uuid-2", "uuid-3"],
  "remark": "批量处理"
}
```

**成功响应**：

```json
{ "code": 0, "message": "批量处理成功", "data": null, "success": true }
```

**业务规则**：
- 每条留言独立调用 `markProcessed()`
- 需前端二次确认弹窗
- 部分失败时返回第一个失败原因

---

### 4.20 指派留言

```
PUT /api/v1/admin/messages/{uuid}/assign
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `uuid` | `String` | 留言唯一标识 |

**请求体**（`AssignMessageDTO`）：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `staffUuid` | `String` | 是 | 员工UUID |
| `staffName` | `String` | 是 | 员工姓名 |

**请求示例**：

```json
{
  "staffUuid": "staff-uuid-001",
  "staffName": "李四"
}
```

**成功响应**：

```json
{ "code": 0, "message": "指派成功", "data": null, "success": true }
```

**业务规则**：
- 调用聚合根 `contactMessage.assign(staffUuid, staffName)`
- 状态从 `PENDING` 变为 `IN_PROGRESS`
- 仅 `PENDING` 状态可指派

---

### 4.21 分类列表（后台）

```
GET /api/v1/admin/categories
```

**请求参数（Query）**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `type` | `String` | 是 | `PRODUCT_CATEGORY` 或 `CONTENT_CATEGORY` |

**成功响应**（`Result<List<CategoryVO>>`）：同 3.5 分类列表

---

### 4.22 新增分类

```
POST /api/v1/admin/categories
```

**请求体**（`CreateCategoryDTO`）：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | `String` | 是 | 分类名称 |
| `type` | `String` | 是 | `PRODUCT_CATEGORY` / `CONTENT_CATEGORY` |
| `parentUuid` | `String` | 否 | 父分类ID |
| `sortOrder` | `int` | 否 | 排序序号，默认 0 |

**成功响应**：

```json
{ "code": 0, "message": "新增成功", "data": { ... }, "success": true }
```

**业务规则**：
- 新增后清除对应 type 的 Redis 分类缓存

---

### 4.23 编辑分类

```
PUT /api/v1/admin/categories/{uuid}
```

**路径参数**：`uuid` — 分类唯一标识

**请求参数**：同 4.21 新增分类，所有字段可选填。

**成功响应**：

```json
{ "code": 0, "message": "修改成功", "data": { ... }, "success": true }
```

**业务规则**：
- 编辑后清除对应 type 的 Redis 分类缓存

---

### 4.24 删除分类

```
DELETE /api/v1/admin/categories/{uuid}
```

**路径参数**：`uuid` — 分类唯一标识

**成功响应**：

```json
{ "code": 0, "message": "删除成功", "data": null, "success": true }
```

**业务规则**：
- 逻辑删除（`deleted = 1`）
- 删除后清除对应 type 的 Redis 分类缓存

---

### 4.25 员工列表

```
GET /api/v1/admin/staff
```

**请求参数（Query）**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `name` | `String` | 姓名模糊搜索 |
| `role` | `String` | 角色筛选 |
| `status` | `String` | 状态筛选 |
| `page` | `int` | 页码，默认 1 |
| `size` | `int` | 每页条数，默认 20 |

**成功响应**（`Result<Page<StaffVO>>`）：

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "content": [
      {
        "staffUuid": "staff-uuid-001",
        "name": "李四",
        "phone": "13900139000",
        "email": "lisi@example.com",
        "role": "FIELD_TECH",
        "status": "STANDBY",
        "createdAt": "2026-05-01 10:00:00"
      }
    ],
    "totalElements": 10,
    "totalPages": 1,
    "size": 20,
    "number": 1
  },
  "success": true
}
```

**StaffVO 字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `staffUuid` | `String` | 员工唯一标识 |
| `name` | `String` | 员工姓名 |
| `phone` | `String` | 联系电话 |
| `email` | `String` | 邮箱 |
| `role` | `String` | 角色：`FIELD_TECH` / `CUSTOMER_SERVICE` / `TECH_SUPPORT` / `AFTER_SALES` |
| `status` | `String` | 状态：`VACATION` / `STANDBY` / `WORKING` / `BUSINESS_TRIP` |

---

### 4.26 员工详情

```
GET /api/v1/admin/staff/{uuid}
```

**路径参数**：`uuid` — 员工唯一标识

**成功响应**（`Result<StaffVO>`）

---

### 4.27 新增员工

```
POST /api/v1/admin/staff
```

**请求体**（`CreateStaffDTO`）：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | `String` | 是 | 员工姓名 |
| `phone` | `String` | 是 | 联系电话 |
| `email` | `String` | 否 | 邮箱 |
| `role` | `String` | 是 | 角色枚举 |
| `status` | `String` | 否 | 状态，默认 `STANDBY` |

---

### 4.28 编辑员工

```
PUT /api/v1/admin/staff/{uuid}
```

**路径参数**：`uuid` — 员工唯一标识

**请求参数**：同 4.27 新增员工，所有字段可选填。

---

### 4.29 删除员工

```
DELETE /api/v1/admin/staff/{uuid}
```

**路径参数**：`uuid` — 员工唯一标识

**业务规则**：
- 逻辑删除（`deleted = 1`）

---

### 4.30 设备列表

```
GET /api/v1/admin/devices
```

**请求参数（Query）**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `customerUuid` | `String` | 客户UUID筛选 |
| `model` | `String` | 设备型号筛选 |
| `gasType` | `String` | 气体类型筛选：CH4/H2S/CO/NH3/O2/OTHER |
| `status` | `String` | 状态筛选：NORMAL/ABNORMAL/OFFLINE/MAINTENANCE |
| `page` | `int` | 页码，默认 1 |
| `size` | `int` | 每页条数，默认 20 |

**成功响应**（`Result<Page<DeviceVO>>`）：

```json
{
  "code": 0,
  "data": {
    "content": [
      {
        "deviceUuid": "uuid-xxx",
        "serialNumber": "DEMO-001",
        "name": "甲烷检测器A1",
        "model": "GT-A100",
        "customerUuid": "customer-xxx",
        "installLocation": "厂区1号车间",
        "gasType": "CH4",
        "rangeMin": "0",
        "rangeMax": "100",
        "alertThreshold": "25",
        "status": "NORMAL",
        "createdAt": "2026-05-20 10:00:00"
      }
    ],
    "totalElements": 8,
    "totalPages": 1
  }
}
```

**DeviceVO 字段**：`deviceUuid`, `serialNumber`, `name`, `model`, `customerUuid`, `installLocation`, `installDate`, `gasType`, `rangeMin`, `rangeMax`, `alertThreshold`, `status`, `createdAt`

---

### 4.31 设备详情

```
GET /api/v1/admin/devices/{uuid}
```

**成功响应**（`Result<DeviceVO>`）：同 4.30 单个设备对象

---

### 4.32 新增设备

```
POST /api/v1/admin/devices
```

**请求体**（`CreateDeviceDTO`）：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `serialNumber` | `String` | 是 | 序列号，全局唯一 |
| `name` | `String` | 是 | 设备名称 |
| `model` | `String` | 是 | 设备型号 |
| `customerUuid` | `String` | 是 | 客户UUID |
| `gasType` | `String` | 是 | CH4/H2S/CO/NH3/O2/OTHER |
| `installLocation` | `String` | 否 | 安装位置 |
| `rangeMin` | `BigDecimal` | 否 | 量程下限 |
| `rangeMax` | `BigDecimal` | 否 | 量程上限 |
| `alertThreshold` | `BigDecimal` | 否 | 报警阈值 |

**成功响应**：`Result<DeviceVO>`

---

### 4.33 编辑设备

```
PUT /api/v1/admin/devices/{uuid}
```

**请求体**（`UpdateDeviceDTO`）：所有字段可选，同 CreateDeviceDTO

---

### 4.34 删除设备

```
DELETE /api/v1/admin/devices/{uuid}
```

- 逻辑删除（`deleted = 1`），需二次确认

---

### 4.35 设备状态操作

```
POST /api/v1/admin/devices/{uuid}/mark-abnormal
POST /api/v1/admin/devices/{uuid}/mark-normal
POST /api/v1/admin/devices/{uuid}/mark-offline
POST /api/v1/admin/devices/{uuid}/start-maintenance
POST /api/v1/admin/devices/{uuid}/end-maintenance
```

**状态流转规则**：
- `mark-abnormal`：NORMAL → ABNORMAL
- `mark-normal`：ABNORMAL → NORMAL
- `mark-offline`：NORMAL → OFFLINE
- `start-maintenance`：任意状态 → MAINTENANCE
- `end-maintenance`：MAINTENANCE → NORMAL

所有操作需二次确认，请求体为空。

---

### 4.36 设备历史数据

```
GET /api/v1/admin/devices/{uuid}/data
```

**请求参数**：`from`（起始时间）、`to`（结束时间）

**成功响应**（`Result<List<DeviceDataPointVO>>`）：

```json
{
  "code": 0,
  "data": [
    {
      "deviceUuid": "uuid-xxx",
      "timestamp": "2026-05-27 14:30:00",
      "concentration": "12.5",
      "battery": "85.0",
      "temperature": "23.5",
      "humidity": "45.0",
      "signalStrength": 92,
      "createdAt": "2026-05-27 14:30:00"
    }
  ]
}
```

---

### 4.37 设备最新数据

```
GET /api/v1/admin/devices/{uuid}/latest
```

**成功响应**（`Result<DeviceDataPointVO>`）：返回该设备最近一条数据点

---

### 4.38 报警规则列表

```
GET /api/v1/admin/alert-rules
```

**成功响应**（`Result<List<AlertRuleVO>>`）：

```json
{
  "code": 0,
  "data": [
    {
      "ruleUuid": "uuid-xxx",
      "name": "甲烷浓度过高",
      "deviceUuid": null,
      "ruleType": "THRESHOLD",
      "gasType": "CH4",
      "threshold": "25.0000",
      "durationSeconds": 60,
      "severity": "CRITICAL",
      "autoCreateWorkOrder": true,
      "enabled": true,
      "createdAt": "2026-05-20 10:00:00"
    }
  ]
}
```

**AlertRuleVO 字段**：`ruleUuid`, `name`, `deviceUuid`, `ruleType`, `gasType`, `threshold`, `durationSeconds`, `severity`, `autoCreateWorkOrder`, `enabled`, `createdAt`

---

### 4.39 报警规则详情

```
GET /api/v1/admin/alert-rules/{uuid}
```

**成功响应**（`Result<AlertRuleVO>`）

---

### 4.40 新增报警规则

```
POST /api/v1/admin/alert-rules
```

**请求体**（`CreateAlertRuleDTO`）：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | `String` | 是 | 规则名称 |
| `deviceUuid` | `String` | 否 | 为空=全局规则 |
| `ruleType` | `String` | 是 | THRESHOLD/OFFLINE/LOW_BATTERY |
| `gasType` | `String` | 否 | 仅阈值规则需要 |
| `threshold` | `BigDecimal` | 否 | 仅阈值规则需要 |
| `durationSeconds` | `int` | 是 | 默认 60，最小 10 |
| `severity` | `String` | 是 | CRITICAL/WARNING/INFO |
| `autoCreateWorkOrder` | `boolean` | 否 | 默认 false |

---

### 4.41 编辑报警规则

```
PUT /api/v1/admin/alert-rules/{uuid}
```

**请求体**（`UpdateAlertRuleDTO`）：所有字段可选

---

### 4.42 删除报警规则

```
DELETE /api/v1/admin/alert-rules/{uuid}
```

- 逻辑删除，需二次确认

---

### 4.43 启用/禁用规则

```
POST /api/v1/admin/alert-rules/{uuid}/enable
POST /api/v1/admin/alert-rules/{uuid}/disable
```

请求体为空。

---

### 4.44 报警记录列表

```
GET /api/v1/admin/alerts
```

**请求参数（Query）**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `deviceUuid` | `String` | 设备UUID筛选 |
| `alertType` | `String` | 报警类型筛选 |
| `severity` | `String` | 严重级别筛选 |
| `status` | `String` | 状态筛选：PENDING/CONFIRMED/RESOLVED/CLOSED |
| `page` | `int` | 页码，默认 1 |
| `size` | `int` | 每页条数，默认 20 |

**成功响应**（`Result<Page<AlertVO>>`）：

```json
{
  "code": 0,
  "data": {
    "content": [
      {
        "alertUuid": "uuid-xxx",
        "deviceUuid": "device-xxx",
        "ruleUuid": "rule-xxx",
        "alertType": "THRESHOLD",
        "severity": "CRITICAL",
        "concentration": "28.5000",
        "threshold": "25.0000",
        "message": "甲烷浓度超标: 28.5 > 25.0",
        "status": "PENDING",
        "triggeredAt": "2026-05-27 14:30:00",
        "confirmedAt": null,
        "resolvedAt": null
      }
    ],
    "totalElements": 6
  }
}
```

---

### 4.45 报警详情

```
GET /api/v1/admin/alerts/{uuid}
```

**成功响应**（`Result<AlertVO>`）：包含完整报警信息（含 confirmedBy、resolvedBy、workOrderUuid）

---

### 4.46 确认报警

```
POST /api/v1/admin/alerts/{uuid}/confirm
```

**请求参数**：`confirmedBy`（Query，确认人用户名）

- 状态：PENDING → CONFIRMED

---

### 4.47 解决报警

```
POST /api/v1/admin/alerts/{uuid}/resolve
```

**请求参数**：`resolvedBy`（Query，解决人用户名）

- 状态：CONFIRMED → RESOLVED

---

### 4.48 关闭报警

```
POST /api/v1/admin/alerts/{uuid}/close
```

- 任何非 CLOSED 状态可直接关闭

---

### 4.49 报警通知记录

```
GET /api/v1/admin/alerts/{uuid}/notifications
```

**请求参数**：`page`、`size`

**成功响应**（`Result<Page<NotificationVO>>`）：

```json
{
  "code": 0,
  "data": {
    "content": [
      {
        "notificationUuid": "uuid-xxx",
        "alertUuid": "alert-xxx",
        "recipient": null,
        "channel": "IN_APP",
        "content": "设备 甲烷检测器A1 触发 CRITICAL 报警：甲烷浓度超标",
        "status": "PENDING",
        "retryCount": 0,
        "errorMessage": null,
        "sentAt": null,
        "createdAt": "2026-05-27 14:30:01"
      }
    ]
  }
}
```

---

### 4.50 大屏统计数据

```
GET /api/v1/admin/dashboard/stats
```

**成功响应**（`Result<DashboardStatsVO>`）：

```json
{
  "code": 0,
  "data": {
    "totalDevices": 8,
    "normalDevices": 5,
    "abnormalDevices": 1,
    "offlineDevices": 1,
    "maintenanceDevices": 1,
    "totalAlerts": 6,
    "alertsToday": 2,
    "pendingAlerts": 3,
    "criticalAlerts": 1,
    "warningAlerts": 3,
    "alertTrend": [
      { "date": "2026-05-21", "count": 1 },
      { "date": "2026-05-22", "count": 0 }
    ]
  }
}
```

**业务规则**：
- Redis 缓存 5 分钟
- `alertTrend` 返回最近 7 天每日报警数

---

### 4.51 设备数据上报

```
POST /api/v1/public/device-data
```

> **无需认证**，供设备模拟器/真实设备调用。

**请求体**（`DeviceDataPointDTO`）：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `deviceUuid` | `String` | 是 | 设备UUID |
| `timestamp` | `String` | 是 | 数据时间，`YYYY-MM-DD HH:mm:ss` |
| `concentration` | `BigDecimal` | 是 | 气体浓度值 |
| `battery` | `BigDecimal` | 否 | 电池电量 |
| `temperature` | `BigDecimal` | 否 | 温度（°C） |
| `humidity` | `BigDecimal` | 否 | 湿度（%） |
| `signalStrength` | `int` | 否 | 信号强度（0-100） |

**请求示例**：

```json
{
  "deviceUuid": "device-uuid-001",
  "timestamp": "2026-05-27 14:30:00",
  "concentration": "12.5",
  "battery": "85.0",
  "temperature": "23.5",
  "humidity": "45.0",
  "signalStrength": 92
}
```

**业务规则**：
- 数据保存后自动触发报警引擎评估
- 报警引擎加载匹配规则 → 滑动窗口判断 → 去重抑制 → 创建报警 → 发布事件
- 返回成功仅代表数据已接收，不反映报警触发结果（异步处理）

---

## 五、全局异常处理

### 5.1 Controller 层约束

- Controller 层**禁止 try-catch**
- 所有异常由全局 `@RestControllerAdvice` 统一处理
- 异常统一转换为 `Result<T>` 返回，`code` 从 `ErrorCode` 枚举获取

### 5.2 异常 → 响应映射

| 异常类 | code | HTTP 状态码 |
|--------|------|------------|
| `ProductNotFoundException` | `4001` | 404 |
| `ContentNotFoundException` | `4002` | 404 |
| `CategoryNotFoundException` | `4003` | 404 |
| `MessageNotFoundException` | `4004` | 404 |
| `UserNotFoundException` | `4005` | 404 |
| `AccountLockedException` | `4006` | 403 |
| `InvalidPasswordException` | `4007` | 401 |
| `UnauthorizedException` | `4008` | 401 |
| `ForbiddenException` | `4009` | 403 |
| `MethodArgumentNotValidException` | `4000` | 400 |
| 其他 `BusinessException` | 对应 ErrorCode | 400 |
| `Exception`（未预期） | `5001` | 500 |

### 5.3 参数校验失败响应示例

```json
{
  "code": 4000,
  "message": "name: 产品名称不能为空; phone: 手机号格式不正确",
  "data": null,
  "success": false
}
```

> 校验失败的 `message` 汇总所有字段错误，以分号分隔，前端可解析后标红对应字段。

---

## 六、Controller 层代码骨架

> 当前实现采用按领域拆分的多 Controller 结构（非单 PublicController + AdminController）。

### 6.1 前台 Public Controller（ProductController / ContentController / CategoryController / MessageController / RegisterController / AIChatController）

```java
// ProductController
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @GetMapping("/products")
    public Result<Page<ProductVO>> getProducts(
            @RequestParam(required = false) String categoryUuid,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) { ... }
    @GetMapping("/products/{uuid}")
    public Result<ProductDetailVO> getProduct(@PathVariable String uuid) { ... }
}

// RegisterController
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class RegisterController {
    private final AuthService authService;
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) { ... }
}

// AIChatController
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class AIChatController {
    private final AIChatService aiChatService;
    @PostMapping("/ai/chat")
    public Result<ChatResponseVO> chat(
            @Valid @RequestBody SendMessageDTO dto,
            HttpServletRequest request) { ... }
}
```

### 6.2 后台 Admin Controller（AuthController / AdminProductController / AdminContentController / AdminCategoryController / AdminMessageController / AdminStaffController）

```java
// AuthController
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public Result<LoginResultVO> login(@Valid @RequestBody LoginDTO dto) { ... }
    @GetMapping("/captcha")
    public Result<CaptchaVO> captcha() { ... }
    @GetMapping("/currentUser")
    public Result<UserVO> currentUser(@RequestAttribute String userUuid) { ... }
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authHeader) { ... }
    @PostMapping("/resetPassword")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) { ... }
}

// AdminCategoryController — 后台分类 CRUD
// AdminMessageController — 含 assign 端点
// AdminStaffController — 员工 CRUD
// AdminProductController / AdminContentController — 产品/内容管理
```

---

## 七、版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| V1.0 | 2026-05-08 | 初始版本，基于架构设计文档 V1.4、类图设计文档最终版、接口规则 Skill 综合整理，补全全部接口定义 |
| V1.1 | 2026-05-21 | 对齐当前代码：新增 AI 对话/注册/验证码/登出/重置密码/留言指派/后台分类CRUD/员工CRUD 共 16 个端点；LoginDTO 增加 captcha 字段；MessageVO 增加 IN_PROGRESS + assignedStaff 字段；Controller 骨架改为多 Controller 拆分 |
