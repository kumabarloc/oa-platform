# OA 平台 - 待办事项

> 最后更新：2026-03-31

## 当前优先级
完善用户管理和系统管理功能

## 🔴 已修复问题
1. **登录页面无法显示** - 前端验证码/登录页问题 ✅
2. **登录后不跳转** - 前端 store 解析响应数据结构错误 ✅
3. **登录超时（401错误）** - 登录时未查询用户角色信息 ✅

### 修复详情
1. **登录后不跳转问题**（2026-03-31）
   - 原因：前端 Pinia store 解析响应数据结构错误。后端返回 `{ code: 200, data: { token, userInfo } }`，但前端直接读取 `res.token`
   - 解决：修改 `src/stores/auth.js` 中的 login 方法，改为读取 `res.data?.token`
   
2. **默认密码问题**
   - 数据库中 admin 用户的密码是 `123456`（不是之前试的 admin123）

3. **登录超时/权限问题**（2026-03-31）
   - 现象：登录成功后点"用户管理"显示"登录超时"
   - 原因：登录时未查询用户角色信息，导致 roleKeys 为 null，JWT Filter 无法设置权限
   - 解决：
     - 修改 `AuthService.java` - 登录时查询用户角色并缓存到 Redis
     - 修改 `SysUserRoleMapper.java` - 新增 selectRolesByUserId 方法
     - 修改 `SysUserRoleMapper.xml` - 添加查询角色 SQL
     - 升级 Lombok 到 1.18.38 解决编译问题

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

### 中优先级
- [ ] 用户管理模块（CRUD）
- [ ] 角色管理模块（CRUD）
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