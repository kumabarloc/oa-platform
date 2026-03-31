# OA 平台 - 待办事项

> 最后更新：2026-03-31

## 当前优先级
排查登录页面无法显示的问题

## 🔴 待解决问题
- [ ] 登录页面无法显示（之前有验证码问题，现在页面刷不出来）
  - 问题描述：前端服务已启动（https://localhost:5174），后端服务已启动（http://localhost:8080）
  - 验证码 API 测试结果：✅ 正常返回 PNG 图片
  - 状态：待用户在浏览器测试确认
  - 相关模块：前端 Vue3 + Element Plus，后端 Spring Boot

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