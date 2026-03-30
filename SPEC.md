# OA 平台 - 项目规范文档

> 最后更新：2026-03-30

## 项目概述
- 项目名称：OA 平台
- 类型：前后端分离办公自动化系统
- 目标用户：公司内部员工、管理层

## 系统环境要求

### 运行环境
| 组件 | 版本 | 说明 | 状态 |
|------|------|------|------|
| JDK | 21+ | Spring Boot 3.x 必需，至少 JDK 21 | ⚠️ 注意：需配置 `JAVA_HOME` 指向 JDK 21，Maven 可能会错误引用更高版本 JDK |
| Node.js | 18+ | 前端构建 | ✅ |
| npm | 9+ | 前端依赖管理 | ✅ |
| Maven | 3.9+ | 后端构建 | ✅ |
| MySQL | 8.0+ | 数据库 | ✅ |
| Docker | 24+ | 容器化部署（可选） | ✅ |

### 本地开发端口规划
| 端口 | 服务 | 说明 |
|------|------|------|
| 3306 | MySQL | 数据库 |
| 6379 | Redis | 缓存（可选） |
| 8080 | Spring Boot | 后端 API |
| 5173 | Vite Dev Server | 前端开发服务器 |
| 8081 | Flowable | 流程引擎（可选） |

### 环境变量说明
- `JAVA_HOME`：**必须指向 JDK 21**，否则 Lombok 编译会失败
- `DATABASE_HOST`：MySQL 主机（默认 localhost:3306）
- `DATABASE_NAME`：数据库名（默认 oa_platform）
- `DATABASE_USER`：数据库用户名（默认 root）
- `DATABASE_PASSWORD`：数据库密码（默认 123456）

## 技术栈（已确定）

| 层级 | 技术选型 | 版本 | 状态 |
|------|---------|------|------|
| 前端框架 | Vue 3 + Element Plus | Vue 3.5 / Element Plus 2.13 | ✅ |
| 前端构建 | Vite | 8.0 | ✅ |
| 前端路由 | Vue Router | 5.0 | ✅ |
| 前端状态 | Pinia | 3.0 | ✅ |
| HTTP 客户端 | Axios | 1.14 | ✅ |
| 后端框架 | Spring Boot | 3.x | ✅ |
| Java 版本 | JDK | 21 | ✅ |
| ORM | MyBatis-Plus | 3.5 | ✅ |
| 数据库 | MySQL | 8.0 | ✅ |
| 迁移工具 | Flyway | 最新 | ✅ |
| 安全框架 | Spring Security + JWT | - | ✅ |
| 流程引擎 | Flowable | 最新 | ✅ |
| 构建工具 | Maven | 3.9 | ✅ |
| 项目管理 | Maven 多模块 | - | ✅ |

## 核心功能模块

| 模块 | 说明 | 状态 |
|------|------|------|
| 用户认证 | 登录、JWT、验证码 | ✅ 后端完成 |
| 权限管理 | 角色、菜单、数据权限 | 🔲 开发中 |
| 系统管理 | 用户、部门、岗位、角色、菜单 CRUD | 🔲 开发中 |
| 工作流审批 | 发文流程、收文流程（Flowable） | ⏳ 待开发 |
| 通知系统 | 站内信、已读未读 | ⏳ 待开发 |
| 日志管理 | 操作日志、登录日志 | ⏳ 待开发 |
| 公文管理 | 发文、收文、附件 | ⏳ 待开发 |

## 项目结构

```
oa-platform/
├── backend/                    # 后端 Maven 多模块项目
│   ├── oa-platform-admin/      # 启动模块（包含 Controller、Service）
│   ├── oa-platform-system/     # 系统模块（Entity）
│   ├── oa-platform-framework/  # 框架模块（配置、安全）
│   ├── oa-platform-common/     # 通用模块（工具类）
│   ├── oa-platform-workflow/   # Flowable 工作流模块
│   └── pom.xml                 # 父 POM
├── frontend/                   # 前端 Vue 3 项目
│   ├── src/
│   │   ├── api/                # API 接口封装
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia 状态管理
│   │   ├── views/              # 页面组件
│   │   └── utils/              # 工具函数
│   └── package.json
├── docker-compose.yml           # Docker Compose 配置
└── *.md                        # 项目文档
```

## 约束与备注

- [x] 单公司架构，不考虑多租户
- [x] 用户规模：设计容量 400 人，实际用户 20 人左右
- [x] 使用 Docker 部署时，确保 `JAVA_HOME` 指向正确版本