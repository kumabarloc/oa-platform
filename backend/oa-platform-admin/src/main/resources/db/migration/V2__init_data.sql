-- V2__init_data.sql
-- 初始化数据（岗位、角色、菜单、默认管理员）

-- ============================================
-- 岗位数据
-- ============================================
INSERT INTO sys_post (post_code, post_name, post_rank, sort_order, status, del_flag) VALUES
('leader', '站领导', 'leader', 1, 0, 0),
('vice_leader', '副站长', 'vice_leader', 2, 0, 0),
('engineer', '总工程师', 'engineer', 3, 0, 0),
('director', '室主任', 'director', 4, 0, 0),
('deputy', '副主任', 'deputy', 5, 0, 0),
('staff', '员工', 'staff', 6, 0, 0);

-- ============================================
-- 角色数据
-- ============================================
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag) VALUES
('超级管理员', 'super_admin', 1, '1', 1, 1, 0, 0),
('系统管理员', 'admin', 2, '1', 1, 1, 0, 0),
('室主任', 'director', 3, '2', 1, 1, 0, 0),
('副主任', 'deputy', 4, '2', 1, 1, 0, 0),
('普通员工', 'staff', 5, '2', 1, 1, 0, 0);

-- ============================================
-- 菜单数据
-- ============================================
-- 一级菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon) VALUES
('系统管理', 0, 1, 'system', NULL, 'M', 0, 0, '', 'system'),
('公文管理', 0, 2, 'document', NULL, 'M', 0, 0, '', 'document'),
('工作台', 0, 3, 'dashboard', 'dashboard/index', 'C', 0, 0, '', 'home');

-- 系统管理子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon) VALUES
('用户管理', 1, 1, 'user', 'system/user/index', 'C', 0, 0, 'system:user:list', 'user'),
('部门管理', 1, 2, 'dept', 'system/dept/index', 'C', 0, 0, 'system:dept:list', 'tree'),
('角色管理', 1, 3, 'role', 'system/role/index', 'C', 0, 0, 'system:role:list', 'peoples'),
('岗位管理', 1, 4, 'post', 'system/post/index', 'C', 0, 0, 'system:post:list', 'post'),
('菜单管理', 1, 5, 'menu', 'system/menu/index', 'C', 0, 0, 'system:menu:list', 'tree-table'),
('字典管理', 1, 6, 'dict', 'system/dict/index', 'C', 0, 0, 'system:dict:list', 'dict'),
('操作日志', 1, 7, 'log', 'system/log/index', 'C', 0, 0, 'system:log:list', 'log');

-- 公文管理子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon) VALUES
('发文管理', 2, 1, 'dispatch', 'document/dispatch/index', 'C', 0, 0, 'document:dispatch:list', 'edit'),
('收文管理', 2, 2, 'receive', 'document/receive/index', 'C', 0, 0, 'document:receive:list', 'document');

-- ============================================
-- 用户数据（默认管理员 admin/123456）
-- ============================================
INSERT INTO sys_user (dept_id, emp_no, user_name, nick_name, user_type, email, phone, sex, password, status, del_flag) VALUES
(1, 'admin', 'admin', '管理员', 'internal', 'admin@example.com', '13800138000', '1', '$2a$10$7JB720yub/SZvFf0EI5M0.YuXR8YiZEuP1nE8g2E.GJ0Q5j2q3vKq', 0, 0);

-- ============================================
-- 角色菜单关联
-- ============================================
-- 超级管理员拥有所有菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE del_flag = 0;

-- 系统管理员拥有系统管理和工作台菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, id FROM sys_menu WHERE del_flag = 0 AND id IN (1, 3);

-- 室主任只有工作台和公文管理
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 3, id FROM sys_menu WHERE del_flag = 0 AND id IN (2, 3);

-- ============================================
-- 用户角色关联
-- ============================================
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);