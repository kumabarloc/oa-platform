-- V1__init_schema.sql
-- 初始化数据库表结构

-- ============================================
-- 系统管理表
-- ============================================

-- 部门表
CREATE TABLE sys_dept (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    parent_id   BIGINT NOT NULL DEFAULT 0 COMMENT '父部门ID',
    ancestors   VARCHAR(1000) DEFAULT '' COMMENT '祖级列表',
    dept_name   VARCHAR(100) NOT NULL COMMENT '部门名称',
    dept_code   VARCHAR(50) COMMENT '部门编码',
    dept_type   VARCHAR(20) COMMENT '部门类型',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 岗位表
CREATE TABLE sys_post (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '岗位ID',
    post_code   VARCHAR(50) NOT NULL COMMENT '岗位编码',
    post_name   VARCHAR(100) NOT NULL COMMENT '岗位名称',
    post_rank   VARCHAR(20) NOT NULL COMMENT '岗位级别',
    sort_order  INT DEFAULT 0 COMMENT '显示顺序',
    status      TINYINT NOT NULL DEFAULT 0 COMMENT '岗位状态（0正常 1停用）',
    del_flag    TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version     INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    UNIQUE INDEX uk_post_code (post_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位表';

-- 用户表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户和角色关联表
CREATE TABLE sys_user_role (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id     BIGINT NOT NULL COMMENT '用户ID',
    role_id     BIGINT NOT NULL COMMENT '角色ID',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE INDEX uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户和角色关联表';

-- 角色和菜单关联表
CREATE TABLE sys_role_menu (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_id     BIGINT NOT NULL COMMENT '角色ID',
    menu_id     BIGINT NOT NULL COMMENT '菜单ID',
    UNIQUE INDEX uk_role_menu (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和菜单关联表';

-- 菜单权限表
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
    del_flag    TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    perms       VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    icon        VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version     INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- 字典表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典表';

-- 字典明细表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典明细表';

-- 系统日志表
CREATE TABLE sys_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    module          VARCHAR(50) DEFAULT '' COMMENT '模块名称',
    business_type   VARCHAR(20) DEFAULT '' COMMENT '业务类型',
    method          VARCHAR(100) DEFAULT '' COMMENT '请求方法',
    request_method  VARCHAR(10) DEFAULT '' COMMENT '请求方式',
    operator_type   VARCHAR(20) DEFAULT '' COMMENT '操作类别',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';

-- 通知表
CREATE TABLE sys_notification (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    title           VARCHAR(200) NOT NULL COMMENT '通知标题',
    content         TEXT COMMENT '通知内容',
    type            VARCHAR(20) NOT NULL COMMENT '通知类型',
    category        VARCHAR(20) DEFAULT 'system' COMMENT '消息分类',
    source_type     VARCHAR(50) DEFAULT '' COMMENT '来源类型',
    source_id       VARCHAR(50) DEFAULT '' COMMENT '来源ID',
    priority        VARCHAR(10) DEFAULT 'low' COMMENT '优先级',
    sender_id       BIGINT COMMENT '发送者ID',
    sender_name     VARCHAR(50) DEFAULT '' COMMENT '发送者名称',
    target_type     VARCHAR(10) DEFAULT 'user' COMMENT '目标类型',
    target_id       VARCHAR(500) DEFAULT '' COMMENT '目标ID列表',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';