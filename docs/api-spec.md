# OA 平台 API 规范文档

> 最后更新：2026-04-02

## 通用说明

所有 API 请求需在 Header 中携带 JWT Token：
```
Authorization: Bearer <token>
```

**统一响应格式：**
```json
{
  "code": 200,
  "data": { ... },
  "message": "操作成功"
}
```

**错误码说明：**
| code | 说明 |
|------|------|
| 200 | 成功 |
| 401 | 未认证（Token 无效或过期） |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 通知管理 API

基础路径：`/api/system/notification`

### GET /api/system/notification/list
- **认证**：需要
- **描述**：分页查询通知列表（管理员查看所有通知）
- **请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| size | int | 否 | 每页条数，默认 10 |
| title | string | 否 | 通知标题（模糊搜索） |
| type | string | 否 | 通知类型 |
| startDate | string | 否 | 开始日期（yyyy-MM-dd） |
| endDate | string | 否 | 结束日期（yyyy-MM-dd） |

- **响应**：
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "title": "系统通知",
        "content": "通知内容",
        "type": "SYSTEM",
        "targetType": "ALL",
        "publishUserId": 1,
        "publishUserName": "管理员",
        "publishTime": "2026-04-02 10:00:00",
        "readCount": 5,
        "createTime": "2026-04-02 10:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10
  },
  "message": "操作成功"
}
```

### GET /api/system/notification/my
- **认证**：需要
- **描述**：获取当前用户收到的通知列表
- **请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| size | int | 否 | 每页条数，默认 10 |
| readStatus | int | 否 | 阅读状态（0未读，1已读） |

- **响应**：同上 records 结构，含 `readStatus` 字段

### GET /api/system/notification/unread-count
- **认证**：需要
- **描述**：获取当前用户未读通知数量
- **响应**：
```json
{
  "code": 200,
  "data": {
    "count": 5
  },
  "message": "操作成功"
}
```

### GET /api/system/notification/{id}
- **认证**：需要
- **描述**：获取通知详情
- **响应**：
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "title": "系统通知",
    "content": "通知内容详情",
    "type": "SYSTEM",
    "targetType": "ALL",
    "publishUserId": 1,
    "publishUserName": "管理员",
    "publishTime": "2026-04-02 10:00:00"
  },
  "message": "操作成功"
}
```

### POST /api/system/notification
- **认证**：需要（管理员权限）
- **描述**：发布通知
- **请求体**：
```json
{
  "title": "通知标题",
  "content": "通知内容",
  "type": "SYSTEM",
  "targetType": "ALL",
  "targetUserIds": [],
  "targetDeptIds": []
}
```
- **响应**：
```json
{
  "code": 200,
  "data": { "id": 1 },
  "message": "发布成功"
}
```

### PUT /api/system/notification/{id}
- **认证**：需要（管理员权限）
- **描述**：编辑通知
- **请求体**：同 POST
- **响应**：同上

### DELETE /api/system/notification/{id}
- **认证**：需要（管理员权限）
- **描述**：删除通知
- **响应**：
```json
{
  "code": 200,
  "data": null,
  "message": "删除成功"
}
```

### PUT /api/system/notification/{id}/read
- **认证**：需要
- **描述**：标记单条通知为已读
- **响应**：
```json
{
  "code": 200,
  "data": null,
  "message": "操作成功"
}
```

### PUT /api/system/notification/read-all
- **认证**：需要
- **描述**：标记全部通知为已读
- **响应**：
```json
{
  "code": 200,
  "data": null,
  "message": "全部已读"
}
```

---

## 收文管理 API

基础路径：`/api/workflow/incoming`

### GET /api/workflow/incoming/list
- **认证**：需要
- **描述**：分页查询收文列表
- **请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| size | int | 否 | 每页条数，默认 10 |
| keyword | string | 否 | 关键词搜索（文号/标题/来文单位） |
| status | string | 否 | 状态（PENDING/DISTRIBUTED/ARCHIVED） |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |

- **响应**：
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "documentNumber": "SW-20260402-001",
        "title": "关于xxx的通知",
        "sourceUnit": "xxx单位",
        "receiveDate": "2026-04-02",
        "status": "PENDING",
        "handlerId": 1,
        "handlerName": "张三",
        "createTime": "2026-04-02 09:00:00"
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10
  },
  "message": "操作成功"
}
```

### GET /api/workflow/incoming/{id}
- **认证**：需要
- **描述**：获取收文详情
- **响应**：
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "documentNumber": "SW-20260402-001",
    "title": "关于xxx的通知",
    "sourceUnit": "xxx单位",
    "contactPerson": "李四",
    "contactPhone": "13800138000",
    "receiveDate": "2026-04-02",
    "urgencyLevel": "NORMAL",
    "category": "NOTICE",
    "content": "公文正文内容",
    "attachmentUrls": ["/uploads/xxx.pdf"],
    "status": "PENDING",
    "handlerId": 1,
    "handlerName": "张三",
    "distributeNote": "",
    "archiveNote": "",
    "createTime": "2026-04-02 09:00:00"
  },
  "message": "操作成功"
}
```

### POST /api/workflow/incoming
- **认证**：需要
- **描述**：登记新收文（自动生成收文编号 SW-YYYYMMDD-序号）
- **请求体**：
```json
{
  "title": "关于xxx的通知",
  "sourceUnit": "xxx单位",
  "contactPerson": "李四",
  "contactPhone": "13800138000",
  "receiveDate": "2026-04-02",
  "urgencyLevel": "NORMAL",
  "category": "NOTICE",
  "content": "公文正文内容",
  "attachmentUrls": []
}
```
- **响应**：
```json
{
  "code": 200,
  "data": { "id": 1, "documentNumber": "SW-20260402-001" },
  "message": "登记成功"
}
```

### PUT /api/workflow/incoming/{id}
- **认证**：需要
- **描述**：编辑收文
- **请求体**：同 POST
- **响应**：同上

### DELETE /api/workflow/incoming/{id}
- **认证**：需要
- **描述**：删除收文
- **响应**：
```json
{
  "code": 200,
  "data": null,
  "message": "删除成功"
}
```

### PUT /api/workflow/incoming/{id}/distribute
- **认证**：需要
- **描述**：分发收文
- **请求体**：
```json
{
  "handlerId": 1,
  "note": "请及时处理"
}
```
- **响应**：
```json
{
  "code": 200,
  "data": null,
  "message": "分发成功"
}
```

### PUT /api/workflow/incoming/{id}/archive
- **认证**：需要
- **描述**：归档收文
- **请求体**：
```json
{
  "archiveNote": "归档备注"
}
```
- **响应**：
```json
{
  "code": 200,
  "data": null,
  "message": "归档成功"
}
```

---

## 发文管理 API

基础路径：`/api/workflow/document`

### GET /api/workflow/document/list
- **认证**：需要
- **描述**：分页查询发文列表
- **请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| size | int | 否 | 每页条数，默认 10 |
| keyword | string | 否 | 关键词搜索 |
| status | string | 否 | 状态（DRAFT/PENDING/APPROVED/REJECTED/PUBLISHED） |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |

- **响应**：
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "documentNumber": "FW-20260402-001",
        "title": "关于xxx的通知",
        "documentType": "NOTICE",
        "urgencyLevel": "NORMAL",
        "status": "DRAFT",
        "creatorId": 1,
        "creatorName": "张三",
        "createTime": "2026-04-02 09:00:00"
      }
    ],
    "total": 30,
    "page": 1,
    "size": 10
  },
  "message": "操作成功"
}
```

### GET /api/workflow/document/{id}
- **认证**：需要
- **描述**：获取发文详情
- **响应**：
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "documentNumber": "FW-20260402-001",
    "title": "关于xxx的通知",
    "documentType": "NOTICE",
    "urgencyLevel": "NORMAL",
    "content": "公文正文",
    "attachmentUrls": [],
    "status": "DRAFT",
    "creatorId": 1,
    "creatorName": "张三",
    "currentApproverId": null,
    "flowInstanceId": null,
    "createTime": "2026-04-02 09:00:00"
  },
  "message": "操作成功"
}
```

### POST /api/workflow/document
- **认证**：需要
- **描述**：创建发文
- **请求体**：
```json
{
  "title": "关于xxx的通知",
  "documentType": "NOTICE",
  "urgencyLevel": "NORMAL",
  "content": "公文正文内容",
  "attachmentUrls": []
}
```
- **响应**：
```json
{
  "code": 200,
  "data": { "id": 1 },
  "message": "创建成功"
}
```

### PUT /api/workflow/document/{id}
- **认证**：需要
- **描述**：编辑发文（仅草稿状态可编辑）
- **请求体**：同 POST
- **响应**：同上

### DELETE /api/workflow/document/{id}
- **认证**：需要
- **描述**：删除发文（仅草稿状态可删除）
- **响应**：
```json
{
  "code": 200,
  "data": null,
  "message": "删除成功"
}
```

### POST /api/workflow/document/{id}/submit
- **认证**：需要
- **描述**：提交发文，启动审批流程
- **响应**：
```json
{
  "code": 200,
  "data": {
    "flowInstanceId": "xxx"
  },
  "message": "提交成功，审批流程已启动"
}
```

### PUT /api/workflow/document/{id}/approve
- **认证**：需要（审批人权限）
- **描述**：审批通过
- **请求体**：
```json
{
  "comment": "同意"
}
```
- **响应**：
```json
{
  "code": 200,
  "data": null,
  "message": "审批通过"
}
```

### PUT /api/workflow/document/{id}/reject
- **认证**：需要（审批人权限）
- **描述**：审批驳回
- **请求体**：
```json
{
  "comment": "驳回原因"
}
```
- **响应**：
```json
{
  "code": 200,
  "data": null,
  "message": "已驳回"
}
```

### GET /api/workflow/document/my-tasks
- **认证**：需要
- **描述**：获取当前用户待审批的任务列表
- **请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| size | int | 否 | 每页条数，默认 10 |

- **响应**：
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "taskId": "xxx",
        "documentId": 1,
        "documentNumber": "FW-20260402-001",
        "title": "关于xxx的通知",
        "urgencyLevel": "NORMAL",
        "creatorName": "张三",
        "createTime": "2026-04-02 09:00:00"
      }
    ],
    "total": 5,
    "page": 1,
    "size": 10
  },
  "message": "操作成功"
}
```

### GET /api/workflow/document/{id}/history
- **认证**：需要
- **描述**：获取发文审批历史记录
- **响应**：
```json
{
  "code": 200,
  "data": [
    {
      "approverName": "室主任",
      "decision": "APPROVE",
      "comment": "同意",
      "time": "2026-04-02 14:00:00"
    },
    {
      "approverName": "副主任",
      "decision": "APPROVE",
      "comment": "已阅",
      "time": "2026-04-02 15:00:00"
    }
  ],
  "message": "操作成功"
}
```

---

## 登录日志 API

基础路径：`/api/system/login-log`

### GET /api/system/login-log/list
- **认证**：需要（管理员权限）
- **描述**：分页查询登录日志
- **请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| size | int | 否 | 每页条数，默认 10 |
| username | string | 否 | 用户名（模糊搜索） |
| status | string | 否 | 登录状态（SUCCESS/FAIL） |
| startDate | string | 否 | 开始时间（yyyy-MM-dd HH:mm:ss） |
| endDate | string | 否 | 结束时间（yyyy-MM-dd HH:mm:ss） |
| ip | string | 否 | IP 地址 |

- **响应**：
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "username": "admin",
        "ip": "192.168.1.100",
        "location": "内网",
        "browser": "Chrome",
        "os": "Windows 10",
        "status": "SUCCESS",
        "msg": "登录成功",
        "loginTime": "2026-04-02 09:00:00"
      }
    ],
    "total": 500,
    "page": 1,
    "size": 10
  },
  "message": "操作成功"
}
```

---

## 操作日志 API

基础路径：`/api/system/operation-log`

### GET /api/system/operation-log/list
- **认证**：需要（管理员权限）
- **描述**：分页查询操作日志
- **请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| size | int | 否 | 每页条数，默认 10 |
| username | string | 否 | 操作人用户名（模糊搜索） |
| module | string | 否 | 操作模块 |
| operation | string | 否 | 操作类型 |
| startDate | string | 否 | 开始时间（yyyy-MM-dd HH:mm:ss） |
| endDate | string | 否 | 结束时间（yyyy-MM-dd HH:mm:ss） |

- **响应**：
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "username": "admin",
        "module": "用户管理",
        "operation": "新增用户",
        "method": "POST /api/system/user",
        "requestParams": "{\"username\":\"test\",\"nickname\":\"测试\"}",
        "responseResult": "{\"code\":200}",
        "ip": "192.168.1.100",
        "costTime": 150,
        "createTime": "2026-04-02 10:00:00"
      }
    ],
    "total": 1000,
    "page": 1,
    "size": 10
  },
  "message": "操作成功"
}
```
