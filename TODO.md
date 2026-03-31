# OA 平台 - 待办事项

> 最后更新：2026-03-31

## 当前优先级
排查登录页面无法显示的问题

## 🔴 待解决问题
- [x] 登录页面无法显示（之前有验证码问题，现在页面刷不出来）
  - 问题描述：前端服务已启动（https://localhost:5174），后端服务已启动（http://localhost:8080）
  - 验证码 API 测试结果：✅ 正常返回 PNG 图片
  - 状态：已修复

### 修复记录
1. **登录后不跳转问题**（2026-03-31）
   - 原因：前端 Pinia store 解析响应数据结构错误。后端返回 `{ code: 200, data: { token, userInfo } }`，但前端直接读取 `res.token`
   - 解决：修改 `src/stores/auth.js` 中的 login 方法，改为读取 `res.data?.token`
   
2. **默认密码问题**
   - 数据库中 admin 用户的密码是 `123456`（不是之前试的 admin123）

## 高优先级
- [✅] 确定技术栈（Vue3 + Element Plus, Spring Boot, MySQL, Flowable）
- [✅] 设计数据库表结构（见 DB_DESIGN.md）
- [✅] 搭建后端项目基础框架（Spring Boot + Maven 多模块 + Flowable）
- [✅] 搭建前端项目基础框架（Vue 3 + Vite）
- [ ] 测试登录功能
- [ ] 实现验证码功能

## 中优先级
- [ ] 用户认证模块（登录、JWT、验证码）
- [ ] 系统管理模块（用户、部门、岗位、角色、菜单）
- [ ] 发文流程设计（Flowable BPMN）
- [ ] 收文流程设计（Flowable BPMN）

## 低优先级
- [ ] 通知系统
- [ ] 日志管理
- [ ] 附件上传
- [ ] 前端 UI 优化
- [ ] Docker 部署配置（docker-compose.yml）