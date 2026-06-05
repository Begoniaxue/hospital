USE hospital_admin;

-- 插入医生管理菜单（门诊管理下的子菜单）
INSERT INTO sys_permission (name, code, type, parent_id, path, component, icon, sort, status, create_time, update_time) VALUES 
('医生管理', 'outpatient:doctor', 1, 114, '/outpatient/doctor', 'outpatient/doctor/index', 'UserFilled', 2, 1, NOW(), NOW());
SET @doctor_id = LAST_INSERT_ID();

-- 插入医生管理的按钮权限
INSERT INTO sys_permission (name, code, type, parent_id, path, component, icon, sort, status, create_time, update_time) VALUES 
('医生查询', 'outpatient:doctor:query', 2, @doctor_id, NULL, NULL, NULL, 1, 1, NOW(), NOW()),
('医生新增', 'outpatient:doctor:add', 2, @doctor_id, NULL, NULL, NULL, 2, 1, NOW(), NOW()),
('医生编辑', 'outpatient:doctor:edit', 2, @doctor_id, NULL, NULL, NULL, 3, 1, NOW(), NOW()),
('医生删除', 'outpatient:doctor:delete', 2, @doctor_id, NULL, NULL, NULL, 4, 1, NOW(), NOW()),
('排班管理', 'outpatient:doctor:schedule', 2, @doctor_id, NULL, NULL, NULL, 5, 1, NOW(), NOW());

-- 查询插入的结果
SELECT id, name, code, type, parent_id, path, component, icon, sort FROM sys_permission WHERE id = @doctor_id OR parent_id = @doctor_id ORDER BY sort;
