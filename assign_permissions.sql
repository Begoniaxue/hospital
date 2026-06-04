INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE code LIKE 'outpatient%' OR code IN ('pharmacy', 'settlement', 'inpatient', 'report');
