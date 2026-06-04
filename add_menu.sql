INSERT INTO sys_permission (name, code, type, parent_id, path, component, icon, sort, status, create_time, update_time) VALUES 
('门诊管理', 'outpatient', 1, 0, '/outpatient', NULL, 'medkit', 4, 1, NOW(), NOW());
SET @outpatient_id = LAST_INSERT_ID();

INSERT INTO sys_permission (name, code, type, parent_id, path, component, icon, sort, status, create_time, update_time) VALUES 
('挂号订单', 'outpatient:registration', 1, @outpatient_id, '/outpatient/registration', 'outpatient/registration/index', 'tickets', 1, 1, NOW(), NOW());
SET @reg_id = LAST_INSERT_ID();

INSERT INTO sys_permission (name, code, type, parent_id, path, component, icon, sort, status, create_time, update_time) VALUES 
('挂号订单查询', 'outpatient:registration:query', 2, @reg_id, NULL, NULL, NULL, 1, 1, NOW(), NOW()),
('挂号订单新增', 'outpatient:registration:add', 2, @reg_id, NULL, NULL, NULL, 2, 1, NOW(), NOW()),
('挂号订单编辑', 'outpatient:registration:edit', 2, @reg_id, NULL, NULL, NULL, 3, 1, NOW(), NOW()),
('挂号订单删除', 'outpatient:registration:delete', 2, @reg_id, NULL, NULL, NULL, 4, 1, NOW(), NOW());

INSERT INTO sys_permission (name, code, type, parent_id, path, component, icon, sort, status, create_time, update_time) VALUES 
('药房管理', 'pharmacy', 1, 0, '/pharmacy', NULL, 'medicine-box', 5, 1, NOW(), NOW()),
('收费结算', 'settlement', 1, 0, '/settlement', NULL, 'wallet', 6, 1, NOW(), NOW()),
('住院管理', 'inpatient', 1, 0, '/inpatient', NULL, 'office-building', 7, 1, NOW(), NOW()),
('数据统计', 'report', 1, 0, '/report', NULL, 'data-analysis', 8, 1, NOW(), NOW());
