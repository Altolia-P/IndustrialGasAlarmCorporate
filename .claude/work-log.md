# 工作日志

> 每次工作简要记录时间、内容和进度。

---

## 2026-05-09

| 时间 | 内容 | 状态 |
|------|------|------|
| — | 创建工作日志文件 | ✅ |
| 14:30 | 生成 Git Pull 指南（个人版 + Claude版） | ✅ |
| — | 约定：每次 pull 前先问我，我来判断状态和风险 | ✅ |

| 12:13 | 盘点并标记所有 Claude 日志文件 | ✅ |
| — | 整理全项目 Skills&Rules，按工具(Claude/Trae/RooCode)分类输出到 `D:\keysAndTools\Tools\` | ✅ |
| 12:34 | 从数据库设计文档提取完整建表SQL | ✅ |
| — | 创建 README.md 项目说明文档 | ✅ |
| — | 初始化 Git 仓库并推送到 Gitee | ✅ |
| — | 添加 GitHub 远程仓库并推送成功 | ✅ |

---

## 2026-05-10

| 时间 | 内容 | 状态 |
|------|------|------|
| 20:00 | 后端骨架全部创建完成（DDD四层架构，111个Java源文件） | ✅ |
| — | Common层：Result\<T\>、Page\<T\>、ErrorCode枚举、9个业务异常类、JwtUtil | ✅ |
| — | Domain层：5个聚合根（Product/Content/Category/ContactMessage/User）、枚举、值对象、领域事件、Repository接口 | ✅ |
| — | Application层：16个DTO/VO、5个Service接口+实现（Product/Content/Category/Message/Auth） | ✅ |
| — | Infrastructure层：7个PO+Mapper、5个RepositoryImpl、3个Redis Repository、Spring Security+JWT配置 | ✅ |
| — | Interface层：8个Controller（public 4个 + admin 4个）、GlobalExceptionHandler | ✅ |
| 20:15 | 修复MyBatis-Plus与Spring Boot 3.5.14不兼容（factoryBeanObjectType类型错误） | ✅ |
| — | 升级 mybatis-plus-boot-starter:3.5.7 → mybatis-plus-spring-boot3-starter:3.5.16 | ✅ |
| 20:16 | 修复测试无法启动（缺少MySQL/Redis运行环境） | ✅ |
| — | 测试环境改用H2内存数据库 + 禁用Redis自动配置 + Mock Redis Repository Bean | ✅ |
| 21:16 | 最终验证：111个源文件编译通过，1个测试通过，BUILD SUCCESS | ✅ |
| 21:23 | 修复 PaginationInnerInterceptor 编译错误：MyBatis-Plus 3.5.9+ 分页插件拆分至 mybatis-plus-jsqlparser 独立模块 | ✅ |
| — | 新增 mybatis-plus-jsqlparser:3.5.16 依赖，应用成功启动（Tomcat 8080） | ✅ |
| 21:24 | IntelliJ IDEA Database 面板提示"未配置数据库驱动程序"：IDE 从 JetBrains CDN 下载驱动失败，手动指定本地 Maven 仓库 mysql-connector-j-8.0.33.jar | ✅ |
| 21:26 | Gas-ToB-SQL.sql 追加演示账号：caseUser/EDITOR (case123)、caseAdmin/ADMIN (admin456)，BCrypt 密码哈希通过 Spring Security 生成 | ✅ |
| — | 追加第13节（INFORMATION_SCHEMA 查所有表）、第14节（10张表 SELECT * 查询） | ✅ |
| 21:27 | 修正 SQL 中 UUID 格式：REPLACE(UUID(),'-','') → UUID()，与 Java 端 UUID.randomUUID().toString() 保持一致的 36 字符标准格式 | ✅ |
| 21:30 | 代码与设计文档颗粒度对齐——逐文件对比类图/API/DB 5份设计文档 vs 108个源文件 | ✅ |
| — | Bug修复：Assembler未填充createdAt/categoryName（Product/Content领域对象+RepositoryImpl+Assembler三层联动） | ✅ |
| — | Bug修复：域对象publish/unpublish抛IllegalStateException→改为ProductCannotPublishException/BusinessException | ✅ |
| — | 补全：ProductCannotPublishException异常类、3个Listener接口、BaseEntity基类 | ✅ |
| 21:47 | 最终验证：115个源文件编译通过，1个测试通过，BUILD SUCCESS | ✅ |
| 22:30 | 前后端全面对齐审计：前端54个源文件 + 后端115个源文件，逐API/类型/字段交叉对比 | ✅ |
| — | 审计结论：25个前端API调用 vs 22个后端端点（3个前端多余：register/captcha/captcha-login） | ✅ |
| — | 发现Content-Type不匹配：Product/Content create/update 前端发FormData，后端期望JSON | ✅ |
| — | DTO/VO字段名全匹配，TypeScript类型与Java类型一致 | ✅ |
| 22:50 | 后端对齐：新增 FileStorageService（文件存储+类型/大小校验） | ✅ |
| — | AdminProductController create/update 改用 @RequestParam + MultipartFile（multipart支持） | ✅ |
| — | AdminContentController create/update 改用 @RequestParam + MultipartFile（multipart支持） | ✅ |
| — | WebConfig 添加 /uploads/** 静态资源映射，application.yml 添加 upload.path 配置 | ✅ |
| — | 编译通过（116个源文件），测试通过（1/1），BUILD SUCCESS | ✅ |

---

## 2026-05-11

| 时间 | 内容 | 状态 |
|------|------|------|
| 08:03 | Context restore: Volar false error fix（TypeScript 5.8.3→5.6.3 降级修复） | ✅ |
| 14:10 | 登录页API+数据库连接全面审计：前端use-login.ts + 后端AuthController/AuthService逐行交叉对比 | ✅ |
| — | Bug修复：后端缺失 /admin/captcha 端点（前端use-login.ts在4006/4007后调用） | ✅ |
| — | Bug修复：后端缺失 /public/register 端点（前端register.vue调用） | ✅ |
| — | Bug修复：LoginDTO 缺失 captcha/captchaToken 字段 | ✅ |
| — | DDD违规修复：User域对象直接调用BCrypt→抽取PasswordHasher接口+BCryptPasswordHasher | ✅ |
| — | 新增文件：CaptchaVO、RegisterDTO、CaptchaGenerator、CaptchaRepository、PasswordHasher、BCryptPasswordHasher、DataInitializer、schema.sql | ✅ |
| — | 前端修复：loginWithCaptcha失败后自动刷新验证码图片 | ✅ |
| 14:30 | 代码与架构文档颗粒度对齐 | ✅ |
| — | AuthController拆分：混合admin+public→拆为AuthController+RegisterController | ✅ |
| — | 添加UserAssembler.toVO()，消除AuthServiceImpl.getCurrentUser()手工创建VO | ✅ |
| — | java-coding-style.md修正：接口I前缀→无前缀；SystemException→BusinessException | ✅ |
| 14:50 | Bug修复：MySQL Connector/J 9.x 不支持 characterEncoding=utf8mb4→改为 UTF-8 | ✅ |
| — | sql.init.mode: always→never，DataInitializer改为嵌入式DDL建表+种子数据 | ✅ |
| 14:57 | Bug修复：Access denied for user 'root'@'localhost' (using password: NO) — DB_PASSWORD环境变量未设置 | ✅ |
| — | 新建 application-local.yml（gitignored）存放本地MySQL/Redis/JWT密钥 | ✅ |
| — | 新建 application-local.example.yml 作为配置模板（已提交） | ✅ |
| — | spring.profiles.active 设为 local（本地开发默认激活） | ✅ |
| 15:13 | 用户填写MySQL密码，创建正式的 application-local.yml | ✅ |
| 15:35 | 后端启动成功：Started IndustrialGasAlarmCorporateApplication in 2.904 seconds on port 8080 | ✅ |
| — | 端口8080残留进程清理（taskkill） | ✅ |
| 16:00 | 前端 token 有效性校验：页面打开时主动验证登录态 | ✅ |
| — | stores/auth.ts：新增 tokenVerified 状态 + verifyToken() 异步方法（调 GET /admin/currentUser） | ✅ |
| — | router/index.ts：beforeEach 改为 async，访问受保护页面前先调 verifyToken() 校验 | ✅ |
| — | 过期 token 恢复处理：校验失败→自动 logout()→清 token→跳登录页，不再闪白屏 | ✅ |
| — | CONTACTOR 用户登录后跳 UserCenter（非 Home），已登录访问 /login 自动跳对应后台 | ✅ |
| — | tokenVerified 缓存：同会话内重复导航不重复调 API | ✅ |
| — | vue-tsc --noEmit 零错误通过 | ✅ |
| 23:51 | /context-save：保存工作上下文 checkpoint（token-validation-and-startup-fixes） | ✅ |

---

