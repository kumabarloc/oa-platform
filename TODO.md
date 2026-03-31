# OA 平台 - 待办事项

> 最后更新：2026-03-31

## 当前优先级
完善用户管理和系统管理功能

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
cd /Users/basara/openclaw-projects/oa-platform/backend/oa-platform-admin
java -jar target/oa-platform-admin.jar

# 3. 启动前端
cd /Users/basara/openclaw-projects/oa-platform/frontend
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

### 角色管理模块（当前任务）
- [✅] 后端：角色管理 API（已有完整的 CRUD + 分配权限）
- [✅] 前端：修复角色管理页面（修正 API 路径、添加菜单树加载）

### 中优先级
- [ ] 用户管理模块（CRUD）
- [ ] 部门管理模块（树形 CRUD）
- [ ] 菜单管理模块
- [ ] 发文流程设计（Flowable BPMN）
- [ ] 收文流程设计（Flowable BPMN）

### 低优先级
- [ ] 通知系统
- [ ] 日志管理
- [ ] 附件上传
- [ ] 前端 UI 优化
- [ ] Docker 部署配置