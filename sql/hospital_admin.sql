-- 医院管理系统数据库
-- 创建数据库
CREATE DATABASE IF NOT EXISTS hospital_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hospital_admin;

-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表（菜单表）
CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(100) COMMENT '权限编码',
    type TINYINT NOT NULL COMMENT '类型 1:菜单 2:按钮',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    icon VARCHAR(50) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 患者表
CREATE TABLE patient (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_no VARCHAR(30) UNIQUE COMMENT '患者编号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender TINYINT NOT NULL COMMENT '性别 0:女 1:男',
    age INT COMMENT '年龄',
    id_card VARCHAR(18) UNIQUE COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '联系方式',
    address VARCHAR(255) COMMENT '地址',
    medical_history TEXT COMMENT '既往病史',
    allergy_history TEXT COMMENT '过敏史',
    emergency_contact VARCHAR(50) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者表';

-- 登录日志表
CREATE TABLE sys_login_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) COMMENT '用户名',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    login_location VARCHAR(100) COMMENT '登录地点',
    browser VARCHAR(50) COMMENT '浏览器',
    os VARCHAR(50) COMMENT '操作系统',
    status TINYINT DEFAULT 1 COMMENT '登录状态 0:失败 1:成功',
    msg VARCHAR(255) COMMENT '提示信息',
    login_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- 操作日志表
CREATE TABLE sys_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    module VARCHAR(50) COMMENT '模块标题',
    business_type VARCHAR(20) COMMENT '业务类型',
    method VARCHAR(100) COMMENT '方法名称',
    request_method VARCHAR(10) COMMENT '请求方式',
    operator_type VARCHAR(20) COMMENT '操作类别',
    username VARCHAR(50) COMMENT '操作人员',
    dept_name VARCHAR(50) COMMENT '部门名称',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    operation_location VARCHAR(100) COMMENT '操作地点',
    url VARCHAR(255) COMMENT '请求URL',
    param TEXT COMMENT '请求参数',
    json_result TEXT COMMENT '返回参数',
    status TINYINT DEFAULT 1 COMMENT '操作状态 0:失败 1:成功',
    error_msg TEXT COMMENT '错误消息',
    operation_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    cost_time BIGINT COMMENT '消耗时间(毫秒)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 初始化数据
-- 插入默认用户 admin/123456
INSERT INTO sys_user (username, password, nickname, email, status) VALUES
('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', 'admin@hospital.com', 1),
('doctor', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '医生', 'doctor@hospital.com', 1),
('nurse', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '护士', 'nurse@hospital.com', 1);

-- 插入默认角色
INSERT INTO sys_role (role_name, role_code, description, sort) VALUES
('超级管理员', 'ROLE_ADMIN', '拥有所有权限', 1),
('医生', 'ROLE_DOCTOR', '医生角色，可管理患者', 2),
('护士', 'ROLE_NURSE', '护士角色，可查看患者', 3);

-- 插入默认权限
INSERT INTO sys_permission (parent_id, name, code, type, path, component, icon, sort) VALUES
-- 一级菜单
(0, '系统管理', 'system', 1, '/system', NULL, 'setting', 1),
(0, '患者管理', 'patient', 1, '/patient', NULL, 'user', 2),
(0, '日志管理', 'log', 1, '/log', NULL, 'document', 3),
-- 系统管理子菜单
(1, '用户管理', 'system:user', 1, '/system/user', 'system/user/index', 'user', 1),
(1, '角色管理', 'system:role', 1, '/system/role', 'system/role/index', 'role', 2),
(1, '权限管理', 'system:permission', 1, '/system/permission', 'system/permission/index', 'key', 3),
-- 患者管理子菜单
(2, '患者列表', 'patient:list', 1, '/patient/list', 'patient/list/index', 'list', 1),
-- 日志管理子菜单
(3, '登录日志', 'log:login', 1, '/log/login', 'log/login/index', 'login', 1),
(3, '操作日志', 'log:operation', 1, '/log/operation', 'log/operation/index', 'operation', 2),
-- 按钮权限
(4, '用户查询', 'system:user:query', 2, NULL, NULL, NULL, 1),
(4, '用户新增', 'system:user:add', 2, NULL, NULL, NULL, 2),
(4, '用户编辑', 'system:user:edit', 2, NULL, NULL, NULL, 3),
(4, '用户删除', 'system:user:delete', 2, NULL, NULL, NULL, 4),
(5, '角色查询', 'system:role:query', 2, NULL, NULL, NULL, 1),
(5, '角色新增', 'system:role:add', 2, NULL, NULL, NULL, 2),
(5, '角色编辑', 'system:role:edit', 2, NULL, NULL, NULL, 3),
(5, '角色删除', 'system:role:delete', 2, NULL, NULL, NULL, 4),
(5, '分配权限', 'system:role:permission', 2, NULL, NULL, NULL, 5),
(7, '患者查询', 'patient:list:query', 2, NULL, NULL, NULL, 1),
(7, '患者新增', 'patient:list:add', 2, NULL, NULL, NULL, 2),
(7, '患者编辑', 'patient:list:edit', 2, NULL, NULL, NULL, 3),
(7, '患者删除', 'patient:list:delete', 2, NULL, NULL, NULL, 4);

-- 分配用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3);

-- 分配角色权限（超级管理员拥有所有权限）
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 医生角色权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(2, 2), (2, 7), (2, 19), (2, 20), (2, 21), (2, 22);

-- 护士角色权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(3, 2), (3, 7), (3, 19);

-- 插入测试患者数据
INSERT INTO patient (patient_no, name, gender, age, id_card, phone, address, medical_history, allergy_history, emergency_contact, emergency_phone) VALUES
('P20240001', '张三', 1, 35, '110101198901011234', '13800138001', '北京市朝阳区', '高血压', '青霉素过敏', '张夫人', '13800138002'),
('P20240002', '李四', 0, 28, '110101199602021234', '13800138003', '北京市海淀区', '无', '无', '李先生', '13800138004'),
('P20240003', '王五', 1, 45, '110101197903031234', '13800138005', '上海市浦东新区', '糖尿病', '海鲜过敏', '王夫人', '13800138006');
