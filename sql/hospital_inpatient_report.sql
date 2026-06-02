USE hospital_admin;

-- 病房表
CREATE TABLE IF NOT EXISTS ward (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    ward_no VARCHAR(30) UNIQUE COMMENT '病房编号',
    ward_name VARCHAR(50) NOT NULL COMMENT '病房名称',
    floor INT COMMENT '楼层',
    department VARCHAR(50) COMMENT '所属科室',
    bed_count INT DEFAULT 0 COMMENT '床位数量',
    status TINYINT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病房表';

-- 床位表
CREATE TABLE IF NOT EXISTS bed (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    bed_no VARCHAR(30) UNIQUE NOT NULL COMMENT '床位编号',
    ward_id BIGINT NOT NULL COMMENT '病房ID',
    ward_name VARCHAR(50) COMMENT '病房名称',
    bed_type TINYINT DEFAULT 1 COMMENT '床位类型 1:普通 2:重症 3:VIP',
    price DECIMAL(10,2) NOT NULL COMMENT '床位单价/天',
    status TINYINT DEFAULT 0 COMMENT '状态 0:空闲 1:已占用 2:维修中',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='床位表';

-- 住院登记表
CREATE TABLE IF NOT EXISTS inpatient (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    inpatient_no VARCHAR(30) UNIQUE NOT NULL COMMENT '住院号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    patient_gender TINYINT COMMENT '患者性别',
    patient_age INT COMMENT '患者年龄',
    admission_date DATETIME COMMENT '入院日期',
    expected_days INT COMMENT '预计住院天数',
    department VARCHAR(50) COMMENT '入住科室',
    doctor_id BIGINT COMMENT '主治医生ID',
    doctor_name VARCHAR(50) COMMENT '主治医生姓名',
    bed_id BIGINT COMMENT '床位ID',
    bed_no VARCHAR(30) COMMENT '床位编号',
    ward_id BIGINT COMMENT '病房ID',
    ward_name VARCHAR(50) COMMENT '病房名称',
    diagnosis VARCHAR(255) COMMENT '入院诊断',
    admission_type TINYINT DEFAULT 1 COMMENT '入院类型 1:普通入院 2:急诊入院 3:转院',
    status TINYINT DEFAULT 1 COMMENT '状态 0:待入院 1:住院中 2:已出院 3:已取消',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='住院登记表';

-- 押金表
CREATE TABLE IF NOT EXISTS inpatient_deposit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    deposit_no VARCHAR(30) UNIQUE NOT NULL COMMENT '押金单号',
    inpatient_id BIGINT NOT NULL COMMENT '住院ID',
    inpatient_no VARCHAR(30) COMMENT '住院号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    amount DECIMAL(10,2) NOT NULL COMMENT '押金金额',
    pay_method TINYINT COMMENT '支付方式 1:现金 2:微信 3:支付宝 4:医保',
    pay_time DATETIME COMMENT '支付时间',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    status TINYINT DEFAULT 1 COMMENT '状态 0:已退款 1:有效',
    refund_time DATETIME COMMENT '退款时间',
    refund_operator_id BIGINT COMMENT '退款人ID',
    refund_operator_name VARCHAR(50) COMMENT '退款人姓名',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='住院押金表';

-- 住院医嘱表
CREATE TABLE IF NOT EXISTS inpatient_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(30) UNIQUE NOT NULL COMMENT '医嘱单号',
    inpatient_id BIGINT NOT NULL COMMENT '住院ID',
    inpatient_no VARCHAR(30) COMMENT '住院号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    doctor_id BIGINT COMMENT '医生ID',
    doctor_name VARCHAR(50) COMMENT '医生姓名',
    order_type TINYINT NOT NULL COMMENT '医嘱类型 1:长期医嘱 2:临时医嘱',
    order_content TEXT NOT NULL COMMENT '医嘱内容',
    order_time DATETIME COMMENT '医嘱时间',
    execute_time DATETIME COMMENT '执行时间',
    execute_user_id BIGINT COMMENT '执行人ID',
    execute_user_name VARCHAR(50) COMMENT '执行人姓名',
    status TINYINT DEFAULT 0 COMMENT '状态 0:待执行 1:执行中 2:已执行 3:已取消',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='住院医嘱表';

-- 病程记录表
CREATE TABLE IF NOT EXISTS inpatient_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    inpatient_id BIGINT NOT NULL COMMENT '住院ID',
    inpatient_no VARCHAR(30) COMMENT '住院号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    record_date DATE NOT NULL COMMENT '记录日期',
    record_type TINYINT DEFAULT 1 COMMENT '记录类型 1:日常病程 2:首次病程 3:上级医师查房 4:术前讨论 5:术后记录',
    title VARCHAR(200) COMMENT '标题',
    content TEXT NOT NULL COMMENT '病程内容',
    vital_signs VARCHAR(255) COMMENT '生命体征（体温、脉搏、呼吸、血压）',
    doctor_id BIGINT COMMENT '记录医生ID',
    doctor_name VARCHAR(50) COMMENT '记录医生姓名',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病程记录表';

-- 住院费用明细表
CREATE TABLE IF NOT EXISTS inpatient_fee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    fee_no VARCHAR(30) UNIQUE NOT NULL COMMENT '费用单号',
    inpatient_id BIGINT NOT NULL COMMENT '住院ID',
    inpatient_no VARCHAR(30) COMMENT '住院号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    fee_date DATE NOT NULL COMMENT '费用日期',
    fee_type TINYINT NOT NULL COMMENT '费用类型 1:床位费 2:诊疗费 3:检查费 4:化验费 5:药品费 6:手术费 7:护理费 8:其他',
    fee_name VARCHAR(100) NOT NULL COMMENT '费用名称',
    quantity INT DEFAULT 1 COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    related_id BIGINT COMMENT '关联ID（处方ID、检查单ID等）',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    status TINYINT DEFAULT 1 COMMENT '状态 0:已退费 1:正常',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='住院费用明细表';

-- 出院结算表
CREATE TABLE IF NOT EXISTS inpatient_discharge (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    discharge_no VARCHAR(30) UNIQUE NOT NULL COMMENT '出院结算单号',
    inpatient_id BIGINT NOT NULL COMMENT '住院ID',
    inpatient_no VARCHAR(30) COMMENT '住院号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    admission_date DATETIME COMMENT '入院日期',
    discharge_date DATETIME COMMENT '出院日期',
    actual_days INT COMMENT '实际住院天数',
    total_amount DECIMAL(10,2) DEFAULT 0 COMMENT '总费用',
    deposit_amount DECIMAL(10,2) DEFAULT 0 COMMENT '押金总额',
    refund_amount DECIMAL(10,2) DEFAULT 0 COMMENT '应退金额',
    supplement_amount DECIMAL(10,2) DEFAULT 0 COMMENT '补交金额',
    actual_pay DECIMAL(10,2) DEFAULT 0 COMMENT '实际结算金额',
    pay_method TINYINT COMMENT '支付方式 1:现金 2:微信 3:支付宝 4:医保',
    pay_time DATETIME COMMENT '结算时间',
    discharge_diagnosis VARCHAR(255) COMMENT '出院诊断',
    discharge_result TINYINT DEFAULT 1 COMMENT '出院结果 1:治愈 2:好转 3:未愈 4:转院 5:死亡',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    status TINYINT DEFAULT 1 COMMENT '状态 0:未结算 1:已结算',
    invoice_no VARCHAR(30) COMMENT '发票号',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出院结算表';

-- 出院小结表
CREATE TABLE IF NOT EXISTS discharge_summary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    inpatient_id BIGINT NOT NULL COMMENT '住院ID',
    inpatient_no VARCHAR(30) COMMENT '住院号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    admission_date DATETIME COMMENT '入院日期',
    discharge_date DATETIME COMMENT '出院日期',
    admission_diagnosis VARCHAR(255) COMMENT '入院诊断',
    discharge_diagnosis VARCHAR(255) COMMENT '出院诊断',
    hospitalization_summary TEXT COMMENT '住院经过',
    examination_summary TEXT COMMENT '主要检查结果',
    treatment_summary TEXT COMMENT '治疗经过',
    discharge_advice TEXT COMMENT '出院医嘱',
    follow_up_advice TEXT COMMENT '随访建议',
    doctor_id BIGINT COMMENT '主治医生ID',
    doctor_name VARCHAR(50) COMMENT '主治医生姓名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出院小结表';

-- 住院档案表
CREATE TABLE IF NOT EXISTS inpatient_archive (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    archive_no VARCHAR(30) UNIQUE NOT NULL COMMENT '档案编号',
    inpatient_id BIGINT NOT NULL COMMENT '住院ID',
    inpatient_no VARCHAR(30) COMMENT '住院号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    archive_status TINYINT DEFAULT 0 COMMENT '档案状态 0:待归档 1:已归档 2:已借阅',
    archive_time DATETIME COMMENT '归档时间',
    archive_user_id BIGINT COMMENT '归档人ID',
    archive_user_name VARCHAR(50) COMMENT '归档人姓名',
    borrow_user_id BIGINT COMMENT '借阅人ID',
    borrow_user_name VARCHAR(50) COMMENT '借阅人姓名',
    borrow_time DATETIME COMMENT '借阅时间',
    expected_return_time DATETIME COMMENT '预计归还时间',
    actual_return_time DATETIME COMMENT '实际归还时间',
    file_path VARCHAR(255) COMMENT '档案文件路径',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='住院档案表';

-- 挂号表（用于统计）
CREATE TABLE IF NOT EXISTS registration (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    registration_no VARCHAR(30) UNIQUE NOT NULL COMMENT '挂号单号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    patient_gender TINYINT COMMENT '性别',
    patient_age INT COMMENT '年龄',
    department VARCHAR(50) NOT NULL COMMENT '科室',
    doctor_id BIGINT COMMENT '医生ID',
    doctor_name VARCHAR(50) COMMENT '医生姓名',
    registration_type TINYINT DEFAULT 1 COMMENT '挂号类型 1:普通号 2:专家号 3:急诊号',
    registration_fee DECIMAL(10,2) DEFAULT 0 COMMENT '挂号费',
    pay_method TINYINT COMMENT '支付方式',
    pay_time DATETIME COMMENT '支付时间',
    visit_date DATE NOT NULL COMMENT '就诊日期',
    visit_time_slot VARCHAR(20) COMMENT '就诊时段',
    status TINYINT DEFAULT 1 COMMENT '状态 0:已取消 1:待就诊 2:已就诊 3:已过期',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='挂号表';

-- 门诊记录表（用于统计）
CREATE TABLE IF NOT EXISTS outpatient_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    record_no VARCHAR(30) UNIQUE NOT NULL COMMENT '门诊记录编号',
    registration_id BIGINT COMMENT '挂号ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    department VARCHAR(50) NOT NULL COMMENT '科室',
    doctor_id BIGINT COMMENT '医生ID',
    doctor_name VARCHAR(50) COMMENT '医生姓名',
    visit_date DATE NOT NULL COMMENT '就诊日期',
    chief_complaint VARCHAR(255) COMMENT '主诉',
    present_illness TEXT COMMENT '现病史',
    physical_examination TEXT COMMENT '体格检查',
    diagnosis VARCHAR(255) COMMENT '诊断',
    treatment_plan TEXT COMMENT '治疗方案',
    prescription_id BIGINT COMMENT '处方ID',
    total_amount DECIMAL(10,2) DEFAULT 0 COMMENT '诊疗总费用',
    status TINYINT DEFAULT 1 COMMENT '状态 0:未完成 1:已完成',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门诊记录表';

-- 插入住院管理和数据统计菜单
INSERT INTO sys_permission (parent_id, name, code, type, path, component, icon, sort) VALUES
(0, '住院管理', 'inpatient', 1, '/inpatient', NULL, 'Notebook', 6),
(0, '数据统计', 'report', 1, '/report', NULL, 'DataLine', 7);

SET @inpatient_parent_id = (SELECT id FROM sys_permission WHERE code = 'inpatient');
SET @report_parent_id = (SELECT id FROM sys_permission WHERE code = 'report');

-- 住院管理子菜单
INSERT INTO sys_permission (parent_id, name, code, type, path, component, icon, sort) VALUES
(@inpatient_parent_id, '入院登记', 'inpatient:admission', 1, '/inpatient/admission', 'inpatient/admission/index', 'EditPen', 1),
(@inpatient_parent_id, '床位分配', 'inpatient:bed', 1, '/inpatient/bed', 'inpatient/bed/index', 'OfficeBuilding', 2),
(@inpatient_parent_id, '押金管理', 'inpatient:deposit', 1, '/inpatient/deposit', 'inpatient/deposit/index', 'Wallet', 3),
(@inpatient_parent_id, '住院医嘱', 'inpatient:order', 1, '/inpatient/order', 'inpatient/order/index', 'Document', 4),
(@inpatient_parent_id, '病程记录', 'inpatient:record', 1, '/inpatient/record', 'inpatient/record/index', 'Reading', 5),
(@inpatient_parent_id, '费用汇总', 'inpatient:fee', 1, '/inpatient/fee', 'inpatient/fee/index', 'Calculator', 6),
(@inpatient_parent_id, '出院结算', 'inpatient:discharge', 1, '/inpatient/discharge', 'inpatient/discharge/index', 'Tickets', 7),
(@inpatient_parent_id, '住院档案', 'inpatient:archive', 1, '/inpatient/archive', 'inpatient/archive/index', 'Folder', 8);

-- 数据统计子菜单
INSERT INTO sys_permission (parent_id, name, code, type, path, component, icon, sort) VALUES
(@report_parent_id, '挂号量统计', 'report:registration', 1, '/report/registration', 'report/registration/index', 'Histogram', 1),
(@report_parent_id, '门诊量统计', 'report:outpatient', 1, '/report/outpatient', 'report/outpatient/index', 'DataBoard', 2),
(@report_parent_id, '医生接诊量', 'report:doctor', 1, '/report/doctor', 'report/doctor/index', 'UserFilled', 3),
(@report_parent_id, '营收统计', 'report:revenue', 1, '/report/revenue', 'report/revenue/index', 'Money', 4),
(@report_parent_id, '药品进销存', 'report:drug', 1, '/report/drug', 'report/drug/index', 'Box', 5),
(@report_parent_id, '就诊趋势图', 'report:trend', 1, '/report/trend', 'report/trend/index', 'TrendCharts', 6);

-- 获取各子菜单ID
SET @admission_id = (SELECT id FROM sys_permission WHERE code = 'inpatient:admission');
SET @bed_id = (SELECT id FROM sys_permission WHERE code = 'inpatient:bed');
SET @deposit_id = (SELECT id FROM sys_permission WHERE code = 'inpatient:deposit');
SET @order_id = (SELECT id FROM sys_permission WHERE code = 'inpatient:order');
SET @record_id = (SELECT id FROM sys_permission WHERE code = 'inpatient:record');
SET @fee_id = (SELECT id FROM sys_permission WHERE code = 'inpatient:fee');
SET @discharge_id = (SELECT id FROM sys_permission WHERE code = 'inpatient:discharge');
SET @archive_id = (SELECT id FROM sys_permission WHERE code = 'inpatient:archive');
SET @reg_report_id = (SELECT id FROM sys_permission WHERE code = 'report:registration');
SET @out_report_id = (SELECT id FROM sys_permission WHERE code = 'report:outpatient');
SET @doc_report_id = (SELECT id FROM sys_permission WHERE code = 'report:doctor');
SET @rev_report_id = (SELECT id FROM sys_permission WHERE code = 'report:revenue');
SET @drug_report_id = (SELECT id FROM sys_permission WHERE code = 'report:drug');
SET @trend_report_id = (SELECT id FROM sys_permission WHERE code = 'report:trend');

-- 插入按钮权限
INSERT INTO sys_permission (parent_id, name, code, type, path, component, icon, sort) VALUES
-- 入院登记按钮
(@admission_id, '查询', 'inpatient:admission:query', 2, NULL, NULL, NULL, 1),
(@admission_id, '新增', 'inpatient:admission:add', 2, NULL, NULL, NULL, 2),
(@admission_id, '编辑', 'inpatient:admission:edit', 2, NULL, NULL, NULL, 3),
(@admission_id, '删除', 'inpatient:admission:delete', 2, NULL, NULL, NULL, 4),
-- 床位分配按钮
(@bed_id, '查询', 'inpatient:bed:query', 2, NULL, NULL, NULL, 1),
(@bed_id, '新增', 'inpatient:bed:add', 2, NULL, NULL, NULL, 2),
(@bed_id, '编辑', 'inpatient:bed:edit', 2, NULL, NULL, NULL, 3),
(@bed_id, '分配', 'inpatient:bed:assign', 2, NULL, NULL, NULL, 4),
-- 押金管理按钮
(@deposit_id, '查询', 'inpatient:deposit:query', 2, NULL, NULL, NULL, 1),
(@deposit_id, '缴费', 'inpatient:deposit:pay', 2, NULL, NULL, NULL, 2),
(@deposit_id, '退款', 'inpatient:deposit:refund', 2, NULL, NULL, NULL, 3),
-- 住院医嘱按钮
(@order_id, '查询', 'inpatient:order:query', 2, NULL, NULL, NULL, 1),
(@order_id, '新增', 'inpatient:order:add', 2, NULL, NULL, NULL, 2),
(@order_id, '执行', 'inpatient:order:execute', 2, NULL, NULL, NULL, 3),
(@order_id, '取消', 'inpatient:order:cancel', 2, NULL, NULL, NULL, 4),
-- 病程记录按钮
(@record_id, '查询', 'inpatient:record:query', 2, NULL, NULL, NULL, 1),
(@record_id, '新增', 'inpatient:record:add', 2, NULL, NULL, NULL, 2),
(@record_id, '编辑', 'inpatient:record:edit', 2, NULL, NULL, NULL, 3),
-- 费用汇总按钮
(@fee_id, '查询', 'inpatient:fee:query', 2, NULL, NULL, NULL, 1),
(@fee_id, '新增', 'inpatient:fee:add', 2, NULL, NULL, NULL, 2),
(@fee_id, '导出', 'inpatient:fee:export', 2, NULL, NULL, NULL, 3),
-- 出院结算按钮
(@discharge_id, '查询', 'inpatient:discharge:query', 2, NULL, NULL, NULL, 1),
(@discharge_id, '结算', 'inpatient:discharge:settle', 2, NULL, NULL, NULL, 2),
(@discharge_id, '打印', 'inpatient:discharge:print', 2, NULL, NULL, NULL, 3),
-- 住院档案按钮
(@archive_id, '查询', 'inpatient:archive:query', 2, NULL, NULL, NULL, 1),
(@archive_id, '归档', 'inpatient:archive:file', 2, NULL, NULL, NULL, 2),
(@archive_id, '借阅', 'inpatient:archive:borrow', 2, NULL, NULL, NULL, 3),
-- 报表按钮
(@reg_report_id, '查询', 'report:registration:query', 2, NULL, NULL, NULL, 1),
(@reg_report_id, '导出', 'report:registration:export', 2, NULL, NULL, NULL, 2),
(@reg_report_id, '打印', 'report:registration:print', 2, NULL, NULL, NULL, 3),
(@out_report_id, '查询', 'report:outpatient:query', 2, NULL, NULL, NULL, 1),
(@out_report_id, '导出', 'report:outpatient:export', 2, NULL, NULL, NULL, 2),
(@out_report_id, '打印', 'report:outpatient:print', 2, NULL, NULL, NULL, 3),
(@doc_report_id, '查询', 'report:doctor:query', 2, NULL, NULL, NULL, 1),
(@doc_report_id, '导出', 'report:doctor:export', 2, NULL, NULL, NULL, 2),
(@doc_report_id, '打印', 'report:doctor:print', 2, NULL, NULL, NULL, 3),
(@rev_report_id, '查询', 'report:revenue:query', 2, NULL, NULL, NULL, 1),
(@rev_report_id, '导出', 'report:revenue:export', 2, NULL, NULL, NULL, 2),
(@rev_report_id, '打印', 'report:revenue:print', 2, NULL, NULL, NULL, 3),
(@drug_report_id, '查询', 'report:drug:query', 2, NULL, NULL, NULL, 1),
(@drug_report_id, '导出', 'report:drug:export', 2, NULL, NULL, NULL, 2),
(@drug_report_id, '打印', 'report:drug:print', 2, NULL, NULL, NULL, 3),
(@trend_report_id, '查询', 'report:trend:query', 2, NULL, NULL, NULL, 1),
(@trend_report_id, '导出', 'report:trend:export', 2, NULL, NULL, NULL, 2),
(@trend_report_id, '打印', 'report:trend:print', 2, NULL, NULL, NULL, 3);

-- 超级管理员拥有所有新增权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE id NOT IN (SELECT permission_id FROM sys_role_permission WHERE role_id = 1);

-- 插入测试病房数据
INSERT INTO ward (ward_no, ward_name, floor, department, bed_count, status) VALUES
('W001', '内科一病房', 3, '内科', 20, 1),
('W002', '内科二病房', 3, '内科', 20, 1),
('W003', '外科一病房', 4, '外科', 25, 1),
('W004', '外科二病房', 4, '外科', 25, 1),
('W005', '儿科病房', 5, '儿科', 15, 1),
('W006', 'ICU病房', 2, '重症医学科', 10, 1);

-- 插入测试床位数据
INSERT INTO bed (bed_no, ward_id, ward_name, bed_type, price, status) VALUES
('W001-01', 1, '内科一病房', 1, 50.00, 0),
('W001-02', 1, '内科一病房', 1, 50.00, 0),
('W001-03', 1, '内科一病房', 1, 50.00, 1),
('W001-04', 1, '内科一病房', 1, 50.00, 0),
('W001-05', 1, '内科一病房', 1, 50.00, 0),
('W002-01', 2, '内科二病房', 1, 50.00, 0),
('W002-02', 2, '内科二病房', 1, 50.00, 0),
('W003-01', 3, '外科一病房', 1, 60.00, 0),
('W003-02', 3, '外科一病房', 1, 60.00, 0),
('W006-01', 6, 'ICU病房', 2, 200.00, 0);

-- 插入测试住院数据
INSERT INTO inpatient (inpatient_no, patient_id, patient_name, patient_gender, patient_age, admission_date, expected_days, department, doctor_id, doctor_name, bed_id, bed_no, ward_id, ward_name, diagnosis, status) VALUES
('ZY202406001', 1, '张三', 1, 35, '2024-06-10 09:00:00', 7, '内科', 2, '医生', 3, 'W001-03', 1, '内科一病房', '高血压3级', 1),
('ZY202406002', 2, '李四', 0, 28, '2024-06-12 14:30:00', 5, '内科', 2, '医生', NULL, NULL, NULL, NULL, '急性支气管炎', 0),
('ZY202406003', 3, '王五', 1, 45, '2024-06-08 10:00:00', 10, '外科', 2, '医生', NULL, NULL, NULL, NULL, '2型糖尿病伴并发症', 1);

-- 插入测试押金数据
INSERT INTO inpatient_deposit (deposit_no, inpatient_id, inpatient_no, patient_id, patient_name, amount, pay_method, pay_time, operator_id, operator_name, status) VALUES
('YJ202406001', 1, 'ZY202406001', 1, '张三', 5000.00, 2, '2024-06-10 09:30:00', 1, '系统管理员', 1),
('YJ202406002', 3, 'ZY202406003', 3, '王五', 8000.00, 1, '2024-06-08 10:30:00', 1, '系统管理员', 1);

-- 插入测试医嘱数据
INSERT INTO inpatient_order (order_no, inpatient_id, inpatient_no, patient_id, patient_name, doctor_id, doctor_name, order_type, order_content, order_time, status) VALUES
('YZ202406001', 1, 'ZY202406001', 1, '张三', 2, '医生', 1, '硝苯地平控释片 30mg 口服 每日一次', '2024-06-10 10:00:00', 2),
('YZ202406002', 1, 'ZY202406001', 1, '张三', 2, '医生', 1, '监测血压 每日两次', '2024-06-10 10:00:00', 1),
('YZ202406003', 3, 'ZY202406003', 3, '王五', 2, '医生', 1, '二甲双胍片 0.5g 口服 每日三次', '2024-06-08 11:00:00', 2);

-- 插入测试病程记录
INSERT INTO inpatient_record (inpatient_id, inpatient_no, patient_id, patient_name, record_date, record_type, title, content, vital_signs, doctor_id, doctor_name) VALUES
(1, 'ZY202406001', 1, '张三', '2024-06-10', 2, '首次病程记录', '患者因"发现血压升高5年，加重伴头晕3天"入院。查体：BP 180/110mmHg，心率85次/分，律齐。初步诊断：高血压3级（很高危）。', 'T:36.5℃, P:85次/分, R:20次/分, BP:180/110mmHg', 2, '医生'),
(1, 'ZY202406001', 1, '张三', '2024-06-11', 1, '日常病程记录', '患者今日一般情况可，头晕症状较前缓解。查体：BP 150/95mmHg。继续目前降压治疗，监测血压变化。', 'T:36.4℃, P:78次/分, R:19次/分, BP:150/95mmHg', 2, '医生'),
(3, 'ZY202406003', 3, '王五', '2024-06-08', 2, '首次病程记录', '患者因"多饮、多尿、消瘦1年，加重1周"入院。既往糖尿病史。查体：身高175cm，体重70kg，BMI 22.9。随机血糖15.8mmol/L。', 'T:36.6℃, P:82次/分, R:20次/分, BP:135/85mmHg', 2, '医生');

-- 插入测试住院费用
INSERT INTO inpatient_fee (fee_no, inpatient_id, inpatient_no, patient_id, patient_name, fee_date, fee_type, fee_name, quantity, unit_price, amount, operator_id, operator_name) VALUES
('FY20240610001', 1, 'ZY202406001', 1, '张三', '2024-06-10', 1, '床位费', 1, 50.00, 50.00, 1, '系统管理员'),
('FY20240610002', 1, 'ZY202406001', 1, '张三', '2024-06-10', 3, '常规心电图检查', 1, 80.00, 80.00, 1, '系统管理员'),
('FY20240610003', 1, 'ZY202406001', 1, '张三', '2024-06-10', 5, '硝苯地平控释片', 1, 32.00, 32.00, 1, '系统管理员'),
('FY20240611001', 1, 'ZY202406001', 1, '张三', '2024-06-11', 1, '床位费', 1, 50.00, 50.00, 1, '系统管理员'),
('FY20240611002', 1, 'ZY202406001', 1, '张三', '2024-06-11', 7, '护理费', 1, 30.00, 30.00, 1, '系统管理员'),
('FY20240608001', 3, 'ZY202406003', 3, '王五', '2024-06-08', 1, '床位费', 1, 60.00, 60.00, 1, '系统管理员'),
('FY20240608002', 3, 'ZY202406003', 3, '王五', '2024-06-08', 4, '血糖监测', 4, 10.00, 40.00, 1, '系统管理员'),
('FY20240608003', 3, 'ZY202406003', 3, '王五', '2024-06-08', 5, '二甲双胍片', 2, 6.50, 13.00, 1, '系统管理员');

-- 插入测试挂号数据
INSERT INTO registration (registration_no, patient_id, patient_name, patient_gender, patient_age, department, doctor_id, doctor_name, registration_type, registration_fee, pay_method, pay_time, visit_date, visit_time_slot, status) VALUES
('GH20240601001', 1, '张三', 1, 35, '内科', 2, '医生', 1, 10.00, 1, '2024-06-01 08:00:00', '2024-06-01', '上午', 2),
('GH20240601002', 2, '李四', 0, 28, '内科', 2, '医生', 1, 10.00, 2, '2024-06-01 08:10:00', '2024-06-01', '上午', 2),
('GH20240602001', 3, '王五', 1, 45, '内分泌科', 2, '医生', 2, 30.00, 3, '2024-06-02 09:00:00', '2024-06-02', '上午', 2),
('GH20240603001', 1, '张三', 1, 35, '内科', 2, '医生', 1, 10.00, 1, '2024-06-03 08:30:00', '2024-06-03', '上午', 2),
('GH20240605001', 2, '李四', 0, 28, '呼吸内科', 2, '医生', 1, 10.00, 2, '2024-06-05 14:00:00', '2024-06-05', '下午', 2),
('GH20240610001', 1, '张三', 1, 35, '心内科', 2, '医生', 2, 30.00, 1, '2024-06-10 09:00:00', '2024-06-10', '上午', 2),
('GH20240612001', 2, '李四', 0, 28, '内科', 2, '医生', 3, 20.00, 2, '2024-06-12 14:30:00', '2024-06-12', '下午', 1),
('GH20240615001', 3, '王五', 1, 45, '内分泌科', 2, '医生', 2, 30.00, 3, '2024-06-15 10:00:00', '2024-06-15', '上午', 1);

-- 插入测试门诊记录
INSERT INTO outpatient_record (record_no, registration_id, patient_id, patient_name, department, doctor_id, doctor_name, visit_date, chief_complaint, diagnosis, total_amount, status) VALUES
('MZ20240601001', 1, 1, '张三', '内科', 2, '医生', '2024-06-01', '头晕、头痛3天', '高血压2级', 120.50, 1),
('MZ20240601002', 2, 2, '李四', '内科', 2, '医生', '2024-06-01', '咳嗽、咳痰1周', '上呼吸道感染', 85.80, 1),
('MZ20240602001', 3, 3, '王五', '内分泌科', 2, '医生', '2024-06-02', '血糖控制不佳', '2型糖尿病', 210.00, 1),
('MZ20240603001', 4, 1, '张三', '内科', 2, '医生', '2024-06-03', '血压波动', '高血压2级', 95.00, 1),
('MZ20240605001', 5, 2, '李四', '呼吸内科', 2, '医生', '2024-06-05', '咳嗽加重伴发热', '急性支气管炎', 156.50, 1),
('MZ20240610001', 6, 1, '张三', '心内科', 2, '医生', '2024-06-10', '血压显著升高', '高血压3级', 280.00, 1);
