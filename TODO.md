# OA 平台 - 待办事项

> 最后更新：2026-04-02

## 当前优先级
通知管理、收文管理、日志管理模块开发

## 🔴 已修复问题
1. **登录页面无法显示** - 前端验证码/登录页问题 ✅
2. **登录后不跳转** - 前端 store 解析响应数据结构错误 ✅
3. **登录超时（401错误）** - 登录时未查询用户角色信息 ✅

### 角色权限设计（2026-03-31 确认）

**预设角色：**
| 角色名 | 角色标识 | 说明 |
|--------|----------|------|
| 超级管理员 | super_admin | 系统最高权限 |
| 系统管理员 | admin | 系统管理权限 |
| 室主任 | director | 审核权限 |
| 副主任 | deputy | 核稿权限 |
| 普通员工 | staff | 基本权限 |

**权限层级：**
- super_admin: 全部权限
- admin: 系统管理（用户、角色、部门、岗位、菜单、字典、日志）
- director: 公文审核
- deputy: 公文核稿
- staff: 公文拟稿、查看

### 启动命令（重要！）
```bash
# 1. 启动 Docker 数据库
docker start oa-mysql-dev oa-redis-dev

# 2. 启动后端
cd backend/oa-platform-admin
mvn clean package -DskipTests
java -jar target/oa-platform-admin.jar

# 3. 启动前端
cd frontend
npm run dev
```

### 访问地址
- 前端：https://localhost:5175（端口可能变化，看终端输出）
- 后端 API：http://localhost:8080
- 登录账号：admin / 123456

## 开发进度

### 高优先级
- [✅] 确定技术栈（Vue3 + Element Plus, Spring Boot, MySQL, Flowable）
- [✅] 设计数据库表结构（见 DB_DESIGN.md）
- [✅] 搭建后端项目基础框架（Spring Boot + Maven 多模块 + Flowable）
- [✅] 搭建前端项目基础框架（Vue 3 + Vite）
- [✅] 测试登录功能
- [✅] 实现验证码功能
- [✅] 修复登录后权限问题
- [✅] 确认角色权限设计
- [✅] 用户管理模块（CRUD + 详情 + 密码重置） 2026-04-02
- [✅] 通知管理模块（CRUD + 已读未读） 2026-04-02
- [✅] 收文管理模块 2026-04-02
- [✅] 登录日志与操作日志模块 2026-04-02

### 中优先级
- [✅] 部门管理模块（树形 CRUD）
- [✅] 菜单管理模块
- [✅] 发文流程设计（Flowable BPMN）

### 低优先级
- [✅] 附件上传
- [✅] 前端 UI 优化
- [ ] Docker 部署配置

## 2026-04-02 完成模块

### 用户管理模块
- [✅] 数据库 sys_user 表已有 phone、avatar 字段
- [✅] 后端：GET /api/system/user/info（当前用户信息）
- [✅] 后端：PUT /api/system/user/password（修改当前用户密码）
- [✅] 前端：api/system/user.js 修正 resetPassword 接口路径
- [✅] 前端：用户列表完善（头像列、昵称、完整表单弹窗）

### 通知管理模块
- [✅] 数据库：sys_notification 表（含 type、target_type、read_status）
- [✅] 数据库：sys_notification_read 表（阅读记录）
- [✅] 后端：NotificationController（/api/system/notification）
- [✅] 后端：NotificationService（CRUD + 已读/未读）
- [✅] 前端：api/system/notification.js
- [✅] 前端：views/system/notification/index.vue
- [✅] 路由注册 + 左侧菜单入口

### 收文管理模块
- [✅] 数据库：sys_incoming_document 表
- [✅] 后端：IncomingDocumentController（/api/workflow/incoming）
- [✅] 后端：IncomingDocumentService
- [✅] 前端：api/workflow/incoming.js
- [✅] 前端：views/workflow/IncomingList.vue、IncomingDetail.vue
- [✅] 路由注册 + 左侧菜单入口

### 日志管理模块
- [✅] 数据库：sys_login_log 表
- [✅] 数据库：sys_operation_log 表
- [✅] 后端：LoginLogController（/api/system/login-log）
- [✅] 后端：OperationLogController（/api/system/operation-log）
- [✅] 前端：api/system/log.js
- [✅] 前端：views/system/log/LoginLog.vue、OperationLog.vue
- [✅] 路由注册 + 左侧菜单入口

## 待完善事项

### 通知管理
- [ ] 通知推送（根据 target_type 推送给指定用户/部门/全员）
- [ ] 通知摘要/未读数角标

### 收文管理
- [ ] Flowable 流程集成（分发/归档关联审批流程）
- [ ] 附件上传功能

### 日志管理
- [ ] AOP 操作日志切面（@Log 注解）
- [ ] 日志导出 Excel

### 部署
- [ ] Docker Compose 生产环境配置
- [ ] Nginx 前端部署配置
- [ ] 环境变量配置文档
