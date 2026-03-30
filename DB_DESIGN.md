# OA 平台 - 数据库设计

> 最后更新：2026-03-30

## 数据库基本信息

- 数据库名：`oa_platform`
- 字符集：`utf8mb4`
- 排序规则：`utf8mb4_unicode_ci`

---

## 一、系统管理表（sys_）

### 1.1 sys_dept（部门表）

```sql
CREATE TABLE sys_dept (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    parent_id   BIGINT NOT NULL DEFAULT 0 COMMENT '父部门ID',
    ancestors   VARCHAR(1000) DEFAULT '' COMMENT '祖级列表',
    dept_name   VARCHAR(100) NOT NULL COMMENT '部门名称',
    dept_code   VARCHAR(50) COMMENT '部门编码',
    dept_type   VARCHAR(20) COMMENT '部门类型（unit/unit_leader/deputy_director/director/section_chief）',
    sort_order  INT DEFAULT 0 COMMENT '显示顺序',
    leader      BIGINT COMMENT '部门负责人用户ID',
    phone       VARCHAR(20) COMMENT '联系电话',
    email       VARCHAR(100) COMMENT '邮箱',
    status      TINYINT NOT NULL DEFAULT 0 COMMENT '部门状态（0正常 1停用）',
    del_flag    TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version     INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    INDEX idx_parent_id (parent_id),
    INDEX idx_dept_code (dept_code)
) COMMENT '部门表';
```

**字段说明：**
- `ancestors`：存储所有祖先节点 ID，如 `0,100,101`，方便查询子部门
- `dept_type`：区分职位层级，对应审批流程节点
- `status`：逻辑删除 + 状态控制

### 1.2 sys_post（岗位表）

```sql
CREATE TABLE sys_post (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '岗位ID',
    post_code   VARCHAR(50) NOT NULL COMMENT '岗位编码',
    post_name   VARCHAR(100) NOT NULL COMMENT '岗位名称',
    post_rank   VARCHAR(20) NOT NULL COMMENT '岗位级别（leader/vice_leader/engineer/director/deputy/staff）',
    sort_order  INT DEFAULT 0 COMMENT '显示顺序',
    status      TINYINT NOT NULL DEFAULT 0 COMMENT '岗位状态（0正常 1停用）',
    del_flag    TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version     INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    UNIQUE INDEX uk_post_code (post_code)
) COMMENT '岗位表';
```

**预设数据：**
| post_code | post_name | post_rank |
|-----------|-----------|-----------|
| leader | 站领导 | leader |
| vice_leader | 副站长 | vice_leader |
| engineer | 总工程师 | engineer |
| director | 室主任 | director |
| deputy | 副主任 | deputy |
| staff | 员工 | staff |

### 1.3 sys_user（用户表）

```sql
CREATE TABLE sys_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    dept_id     BIGINT NOT NULL COMMENT '归属部门ID',
    post_id     BIGINT COMMENT '岗位ID',
    emp_no      VARCHAR(50) NOT NULL COMMENT '工号',
    user_name   VARCHAR(30) NOT NULL COMMENT '用户名称',
    nick_name   VARCHAR(30) DEFAULT '' COMMENT '用户昵称',
    user_type   VARCHAR(10) DEFAULT 'internal' COMMENT '用户类型（internal编制内/contract编制外）',
    email       VARCHAR(100) DEFAULT '' COMMENT '用户邮箱',
    phone       VARCHAR(20) DEFAULT '' COMMENT '手机号码',
    sex         CHAR(1) DEFAULT '0' COMMENT '用户性别（0未知 1男 2女）',
    avatar      VARCHAR(255) DEFAULT '' COMMENT '头像地址',
    password    VARCHAR(200) NOT NULL COMMENT '密码（BCrypt加密）',
    status      TINYINT NOT NULL DEFAULT 0 COMMENT '用户状态（0正常 1停用）',
    login_ip    VARCHAR(50) DEFAULT '' COMMENT '最后登录IP',
    login_date  DATETIME DEFAULT NULL COMMENT '最后登录时间',
    del_flag    TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version     INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    INDEX idx_dept_id (dept_id),
    INDEX idx_post_id (post_id),
    INDEX idx_emp_no (emp_no),
    UNIQUE INDEX uk_user_name (user_name)
) COMMENT '用户表';
```

**字段说明：**
- `user_type`：区分编制内/编制外员工
- `password`：使用 BCrypt 加密存储
- `login_ip/login_date`：记录登录信息

### 1.4 sys_role（角色表）

```sql
CREATE TABLE sys_role (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name   VARCHAR(30) NOT NULL COMMENT '角色名称',
    role_key    VARCHAR(100) NOT NULL COMMENT '角色权限字符串',
    role_sort   INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
    data_scope  VARCHAR(50) DEFAULT '1' COMMENT '数据范围（1全部 2本部门及以下）',
    menu_check_strictly TINYINT DEFAULT 1 COMMENT '菜单树严格绑定',
    dept_check_strictly TINYINT DEFAULT 1 COMMENT '部门树严格绑定',
    status      TINYINT NOT NULL DEFAULT 0 COMMENT '角色状态（0正常 1停用）',
    del_flag    TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version     INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    UNIQUE INDEX uk_role_key (role_key)
) COMMENT '角色表';
```

**预设数据：**
| role_key | role_name | 说明 |
|----------|-----------|------|
| super_admin | 超级管理员 | 系统最高权限 |
| admin | 系统管理员 | 系统管理权限 |
| director | 室主任 | 审核权限 |
| deputy | 副主任 | 核稿权限 |
| staff | 普通员工 | 基本权限 |

### 1.5 sys_user_role（用户角色关联表）

```sql
CREATE TABLE sys_user_role (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id     BIGINT NOT NULL COMMENT '用户ID',
    role_id     BIGINT NOT NULL COMMENT '角色ID',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE INDEX uk_user_role (user_id, role_id)
) COMMENT '用户和角色关联表';
```

### 1.6 sys_menu（菜单权限表）

```sql
CREATE TABLE sys_menu (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
    menu_name   VARCHAR(50) NOT NULL COMMENT '菜单名称',
    parent_id   BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID',
    order_num   INT DEFAULT 0 COMMENT '显示顺序',
    path        VARCHAR(200) DEFAULT '' COMMENT '路由地址',
    component   VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    query       VARCHAR(255) DEFAULT NULL COMMENT '路由参数',
    is_frame    TINYINT NOT NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
    is_cache    TINYINT NOT NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
    menu_type   CHAR(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
    visible     TINYINT NOT NULL DEFAULT 0 COMMENT '菜单状态（0显示 1隐藏）',
    status      TINYINT NOT NULL DEFAULT 0 COMMENT '菜单状态（0正常 1停用）',
    perms       VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    icon        VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version     INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    INDEX idx_parent_id (parent_id)
) COMMENT '菜单权限表';
```

### 1.7 sys_dict（字典表）

```sql
CREATE TABLE sys_dict (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典ID',
    dict_name   VARCHAR(100) NOT NULL COMMENT '字典名称',
    dict_type   VARCHAR(100) NOT NULL COMMENT '字典类型',
    status      TINYINT NOT NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
    del_flag    TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    UNIQUE INDEX uk_dict_type (dict_type)
) COMMENT '字典表';
```

### 1.8 sys_dict_item（字典明细表）

```sql
CREATE TABLE sys_dict_item (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典明细ID',
    dict_id     BIGINT NOT NULL COMMENT '字典ID',
    dict_label  VARCHAR(100) NOT NULL COMMENT '字典标签',
    dict_value  VARCHAR(100) NOT NULL COMMENT '字典键值',
    dict_type   VARCHAR(100) DEFAULT '' COMMENT '字典类型',
    css_class   VARCHAR(100) DEFAULT NULL COMMENT '样式属性',
    list_class  VARCHAR(100) DEFAULT NULL COMMENT '表格回显样式',
    is_default  TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认（0否 1是）',
    sort_order  INT DEFAULT 0 COMMENT '显示顺序',
    status      TINYINT NOT NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
    del_flag    TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_dict_id (dict_id)
) COMMENT '字典明细表';
```

### 1.9 sys_log（系统日志表）

```sql
CREATE TABLE sys_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    module          VARCHAR(50) DEFAULT '' COMMENT '模块名称',
    business_type   VARCHAR(20) DEFAULT '' COMMENT '业务类型（0其它 1新增 2修改 3删除 4授权 5导出 6导入 7强退 8登录 9登出）',
    method          VARCHAR(100) DEFAULT '' COMMENT '请求方法',
    request_method  VARCHAR(10) DEFAULT '' COMMENT '请求方式',
    operator_type   VARCHAR(20) DEFAULT '' COMMENT '操作类别（0其他 1用户 2部门）',
    operator_id     BIGINT DEFAULT NULL COMMENT '操作人员ID',
    operator_name   VARCHAR(50) DEFAULT '' COMMENT '操作人员名称',
    dept_name       VARCHAR(100) DEFAULT '' COMMENT '部门名称',
    url             VARCHAR(255) DEFAULT '' COMMENT '请求URL',
    ip              VARCHAR(50) DEFAULT '' COMMENT '主机地址',
    location        VARCHAR(255) DEFAULT '' COMMENT '操作地点',
    param           TEXT COMMENT '请求参数',
    json_result     TEXT COMMENT '返回参数',
    status          TINYINT NOT NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
    error_msg       VARCHAR(2000) DEFAULT '' COMMENT '错误消息',
    login_time      BIGINT DEFAULT 0 COMMENT '登录时间（毫秒）',
    cost_time       BIGINT DEFAULT 0 COMMENT '消耗时间（毫秒）',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_operator_id (operator_id),
    INDEX idx_business_type (business_type),
    INDEX idx_create_time (create_time)
) COMMENT '系统日志表';
```

### 1.10 sys_notification（通知表）

```sql
CREATE TABLE sys_notification (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    title           VARCHAR(200) NOT NULL COMMENT '通知标题',
    content         TEXT COMMENT '通知内容',
    type            VARCHAR(20) NOT NULL COMMENT '通知类型（system/system_approval）',
    category        VARCHAR(20) DEFAULT 'system' COMMENT '消息分类',
    source_type     VARCHAR(50) DEFAULT '' COMMENT '来源类型（document_task/approval）',
    source_id       VARCHAR(50) DEFAULT '' COMMENT '来源ID',
    priority        VARCHAR(10) DEFAULT 'low' COMMENT '优先级（low/medium/high）',
    sender_id       BIGINT COMMENT '发送者ID',
    sender_name     VARCHAR(50) DEFAULT '' COMMENT '发送者名称',
    target_type     VARCHAR(10) DEFAULT 'user' COMMENT '目标类型（user/dept/role/all）',
    target_id       VARCHAR(500) DEFAULT '' COMMENT '目标ID列表（逗号分隔）',
    read_status     TINYINT NOT NULL DEFAULT 0 COMMENT '已读状态（0未读 1已读）',
    read_time       DATETIME DEFAULT NULL COMMENT '阅读时间',
    del_flag        TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    create_by       VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_target_id (target_id),
    INDEX idx_read_status (read_status),
    INDEX idx_create_time (create_time)
) COMMENT '通知表';
```

---

## 二、业务表（oa_）

### 2.1 oa_document（公文主表）

```sql
CREATE TABLE oa_document (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公文ID',
    document_no     VARCHAR(50) NOT NULL COMMENT '公文编号（自动生成）',
    document_type   VARCHAR(20) NOT NULL COMMENT '公文类型（dispatch收文/receive发文）',
    title           VARCHAR(200) NOT NULL COMMENT '公文标题',
    content         TEXT COMMENT '公文内容/正文',
    urgent_level    VARCHAR(20) DEFAULT 'normal' COMMENT '紧急程度（normal/urgent/very_urgent）',
    secret_level    VARCHAR(20) DEFAULT 'public' COMMENT '密级（public/internal/confidential）',
    dept_id         BIGINT NOT NULL COMMENT '拟稿部门ID',
    user_id         BIGINT NOT NULL COMMENT '拟稿人ID',
    apply_date      DATE NOT NULL COMMENT '拟稿日期',
    process_status  VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '流程状态（draft/pending/approved/rejected/cancelled/archived）',
    current_node    VARCHAR(100) DEFAULT '' COMMENT '当前环节/节点',
    process_instance_id VARCHAR(100) DEFAULT '' COMMENT 'Flowable流程实例ID',
    source_org      VARCHAR(200) COMMENT '来文单位（收文时填写）',
    source_doc_no   VARCHAR(50) COMMENT '来文字号（收文时填写）',
    source_date     DATE COMMENT '来文日期（收文时填写）',
    attachment_ids  VARCHAR(1000) DEFAULT '' COMMENT '附件ID列表（逗号分隔）',
    remark          VARCHAR(500) DEFAULT '' COMMENT '备注',
    del_flag        TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    create_by       VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version         INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    INDEX idx_document_no (document_no),
    INDEX idx_document_type (document_type),
    INDEX idx_process_status (process_status),
    INDEX idx_user_id (user_id),
    INDEX idx_dept_id (dept_id),
    INDEX idx_create_time (create_time)
) COMMENT '公文主表';
```

**字段说明：**
- `document_no`：自动生成，格式如 `FW-20260330-001`（发文）或 `SW-20260330-001`（收文）
- `process_status`：draft=草稿, pending=审批中, approved=已通过, rejected=驳回, cancelled=撤销, archived=归档
- `process_instance_id`：关联 Flowable 流程实例

### 2.2 oa_document_process（公文流程记录表）

```sql
CREATE TABLE oa_document_process (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    document_id         BIGINT NOT NULL COMMENT '公文ID',
    process_instance_id VARCHAR(100) COMMENT 'Flowable流程实例ID',
    process_key         VARCHAR(100) COMMENT '流程定义Key',
    task_id             VARCHAR(100) COMMENT 'Flowable任务ID',
    task_name           VARCHAR(200) COMMENT '任务名称/环节',
    node_type           VARCHAR(20) COMMENT '节点类型（start/user_task/end）',
    assignee_id         BIGINT COMMENT '处理人ID',
    assignee_name       VARCHAR(50) COMMENT '处理人名称',
    action              VARCHAR(20) DEFAULT '' COMMENT '操作动作（submit/approve/reject/withdraw/return）',
    opinion             TEXT COMMENT '审批意见',
    comment             VARCHAR(500) COMMENT '简短备注',
    start_time          DATETIME DEFAULT NULL COMMENT '开始时间',
    end_time            DATETIME DEFAULT NULL COMMENT '结束时间',
    duration            BIGINT DEFAULT 0 COMMENT '处理时长（秒）',
    result              VARCHAR(20) DEFAULT '' COMMENT '处理结果（pass/reject/withdraw/return）',
    cc_to               VARCHAR(500) DEFAULT '' COMMENT '抄送人ID列表',
    del_flag            TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    create_by           VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_document_id (document_id),
    INDEX idx_assignee_id (assignee_id),
    INDEX idx_create_time (create_time)
) COMMENT '公文流程记录表';
```

### 2.3 oa_document_attachment（公文附件表）

```sql
CREATE TABLE oa_document_attachment (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '附件ID',
    document_id     BIGINT NOT NULL COMMENT '公文ID',
    file_name       VARCHAR(200) NOT NULL COMMENT '文件名称',
    file_path       VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size       BIGINT NOT NULL COMMENT '文件大小（字节）',
    file_type       VARCHAR(50) DEFAULT '' COMMENT '文件类型/MIME',
    file_extension  VARCHAR(20) DEFAULT '' COMMENT '文件扩展名',
    sort_order      INT DEFAULT 0 COMMENT '排序',
    uploader_id     BIGINT NOT NULL COMMENT '上传人ID',
    uploader_name   VARCHAR(50) NOT NULL COMMENT '上传人名称',
    upload_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    del_flag        TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    INDEX idx_document_id (document_id)
) COMMENT '公文附件表';
```

---

## 三、Flowable 相关表

Flowable 会自动创建自己的表结构（ACT_* 前缀），我们在业务层主要关注：

- `ACT_RU_*`：运行时表
- `ACT_HI_*`：历史表
- `ACT_RE_*`：流程定义表

业务表通过 `process_instance_id` 关联 Flowable 实例。

---

## 四、索引设计汇总

| 表名 | 索引名称 | 字段 | 类型 |
|------|----------|------|------|
| sys_dept | idx_parent_id | parent_id | 普通 |
| sys_user | idx_dept_id | dept_id | 普通 |
| sys_user | idx_post_id | post_id | 普通 |
| sys_user | idx_emp_no | emp_no | 普通 |
| sys_log | idx_operator_id | operator_id | 普通 |
| sys_log | idx_create_time | create_time | 普通 |
| sys_notification | idx_target_id | target_id | 普通 |
| oa_document | idx_document_no | document_no | 普通 |
| oa_document | idx_process_status | process_status | 普通 |
| oa_document | idx_create_time | create_time | 普通 |
| oa_document_process | idx_document_id | document_id | 普通 |
| oa_document_process | idx_assignee_id | assignee_id | 普通 |
| oa_document_attachment | idx_document_id | document_id | 普通 |

---

## 五、命名规范

- 表名：`sys_` 系统表前缀，`oa_` 业务表前缀
- 主键：`id`，类型 BIGINT，AUTO_INCREMENT
- 逻辑删除：`del_flag`，TINYINT（0存在 1删除）
- 时间戳：`create_time`, `update_time`，DATETIME
- 创建人/更新人：`create_by`, `update_by`，VARCHAR(64)
- 乐观锁：`version`，INT，默认0
- 状态：`status`，TINYINT（0正常 1停用）