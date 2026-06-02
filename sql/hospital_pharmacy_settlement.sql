USE hospital_admin;

-- 药品档案表
CREATE TABLE drug (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    drug_code VARCHAR(30) UNIQUE COMMENT '药品编码',
    name VARCHAR(100) NOT NULL COMMENT '药品名称',
    specification VARCHAR(100) COMMENT '规格',
    unit VARCHAR(20) COMMENT '单位',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    stock INT DEFAULT 0 COMMENT '库存数量',
    stock_threshold INT DEFAULT 10 COMMENT '库存预警阈值',
    manufacturer VARCHAR(200) COMMENT '生产厂家',
    category VARCHAR(50) COMMENT '药品分类',
    expiry_date DATE COMMENT '有效期',
    status TINYINT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品档案表';

-- 药品库存流水表
CREATE TABLE drug_stock_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    drug_id BIGINT NOT NULL COMMENT '药品ID',
    type TINYINT NOT NULL COMMENT '类型 1:入库 2:出库',
    quantity INT NOT NULL COMMENT '数量',
    before_stock INT COMMENT '变动前库存',
    after_stock INT COMMENT '变动后库存',
    reason VARCHAR(255) COMMENT '原因',
    related_id BIGINT COMMENT '关联ID（处方ID等）',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品库存流水表';

-- 处方表
CREATE TABLE prescription (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    prescription_no VARCHAR(30) UNIQUE COMMENT '处方编号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    doctor_id BIGINT COMMENT '医生ID',
    doctor_name VARCHAR(50) COMMENT '医生姓名',
    diagnosis VARCHAR(255) COMMENT '诊断',
    status TINYINT DEFAULT 0 COMMENT '状态 0:待审核 1:已审核 2:已调配 3:已发药 4:已取消',
    audit_user_id BIGINT COMMENT '审核人ID',
    audit_user_name VARCHAR(50) COMMENT '审核人姓名',
    audit_time DATETIME COMMENT '审核时间',
    dispense_user_id BIGINT COMMENT '调配人ID',
    dispense_user_name VARCHAR(50) COMMENT '调配人姓名',
    dispense_time DATETIME COMMENT '调配时间',
    issue_user_id BIGINT COMMENT '发药人ID',
    issue_user_name VARCHAR(50) COMMENT '发药人姓名',
    issue_time DATETIME COMMENT '发药时间',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方表';

-- 处方明细表
CREATE TABLE prescription_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    prescription_id BIGINT NOT NULL COMMENT '处方ID',
    drug_id BIGINT NOT NULL COMMENT '药品ID',
    drug_name VARCHAR(100) COMMENT '药品名称',
    specification VARCHAR(100) COMMENT '规格',
    quantity INT NOT NULL COMMENT '数量',
    unit VARCHAR(20) COMMENT '单位',
    price DECIMAL(10,2) COMMENT '单价',
    amount DECIMAL(10,2) COMMENT '金额',
    dosage VARCHAR(100) COMMENT '用法用量',
    frequency VARCHAR(50) COMMENT '用药频率',
    days INT COMMENT '用药天数',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方明细表';

-- 收费结算表
CREATE TABLE settlement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    settlement_no VARCHAR(30) UNIQUE COMMENT '结算单号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    prescription_id BIGINT COMMENT '关联处方ID',
    registration_fee DECIMAL(10,2) DEFAULT 0 COMMENT '挂号费',
    drug_fee DECIMAL(10,2) DEFAULT 0 COMMENT '药品费',
    exam_fee DECIMAL(10,2) DEFAULT 0 COMMENT '检查费',
    other_fee DECIMAL(10,2) DEFAULT 0 COMMENT '其他费用',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '合计金额',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    actual_amount DECIMAL(10,2) NOT NULL COMMENT '实收金额',
    pay_method TINYINT COMMENT '支付方式 1:现金 2:微信 3:支付宝 4:医保',
    status TINYINT DEFAULT 0 COMMENT '状态 0:未收费 1:已收费 2:已退费 3:部分退费',
    operator_id BIGINT COMMENT '收费人ID',
    operator_name VARCHAR(50) COMMENT '收费人姓名',
    settle_time DATETIME COMMENT '收费时间',
    invoice_no VARCHAR(30) COMMENT '发票号',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收费结算表';

-- 收费明细表
CREATE TABLE settlement_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    settlement_id BIGINT NOT NULL COMMENT '结算ID',
    type TINYINT NOT NULL COMMENT '费用类型 1:挂号费 2:药品费 3:检查费 4:其他',
    name VARCHAR(100) NOT NULL COMMENT '项目名称',
    quantity INT DEFAULT 1 COMMENT '数量',
    price DECIMAL(10,2) COMMENT '单价',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收费明细表';

-- 退费申请表
CREATE TABLE refund_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    refund_no VARCHAR(30) UNIQUE COMMENT '退费单号',
    settlement_id BIGINT NOT NULL COMMENT '原结算ID',
    settlement_no VARCHAR(30) COMMENT '原结算单号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    refund_amount DECIMAL(10,2) NOT NULL COMMENT '退费金额',
    reason VARCHAR(500) NOT NULL COMMENT '退费原因',
    status TINYINT DEFAULT 0 COMMENT '状态 0:待审核 1:已通过 2:已拒绝',
    apply_user_id BIGINT COMMENT '申请人ID',
    apply_user_name VARCHAR(50) COMMENT '申请人姓名',
    apply_time DATETIME COMMENT '申请时间',
    audit_user_id BIGINT COMMENT '审核人ID',
    audit_user_name VARCHAR(50) COMMENT '审核人姓名',
    audit_time DATETIME COMMENT '审核时间',
    audit_remark VARCHAR(500) COMMENT '审核意见',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退费申请表';

-- 插入药房管理权限
INSERT INTO sys_permission (parent_id, name, code, type, path, component, icon, sort) VALUES
(0, '药房管理', 'pharmacy', 1, '/pharmacy', NULL, 'FirstAidKit', 4),
(0, '收费结算', 'settlement', 1, '/settlement', NULL, 'Money', 5);

SET @pharmacy_id = (SELECT id FROM sys_permission WHERE code = 'pharmacy');
SET @settlement_id = (SELECT id FROM sys_permission WHERE code = 'settlement');

INSERT INTO sys_permission (parent_id, name, code, type, path, component, icon, sort) VALUES
(@pharmacy_id, '药品档案', 'pharmacy:drug', 1, '/pharmacy/drug', 'pharmacy/drug/index', 'Box', 1),
(@pharmacy_id, '库存管理', 'pharmacy:stock', 1, '/pharmacy/stock', 'pharmacy/stock/index', 'Goods', 2),
(@pharmacy_id, '处方审核', 'pharmacy:prescription', 1, '/pharmacy/prescription', 'pharmacy/prescription/index', 'Document', 3),
(@settlement_id, '收费列表', 'settlement:list', 1, '/settlement/list', 'settlement/list/index', 'List', 1),
(@settlement_id, '退费管理', 'settlement:refund', 1, '/settlement/refund', 'settlement/refund/index', 'RefreshLeft', 2),
(@settlement_id, '结算报表', 'settlement:report', 1, '/settlement/report', 'settlement/report/index', 'DataAnalysis', 3);

SET @drug_menu_id = (SELECT id FROM sys_permission WHERE code = 'pharmacy:drug');
SET @stock_menu_id = (SELECT id FROM sys_permission WHERE code = 'pharmacy:stock');
SET @prescription_menu_id = (SELECT id FROM sys_permission WHERE code = 'pharmacy:prescription');
SET @settlement_list_id = (SELECT id FROM sys_permission WHERE code = 'settlement:list');
SET @refund_menu_id = (SELECT id FROM sys_permission WHERE code = 'settlement:refund');
SET @report_menu_id = (SELECT id FROM sys_permission WHERE code = 'settlement:report');

INSERT INTO sys_permission (parent_id, name, code, type, path, component, icon, sort) VALUES
(@drug_menu_id, '药品查询', 'pharmacy:drug:query', 2, NULL, NULL, NULL, 1),
(@drug_menu_id, '药品新增', 'pharmacy:drug:add', 2, NULL, NULL, NULL, 2),
(@drug_menu_id, '药品编辑', 'pharmacy:drug:edit', 2, NULL, NULL, NULL, 3),
(@drug_menu_id, '药品删除', 'pharmacy:drug:delete', 2, NULL, NULL, NULL, 4),
(@stock_menu_id, '库存查询', 'pharmacy:stock:query', 2, NULL, NULL, NULL, 1),
(@stock_menu_id, '入库登记', 'pharmacy:stock:in', 2, NULL, NULL, NULL, 2),
(@stock_menu_id, '出库登记', 'pharmacy:stock:out', 2, NULL, NULL, NULL, 3),
(@prescription_menu_id, '处方查询', 'pharmacy:prescription:query', 2, NULL, NULL, NULL, 1),
(@prescription_menu_id, '处方审核', 'pharmacy:prescription:audit', 2, NULL, NULL, NULL, 2),
(@prescription_menu_id, '药品调配', 'pharmacy:prescription:dispense', 2, NULL, NULL, NULL, 3),
(@prescription_menu_id, '发药确认', 'pharmacy:prescription:issue', 2, NULL, NULL, NULL, 4),
(@settlement_list_id, '收费查询', 'settlement:list:query', 2, NULL, NULL, NULL, 1),
(@settlement_list_id, '收费结算', 'settlement:list:charge', 2, NULL, NULL, NULL, 2),
(@settlement_list_id, '打印小票', 'settlement:list:print', 2, NULL, NULL, NULL, 3),
(@refund_menu_id, '退费查询', 'settlement:refund:query', 2, NULL, NULL, NULL, 1),
(@refund_menu_id, '退费申请', 'settlement:refund:apply', 2, NULL, NULL, NULL, 2),
(@refund_menu_id, '退费审核', 'settlement:refund:audit', 2, NULL, NULL, NULL, 3),
(@report_menu_id, '报表查询', 'settlement:report:query', 2, NULL, NULL, NULL, 1);

-- 超级管理员拥有新增权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE id NOT IN (SELECT permission_id FROM sys_role_permission WHERE role_id = 1);

-- 插入测试药品数据
INSERT INTO drug (drug_code, name, specification, unit, price, stock, stock_threshold, manufacturer, category, expiry_date) VALUES
('D20240001', '阿莫西林胶囊', '0.5g*24粒/盒', '盒', 12.50, 200, 20, '华北制药', '抗生素', '2026-06-30'),
('D20240002', '布洛芬缓释胶囊', '0.3g*20粒/盒', '盒', 15.80, 150, 15, '中美史克', '解热镇痛', '2026-12-31'),
('D20240003', '阿司匹林肠溶片', '100mg*30片/盒', '盒', 8.90, 300, 30, '拜耳医药', '解热镇痛', '2026-09-30'),
('D20240004', '硝苯地平控释片', '30mg*7片/盒', '盒', 32.00, 100, 10, '拜耳医药', '降压药', '2026-03-31'),
('D20240005', '二甲双胍片', '0.5g*20片/盒', '盒', 6.50, 250, 25, '中美施贵宝', '降糖药', '2026-08-31'),
('D20240006', '头孢克肟分散片', '0.1g*6片/盒', '盒', 28.00, 80, 10, '广州白云山', '抗生素', '2025-12-31'),
('D20240007', '奥美拉唑肠溶胶囊', '20mg*14粒/盒', '盒', 18.60, 120, 12, '阿斯利康', '消化系统', '2026-05-31'),
('D20240008', '氯雷他定片', '10mg*6片/盒', '盒', 22.50, 90, 10, '上海先灵葆雅', '抗过敏', '2026-10-31');

-- 插入测试处方数据
INSERT INTO prescription (prescription_no, patient_id, patient_name, doctor_id, doctor_name, diagnosis, status) VALUES
('RX20240001', 1, '张三', 2, '医生', '上呼吸道感染', 0),
('RX20240002', 2, '李四', 2, '医生', '过敏性鼻炎', 1);

INSERT INTO prescription_item (prescription_id, drug_id, drug_name, specification, quantity, unit, price, amount, dosage, frequency, days) VALUES
(1, 1, '阿莫西林胶囊', '0.5g*24粒/盒', 2, '盒', 12.50, 25.00, '每次0.5g', '每日3次', 7),
(1, 2, '布洛芬缓释胶囊', '0.3g*20粒/盒', 1, '盒', 15.80, 15.80, '每次0.3g', '每日2次', 3),
(2, 8, '氯雷他定片', '10mg*6片/盒', 2, '盒', 22.50, 45.00, '每次10mg', '每日1次', 12);

-- 插入测试收费数据
INSERT INTO settlement (settlement_no, patient_id, patient_name, prescription_id, registration_fee, drug_fee, exam_fee, other_fee, total_amount, actual_amount, pay_method, status, operator_id, operator_name, settle_time, invoice_no) VALUES
('S20240001', 2, '李四', 2, 10.00, 45.00, 0, 0, 55.00, 55.00, 1, 1, 1, '系统管理员', '2024-06-15 10:30:00', 'INV20240001');

INSERT INTO settlement_item (settlement_id, type, name, quantity, price, amount) VALUES
(1, 1, '挂号费', 1, 10.00, 10.00),
(1, 2, '氯雷他定片', 2, 22.50, 45.00);
