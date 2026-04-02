# OA 平台 - 技术架构文档

> 最后更新：2026-04-02

## 系统架构
- **前端：** Vue 3 + Element Plus（SPA）
- **后端：** Spring Boot 3.x + JDK 21
- **数据库：** MySQL 8.x
- **部署：** Docker Compose

## 前端架构
- Vue 3（Composition API）
- Element Plus UI 组件库
- Vue Router（路由）
- Pinia（状态管理）
- Axios（HTTP 请求，响应拦截器直接返回 data 部分）

## 后端架构
- Spring Boot 3.x
- Spring Security + JWT（认证鉴权）
- MyBatis / MyBatis-Plus（持久层）
- Flyway（数据库迁移）
- Maven（多模块项目）
- PageHelper（分页插件）

---

## 数据库设计

### 核心业务表

| 表名 | 说明 | 所属模块 |
|------|------|----------|
| sys_user | 用户表 | 用户管理 |
| sys_role | 角色表 | 角色管理 |
| sys_menu | 菜单表 | 菜单管理 |
| sys_dept | 部门表 | 部门管理 |
| sys_post | 岗位表 | 岗位管理 |
| sys_dict | 字典表 | 字典管理 |
| sys_dict_item | 字典项表 | 字典管理 |
| sys_role_menu | 角色菜单关联表 | 权限管理 |
| sys_user_role | 用户角色关联表 | 权限管理 |
| sys_document | 发文/公告文档表 | 发文管理 |
| sys_notification | 通知公告表 | 通知管理 |
| sys_notification_read | 通知阅读记录表 | 通知管理 |
| sys_incoming_document | 收文管理表 | 收文管理 |
| sys_log | 操作日志表 | 日志管理 |
| sys_login_log | 登录日志表 | 日志管理 |
| sys_operation_log | 操作日志表 | 日志管理 |

### sys_user 表结构
```sql
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  dept_id BIGINT NOT NULL,
  post_id BIGINT,
  emp_no VARCHAR(50) NOT NULL,
  user_name VARCHAR(30) NOT NULL UNIQUE,
  nick_name VARCHAR(30),
  user_type VARCHAR(10) DEFAULT 'internal',
  email VARCHAR(100),
  phone VARCHAR(20),
  sex CHAR(1) DEFAULT '0',
  avatar VARCHAR(255),
  password VARCHAR(200) NOT NULL,
  status TINYINT NOT NULL DEFAULT 0,
  login_ip VARCHAR(50),
  login_date DATETIME,
  del_flag TINYINT NOT NULL DEFAULT 0,
  create_by VARCHAR(64),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by VARCHAR(64),
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  version INT NOT NULL DEFAULT 0
);
```

### sys_notification 表结构
```sql
CREATE TABLE sys_notification (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  content TEXT,
  type VARCHAR(20) NOT NULL,
  category VARCHAR(20) DEFAULT 'system',
  source_type VARCHAR(50),
  source_id VARCHAR(50),
  priority VARCHAR(10) DEFAULT 'low',
  sender_id BIGINT,
  sender_name VARCHAR(50),
  target_type VARCHAR(10) DEFAULT 'user',
  target_id VARCHAR(500),
  read_status TINYINT NOT NULL DEFAULT 0,
  read_time DATETIME,
  del_flag TINYINT NOT NULL DEFAULT 0,
  create_by VARCHAR(64),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by VARCHAR(64),
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### sys_notification_read 表结构
```sql
CREATE TABLE sys_notification_read (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  notification_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  read_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_notify_user (notification_id, user_id)
);
```

### sys_incoming_document 表结构
```sql
CREATE TABLE sys_incoming_document (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  doc_number VARCHAR(50),
  source_unit VARCHAR(200),
  receive_time DATETIME,
  title VARCHAR(200) NOT NULL,
  content TEXT,
  attach_urls VARCHAR(2000),
  status VARCHAR(20) DEFAULT 'received',
  current_handler_id BIGINT,
  draft_user_id BIGINT,
  priority VARCHAR(20) DEFAULT 'normal',
  create_by BIGINT,
  create_dept BIGINT DEFAULT 100,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag CHAR(1) DEFAULT '0',
  INDEX idx_doc_number (doc_number),
  INDEX idx_status (status),
  INDEX idx_receive_time (receive_time)
);
```

### sys_login_log 表结构
```sql
CREATE TABLE sys_login_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT,
  username VARCHAR(50),
  ip VARCHAR(128),
  location VARCHAR(255),
  browser VARCHAR(50),
  os VARCHAR(50),
  status CHAR(1) DEFAULT '0',
  msg VARCHAR(255),
  login_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user (user_id),
  INDEX idx_login_time (login_time)
);
```

### sys_operation_log 表结构
```sql
CREATE TABLE sys_operation_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) DEFAULT '',
  business_type VARCHAR(20),
  method VARCHAR(100),
  request_method VARCHAR(10),
  operator_type VARCHAR(10),
  oper_id BIGINT,
  oper_name VARCHAR(50),
  dept_name VARCHAR(50),
  url VARCHAR(255),
  ip VARCHAR(128),
  location VARCHAR(255),
  oper_param VARCHAR(2000),
  json_result TEXT,
  status TINYINT DEFAULT 1,
  error_msg TEXT,
  oper_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_oper_time (oper_time),
  INDEX idx_oper_id (oper_id)
);
```

---

## API 设计规范

### 统一响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1712000000000
}
```

### 认证
- Header: `Authorization: Bearer <token>`
- 登录接口：`POST /api/auth/login`
- 获取用户信息：`GET /api/system/user/info`

### 分页接口响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 10
  },
  "timestamp": 1712000000000
}
```

### 用户管理接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/user/list | 用户分页列表 |
| GET | /api/system/user/{id} | 用户详情 |
| GET | /api/system/user/info | 获取当前用户信息 |
| POST | /api/system/user | 创建用户 |
| PUT | /api/system/user/{id} | 更新用户 |
| DELETE | /api/system/user/{id} | 删除用户 |
| PUT | /api/system/user/{id}/password | 重置用户密码 |
| PUT | /api/system/user/password | 修改当前用户密码 |

### 通知管理接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/notification/list | 通知分页列表 |
| GET | /api/system/notification/{id} | 通知详情 |
| POST | /api/system/notification | 创建通知 |
| PUT | /api/system/notification/{id} | 更新通知 |
| DELETE | /api/system/notification/{id} | 删除通知 |
| PUT | /api/system/notification/{id}/read | 标记已读 |
| PUT | /api/system/notification/read-all | 全部已读 |
| GET | /api/system/notification/my | 我的通知列表 |

### 收文管理接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/workflow/incoming/list | 收文分页列表 |
| GET | /api/workflow/incoming/{id} | 收文详情 |
| POST | /api/workflow/incoming | 收文登记 |
| PUT | /api/workflow/incoming/{id} | 更新收文 |
| DELETE | /api/workflow/incoming/{id} | 删除收文 |
| PUT | /api/workflow/incoming/{id}/distribute | 分发收文 |
| PUT | /api/workflow/incoming/{id}/archive | 归档收文 |

### 日志接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/login-log/list | 登录日志列表 |
| GET | /api/system/operation-log/list | 操作日志列表 |

---

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
- JAVA_HOME（指向 JDK 21）
