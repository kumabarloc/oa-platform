# OA 平台 - 技术架构文档

> 最后更新：2026-03-30

## 系统架构
- **前端：** Vue 3 + Element Plus（SPA）
- **后端：** Spring Boot 3.x + JDK 21
- **数据库：** MySQL 8.x
- **部署：** 待定

## 前端架构
- Vue 3（Composition API）
- Element Plus UI 组件库
- Vue Router（路由）
- Pinia / Vuex（状态管理）
- Axios（HTTP 请求）

## 后端架构
- Spring Boot 3.x
- Spring Security + JWT（认证鉴权）
- MyBatis / MyBatis-Plus（持久层）
- Flyway（数据库迁移）
- Maven（多模块项目）

## 数据库设计
待补充...

## API 设计规范
待补充...

## 部署方案

### Docker Compose 架构
```
├── docker-compose.yml
├── mysql/                 # MySQL 数据持久化
├── nginx/                 # Nginx 配置
│   └── nginx.conf
├── app/                   # 后端 JAR
└── uploads/               # 文件上传目录
```

### 容器规划
| 容器名 | 镜像 | 端口 | 说明 |
|--------|------|------|------|
| oa-mysql | mysql:8.0 | 3306 | MySQL 数据库 |
| oa-redis | redis:7 | 6379 | Redis（可选缓存） |
| oa-backend | 自定义镜像 | 8080 | Spring Boot 后端 |
| oa-frontend | nginx:alpine | 80/443 | Vue 前端静态资源 |
| oa-flowable | 自定义镜像 | 8081 | Flowable 流程引擎 |

### 环境变量
- DATABASE_HOST, DATABASE_PORT, DATABASE_NAME
- REDIS_HOST, REDIS_PORT
- JWT_SECRET, JWT_EXPIRATION