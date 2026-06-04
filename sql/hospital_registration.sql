-- 挂号流程相关表

USE hospital_admin;

-- 科室表
CREATE TABLE IF NOT EXISTS department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID，0表示一级科室',
    dept_code VARCHAR(50) NOT NULL UNIQUE COMMENT '科室编码',
    dept_name VARCHAR(100) NOT NULL COMMENT '科室名称',
    dept_name_pinyin VARCHAR(200) COMMENT '科室名称拼音',
    dept_type TINYINT DEFAULT 1 COMMENT '科室类型 1:临床科室 2:医技科室 3:行政科室',
    dept_level TINYINT DEFAULT 1 COMMENT '科室等级 1:一级 2:二级',
    location VARCHAR(100) COMMENT '科室位置',
    phone VARCHAR(20) COMMENT '科室电话',
    description TEXT COMMENT '科室简介',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    INDEX idx_parent_id (parent_id),
    INDEX idx_dept_name (dept_name),
    INDEX idx_dept_pinyin (dept_name_pinyin)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室表';

-- 医生表
CREATE TABLE IF NOT EXISTS doctor (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    doctor_no VARCHAR(30) UNIQUE COMMENT '医生工号',
    name VARCHAR(50) NOT NULL COMMENT '医生姓名',
    name_pinyin VARCHAR(200) COMMENT '医生姓名拼音',
    gender TINYINT NOT NULL COMMENT '性别 0:女 1:男',
    title VARCHAR(50) COMMENT '职称',
    specialty VARCHAR(500) COMMENT '专业特长',
    department_id BIGINT NOT NULL COMMENT '所属科室ID',
    department_name VARCHAR(100) COMMENT '所属科室名称',
    consultation_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '挂号费',
    introduction TEXT COMMENT '医生简介',
    avatar VARCHAR(255) COMMENT '头像',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0:停用 1:启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    INDEX idx_department_id (department_id),
    INDEX idx_doctor_name (name),
    INDEX idx_doctor_pinyin (name_pinyin)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生表';

-- 医生排班表
CREATE TABLE IF NOT EXISTS doctor_schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    doctor_name VARCHAR(50) COMMENT '医生姓名',
    department_id BIGINT NOT NULL COMMENT '科室ID',
    department_name VARCHAR(100) COMMENT '科室名称',
    schedule_date DATE NOT NULL COMMENT '出诊日期',
    time_slot VARCHAR(20) NOT NULL COMMENT '时段 AM:上午 PM:下午',
    start_time TIME COMMENT '开始时间',
    end_time TIME COMMENT '结束时间',
    max_count INT DEFAULT 30 COMMENT '最大号量',
    remaining_count INT DEFAULT 30 COMMENT '剩余号量',
    registration_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '挂号费',
    is_suspended TINYINT DEFAULT 0 COMMENT '是否停诊 0:否 1:是',
    suspend_reason VARCHAR(255) COMMENT '停诊原因',
    status TINYINT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    UNIQUE KEY uk_doctor_date_slot (doctor_id, schedule_date, time_slot),
    INDEX idx_schedule_date (schedule_date),
    INDEX idx_department_date (department_id, schedule_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生排班表';

-- 症状表
CREATE TABLE IF NOT EXISTS symptom (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    symptom_code VARCHAR(50) UNIQUE COMMENT '症状编码',
    symptom_name VARCHAR(100) NOT NULL COMMENT '症状名称',
    symptom_icon VARCHAR(100) COMMENT '症状图标',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='症状表';

-- 症状科室关联表
CREATE TABLE IF NOT EXISTS symptom_department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    symptom_id BIGINT NOT NULL COMMENT '症状ID',
    department_id BIGINT NOT NULL COMMENT '科室ID',
    match_weight INT DEFAULT 1 COMMENT '匹配权重',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_symptom_dept (symptom_id, department_id),
    INDEX idx_symptom_id (symptom_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='症状科室关联表';

-- 挂号订单表（扩展已有的registration表，添加更多字段）
ALTER TABLE registration 
ADD COLUMN IF NOT EXISTS department_id BIGINT COMMENT '科室ID' AFTER department,
ADD COLUMN IF NOT EXISTS schedule_id BIGINT COMMENT '排班ID' AFTER doctor_id,
ADD COLUMN IF NOT EXISTS visit_location VARCHAR(100) COMMENT '就诊位置' AFTER visit_time_slot,
ADD COLUMN IF NOT EXISTS queue_number INT COMMENT '排队序号' AFTER visit_location,
ADD COLUMN IF NOT EXISTS checkin_time DATETIME COMMENT '签到时间' AFTER queue_number,
ADD COLUMN IF NOT EXISTS consult_time DATETIME COMMENT '就诊时间' AFTER checkin_time,
ADD COLUMN IF NOT EXISTS finish_time DATETIME COMMENT '完成时间' AFTER consult_time,
ADD COLUMN IF NOT EXISTS cancel_time DATETIME COMMENT '取消时间' AFTER finish_time,
ADD COLUMN IF NOT EXISTS cancel_reason VARCHAR(255) COMMENT '取消原因' AFTER cancel_time,
ADD COLUMN IF NOT EXISTS refund_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '退费金额' AFTER cancel_reason,
ADD COLUMN IF NOT EXISTS refund_time DATETIME COMMENT '退费时间' AFTER refund_amount,
ADD COLUMN IF NOT EXISTS medical_insurance_settle TINYINT DEFAULT 0 COMMENT '医保结算 0:否 1:是' AFTER refund_time,
ADD COLUMN IF NOT EXISTS medical_insurance_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '医保支付金额' AFTER medical_insurance_settle,
ADD COLUMN IF NOT EXISTS out_pocket_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '自付金额' AFTER medical_insurance_amount,
ADD COLUMN IF NOT EXISTS his_registration_no VARCHAR(50) COMMENT 'HIS挂号单号' AFTER out_pocket_amount,
ADD COLUMN IF NOT EXISTS is_notified TINYINT DEFAULT 0 COMMENT '是否已通知 0:否 1:是' AFTER his_registration_no,
ADD INDEX IF NOT EXISTS idx_patient_status (patient_id, status),
ADD INDEX IF NOT EXISTS idx_schedule_id (schedule_id),
ADD INDEX IF NOT EXISTS idx_visit_date (visit_date);

-- 叫号队列表
CREATE TABLE IF NOT EXISTS calling_queue (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    registration_id BIGINT NOT NULL COMMENT '挂号单ID',
    registration_no VARCHAR(50) COMMENT '挂号单号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    doctor_name VARCHAR(50) COMMENT '医生姓名',
    department_id BIGINT NOT NULL COMMENT '科室ID',
    department_name VARCHAR(100) COMMENT '科室名称',
    visit_date DATE NOT NULL COMMENT '就诊日期',
    time_slot VARCHAR(20) NOT NULL COMMENT '时段',
    queue_number INT NOT NULL COMMENT '排队序号',
    current_number INT COMMENT '当前叫号',
    queue_status TINYINT DEFAULT 0 COMMENT '排队状态 0:等待中 1:叫号中 2:已就诊 3:已过号',
    room_no VARCHAR(20) COMMENT '诊室号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_registration (registration_id),
    INDEX idx_doctor_date (doctor_id, visit_date),
    INDEX idx_queue_status (queue_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='叫号队列表';

-- 消息通知表
CREATE TABLE IF NOT EXISTS message_notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    wechat_user_id BIGINT COMMENT '微信用户ID',
    patient_id BIGINT COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    msg_type VARCHAR(50) NOT NULL COMMENT '消息类型',
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    content TEXT COMMENT '消息内容',
    template_id VARCHAR(100) COMMENT '微信模板ID',
    template_data TEXT COMMENT '模板数据(JSON)',
    page_path VARCHAR(255) COMMENT '跳转页面',
    msg_status TINYINT DEFAULT 0 COMMENT '发送状态 0:待发送 1:已发送 2:发送失败',
    send_time DATETIME COMMENT '发送时间',
    read_status TINYINT DEFAULT 0 COMMENT '阅读状态 0:未读 1:已读',
    read_time DATETIME COMMENT '阅读时间',
    error_msg VARCHAR(500) COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_wechat_user (wechat_user_id),
    INDEX idx_patient (patient_id),
    INDEX idx_msg_status (msg_status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- 初始化科室数据
INSERT INTO department (parent_id, dept_code, dept_name, dept_name_pinyin, dept_type, dept_level, location, description, sort) VALUES
-- 一级科室
(0, 'NK', '内科', 'neike', 1, 1, '门诊楼2层', '内科是医院的重点科室，涵盖多个亚专科', 1),
(0, 'WK', '外科', 'waike', 1, 1, '门诊楼3层', '外科包含普外科、骨科等多个专业', 2),
(0, 'EK', '儿科', 'erke', 1, 1, '门诊楼4层', '儿科专注于儿童健康', 3),
(0, 'FK', '妇产科', 'fuchanke', 1, 1, '门诊楼5层', '妇产科提供妇女健康和孕产服务', 4),
(0, 'YK', '眼科', 'yanke', 1, 1, '门诊楼6层', '眼科诊治各类眼部疾病', 5),
(0, 'EBHK', '耳鼻喉科', 'erbihouke', 1, 1, '门诊楼6层', '耳鼻喉科诊治耳、鼻、喉疾病', 6),
(0, 'KQK', '口腔科', 'kouqiangke', 1, 1, '门诊楼7层', '口腔科提供口腔医疗服务', 7),
(0, 'PFK', '皮肤科', 'pifuke', 1, 1, '门诊楼7层', '皮肤科诊治各类皮肤疾病', 8),
-- 内科下属二级科室
(1, 'NK-XH', '消化内科', 'xiaohuaneike', 1, 2, '门诊楼2层东区', '诊治消化系统疾病', 1),
(1, 'NK-XX', '心血管内科', 'xinxuanguanneike', 1, 2, '门诊楼2层西区', '诊治心血管疾病', 2),
(1, 'NK-HX', '呼吸内科', 'huxineike', 1, 2, '门诊楼2层南区', '诊治呼吸系统疾病', 3),
(1, 'NK-SJ', '神经内科', 'shenjingneike', 1, 2, '门诊楼2层北区', '诊治神经系统疾病', 4),
(1, 'NK-NF', '内分泌科', 'neifenmike', 1, 2, '门诊楼2层中区', '诊治内分泌疾病', 5),
(1, 'NK-SZ', '肾内科', 'shenneike', 1, 2, '门诊楼2层中区', '诊治肾脏疾病', 6),
-- 外科下属二级科室
(2, 'WK-PW', '普外科', 'putongwaike', 1, 2, '门诊楼3层东区', '诊治普通外科疾病', 1),
(2, 'WK-GK', '骨科', 'guke', 1, 2, '门诊楼3层西区', '诊治骨骼关节疾病', 2),
(2, 'WK-XW', '胸外科', 'xiongwaike', 1, 2, '门诊楼3层南区', '诊治胸部外科疾病', 3),
(2, 'WK-NK', '神经外科', 'shenjingwaike', 1, 2, '门诊楼3层北区', '诊治神经系统外科疾病', 4),
(2, 'WK-MN', '泌尿外科', 'miniaowaike', 1, 2, '门诊楼3层中区', '诊治泌尿系统疾病', 5);

-- 初始化医生数据
INSERT INTO doctor (doctor_no, name, name_pinyin, gender, title, specialty, department_id, department_name, consultation_fee, introduction, sort) VALUES
('D001', '张建国', 'zhangjianguo', 1, '主任医师', '擅长消化系统疾病的诊治，尤其在胃炎、胃溃疡、肠炎等方面有丰富经验', 9, '消化内科', 50.00, '张建国医生从医30年，在消化内科领域有深厚造诣，发表学术论文50余篇', 1),
('D002', '李明华', 'liminghua', 0, '副主任医师', '擅长心血管疾病的预防和治疗，包括高血压、冠心病、心律失常等', 10, '心血管内科', 40.00, '李明华医生在心血管内科工作20年，对复杂心律失常有独到见解', 2),
('D003', '王秀兰', 'wangxiulan', 0, '主任医师', '擅长呼吸系统疾病，如哮喘、肺炎、慢性阻塞性肺病等', 11, '呼吸内科', 50.00, '王秀兰医生是呼吸内科专家，在肺部感染和哮喘治疗方面经验丰富', 3),
('D004', '赵志强', 'zhaozhiqiang', 1, '副主任医师', '擅长脑血管病、头痛、癫痫、帕金森病等神经系统疾病', 12, '神经内科', 40.00, '赵志强医生专注神经内科临床工作18年，擅长脑血管病的诊治', 4),
('D005', '陈美玲', 'chenmeiling', 0, '主任医师', '擅长糖尿病、甲状腺疾病、垂体疾病等内分泌代谢疾病', 13, '内分泌科', 50.00, '陈美玲医生在内分泌科工作25年，在糖尿病治疗方面有丰富经验', 5),
('D006', '刘伟强', 'liuweiqiang', 1, '副主任医师', '擅长急慢性肾炎、肾病综合征、肾功能衰竭等肾脏疾病', 14, '肾内科', 40.00, '刘伟强医生在肾内科临床和科研方面都有突出成绩', 6),
('D007', '孙德明', 'sundeming', 1, '主任医师', '擅长普外科常见疾病，如阑尾炎、疝气、胆囊疾病、甲状腺疾病等', 15, '普外科', 50.00, '孙德明医生是普外科资深专家，完成各类手术上万例', 7),
('D008', '周爱华', 'zhouaihua', 0, '副主任医师', '擅长骨关节疾病、脊柱疾病、骨折创伤等骨科疾病', 16, '骨科', 40.00, '周爱华医生在骨科领域有20年经验，尤其擅长关节置换手术', 8),
('D009', '吴明亮', 'wuliangming', 1, '主治医师', '擅长小儿常见病、多发病的诊治，如感冒、发烧、腹泻等', 3, '儿科', 30.00, '吴明亮医生对儿科常见病有丰富的临床经验', 9),
('D010', '郑小燕', 'zhengxiaoyan', 0, '主治医师', '擅长妇科炎症、月经不调、孕期保健等妇产科疾病', 4, '妇产科', 30.00, '郑小燕医生在妇产科工作10年，对孕期保健有深入研究', 10);

-- 初始化症状数据
INSERT INTO symptom (symptom_code, symptom_name, symptom_icon, parent_id, sort) VALUES
-- 常见症状分类
('SY-001', '头痛', '🤕', 0, 1),
('SY-002', '发烧', '🤒', 0, 2),
('SY-003', '腹痛', '😣', 0, 3),
('SY-004', '咳嗽', '😷', 0, 4),
('SY-005', '胸闷', '❤️‍🔥', 0, 5),
('SY-006', '腹泻', '💩', 0, 6),
('SY-007', '呕吐', '🤮', 0, 7),
('SY-008', '皮疹', '🔴', 0, 8),
('SY-009', '关节痛', '🦴', 0, 9),
('SY-010', '腰痛', '🩹', 0, 10),
('SY-011', '头晕', '💫', 0, 11),
('SY-012', '失眠', '🌙', 0, 12),
('SY-013', '眼痛', '👁️', 0, 13),
('SY-014', '鼻塞', '👃', 0, 14),
('SY-015', '牙痛', '🦷', 0, 15),
('SY-016', '咽喉痛', '👄', 0, 16),
('SY-017', '尿频', '🚻', 0, 17),
('SY-018', '水肿', '🫠', 0, 18),
('SY-019', '贫血', '🩸', 0, 19),
('SY-020', '体重变化', '⚖️', 0, 20);

-- 症状科室关联数据
INSERT INTO symptom_department (symptom_id, department_id, match_weight) VALUES
-- 头痛相关
(1, 12, 3), -- 头痛 -> 神经内科
(1, 10, 2), -- 头痛 -> 心血管内科
(1, 5, 1),  -- 头痛 -> 眼科
-- 发烧相关
(2, 11, 3), -- 发烧 -> 呼吸内科
(2, 9, 2),  -- 发烧 -> 消化内科
(2, 3, 2),  -- 发烧 -> 儿科
-- 腹痛相关
(3, 9, 3),  -- 腹痛 -> 消化内科
(3, 15, 2), -- 腹痛 -> 普外科
(3, 14, 1), -- 腹痛 -> 肾内科
-- 咳嗽相关
(4, 11, 3), -- 咳嗽 -> 呼吸内科
(4, 15, 1), -- 咳嗽 -> 普外科
-- 胸闷相关
(5, 10, 3), -- 胸闷 -> 心血管内科
(5, 11, 2), -- 胸闷 -> 呼吸内科
-- 腹泻相关
(6, 9, 3),  -- 腹泻 -> 消化内科
(6, 3, 2),  -- 腹泻 -> 儿科
-- 呕吐相关
(7, 9, 3),  -- 呕吐 -> 消化内科
(7, 12, 2), -- 呕吐 -> 神经内科
-- 皮疹相关
(8, 8, 3),  -- 皮疹 -> 皮肤科
(8, 13, 1), -- 皮疹 -> 内分泌科
-- 关节痛相关
(9, 16, 3), -- 关节痛 -> 骨科
(9, 13, 1), -- 关节痛 -> 内分泌科
-- 腰痛相关
(10, 16, 3), -- 腰痛 -> 骨科
(10, 14, 2), -- 腰痛 -> 肾内科
(10, 17, 1), -- 腰痛 -> 胸外科
-- 头晕相关
(11, 10, 3), -- 头晕 -> 心血管内科
(11, 12, 3), -- 头晕 -> 神经内科
(11, 13, 1), -- 头晕 -> 内分泌科
-- 失眠相关
(12, 12, 3), -- 失眠 -> 神经内科
(12, 13, 1), -- 失眠 -> 内分泌科
-- 眼痛相关
(13, 5, 3),  -- 眼痛 -> 眼科
(13, 12, 1), -- 眼痛 -> 神经内科
-- 鼻塞相关
(14, 6, 3),  -- 鼻塞 -> 耳鼻喉科
(14, 11, 2), -- 鼻塞 -> 呼吸内科
-- 牙痛相关
(15, 7, 3),  -- 牙痛 -> 口腔科
-- 咽喉痛相关
(16, 6, 3),  -- 咽喉痛 -> 耳鼻喉科
(16, 11, 2), -- 咽喉痛 -> 呼吸内科
-- 尿频相关
(17, 19, 3), -- 尿频 -> 泌尿外科
(17, 14, 2), -- 尿频 -> 肾内科
(17, 13, 1), -- 尿频 -> 内分泌科
-- 水肿相关
(18, 14, 3), -- 水肿 -> 肾内科
(18, 10, 2), -- 水肿 -> 心血管内科
(18, 9, 1),  -- 水肿 -> 消化内科
-- 贫血相关
(19, 13, 3), -- 贫血 -> 内分泌科
(19, 9, 1),  -- 贫血 -> 消化内科
-- 体重变化相关
(20, 13, 3), -- 体重变化 -> 内分泌科
(20, 9, 1);  -- 体重变化 -> 消化内科

-- 初始化医生排班数据（未来7天）
INSERT INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee) VALUES
-- 张建国 消化内科 未来7天上午出诊
(1, '张建国', 9, '消化内科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', '08:00:00', '11:30:00', 30, 30, 50.00),
(1, '张建国', 9, '消化内科', DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'AM', '08:00:00', '11:30:00', 30, 25, 50.00),
(1, '张建国', 9, '消化内科', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'AM', '08:00:00', '11:30:00', 30, 20, 50.00),
(1, '张建国', 9, '消化内科', DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'AM', '08:00:00', '11:30:00', 30, 15, 50.00),
(1, '张建国', 9, '消化内科', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'AM', '08:00:00', '11:30:00', 30, 10, 50.00),
(1, '张建国', 9, '消化内科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'PM', '14:00:00', '17:30:00', 25, 25, 50.00),
(1, '张建国', 9, '消化内科', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'PM', '14:00:00', '17:30:00', 25, 0, 50.00),
-- 李明华 心血管内科
(2, '李明华', 10, '心血管内科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', '08:00:00', '11:30:00', 25, 20, 40.00),
(2, '李明华', 10, '心血管内科', DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'PM', '14:00:00', '17:30:00', 25, 25, 40.00),
(2, '李明华', 10, '心血管内科', DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'AM', '08:00:00', '11:30:00', 25, 15, 40.00),
(2, '李明华', 10, '心血管内科', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'PM', '14:00:00', '17:30:00', 25, 0, 40.00),
-- 王秀兰 呼吸内科
(3, '王秀兰', 11, '呼吸内科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', '08:00:00', '11:30:00', 30, 28, 50.00),
(3, '王秀兰', 11, '呼吸内科', DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'AM', '08:00:00', '11:30:00', 30, 22, 50.00),
(3, '王秀兰', 11, '呼吸内科', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'PM', '14:00:00', '17:30:00', 30, 30, 50.00),
(3, '王秀兰', 11, '呼吸内科', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'AM', '08:00:00', '11:30:00', 30, 18, 50.00),
(3, '王秀兰', 11, '呼吸内科', DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'PM', '14:00:00', '17:30:00', 30, 25, 50.00),
-- 赵志强 神经内科
(4, '赵志强', 12, '神经内科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'PM', '14:00:00', '17:30:00', 25, 20, 40.00),
(4, '赵志强', 12, '神经内科', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'AM', '08:00:00', '11:30:00', 25, 15, 40.00),
(4, '赵志强', 12, '神经内科', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'PM', '14:00:00', '17:30:00', 25, 10, 40.00),
-- 陈美玲 内分泌科
(5, '陈美玲', 13, '内分泌科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', '08:00:00', '11:30:00', 30, 20, 50.00),
(5, '陈美玲', 13, '内分泌科', DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'PM', '14:00:00', '17:30:00', 30, 25, 50.00),
(5, '陈美玲', 13, '内分泌科', DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'AM', '08:00:00', '11:30:00', 30, 18, 50.00),
(5, '陈美玲', 13, '内分泌科', DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'PM', '14:00:00', '17:30:00', 30, 30, 50.00),
-- 刘伟强 肾内科
(6, '刘伟强', 14, '肾内科', DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'AM', '08:00:00', '11:30:00', 25, 22, 40.00),
(6, '刘伟强', 14, '肾内科', DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'PM', '14:00:00', '17:30:00', 25, 20, 40.00),
(6, '刘伟强', 14, '肾内科', DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'AM', '08:00:00', '11:30:00', 25, 15, 40.00),
-- 孙德明 普外科
(7, '孙德明', 15, '普外科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'PM', '14:00:00', '17:30:00', 30, 25, 50.00),
(7, '孙德明', 15, '普外科', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'AM', '08:00:00', '11:30:00', 30, 20, 50.00),
(7, '孙德明', 15, '普外科', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'PM', '14:00:00', '17:30:00', 30, 10, 50.00),
-- 周爱华 骨科
(8, '周爱华', 16, '骨科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', '08:00:00', '11:30:00', 30, 28, 40.00),
(8, '周爱华', 16, '骨科', DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'PM', '14:00:00', '17:30:00', 30, 25, 40.00),
(8, '周爱华', 16, '骨科', DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'AM', '08:00:00', '11:30:00', 30, 20, 40.00),
(8, '周爱华', 16, '骨科', DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'PM', '14:00:00', '17:30:00', 30, 15, 40.00),
-- 吴明亮 儿科
(9, '吴明亮', 3, '儿科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', '08:00:00', '11:30:00', 35, 30, 30.00),
(9, '吴明亮', 3, '儿科', DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'PM', '14:00:00', '17:30:00', 35, 35, 30.00),
(9, '吴明亮', 3, '儿科', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'AM', '08:00:00', '11:30:00', 35, 25, 30.00),
(9, '吴明亮', 3, '儿科', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'PM', '14:00:00', '17:30:00', 35, 20, 30.00),
-- 郑小燕 妇产科
(10, '郑小燕', 4, '妇产科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'PM', '14:00:00', '17:30:00', 30, 28, 30.00),
(10, '郑小燕', 4, '妇产科', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'AM', '08:00:00', '11:30:00', 30, 25, 30.00),
(10, '郑小燕', 4, '妇产科', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'PM', '14:00:00', '17:30:00', 30, 20, 30.00);

-- 门诊管理菜单权限数据
-- 现有最大权限ID为22，从23开始
INSERT INTO sys_permission (id, parent_id, name, code, type, path, component, icon, sort) VALUES
-- 门诊管理一级菜单 (id=23)
(23, 0, '门诊管理', 'outpatient', 1, '/outpatient', NULL, 'hospital', 4),
-- 科室管理 (id=24)
(24, 23, '科室管理', 'outpatient:department', 1, '/outpatient/department', 'outpatient/department/index', 'office', 1),
-- 医生管理 (id=25)
(25, 23, '医生管理', 'outpatient:doctor', 1, '/outpatient/doctor', 'outpatient/doctor/index', 'doctor', 2),
-- 排班管理 (id=26)
(26, 23, '排班管理', 'outpatient:schedule', 1, '/outpatient/schedule', 'outpatient/schedule/index', 'schedule', 3),
-- 挂号订单 (id=27)
(27, 23, '挂号订单', 'outpatient:registration', 1, '/outpatient/registration', 'outpatient/registration/index', 'form', 4),
-- 症状管理 (id=28)
(28, 23, '症状管理', 'outpatient:symptom', 1, '/outpatient/symptom', 'outpatient/symptom/index', 'help', 5),
-- 科室管理按钮权限
(29, 24, '科室查询', 'outpatient:department:query', 2, NULL, NULL, NULL, 1),
(30, 24, '科室新增', 'outpatient:department:add', 2, NULL, NULL, NULL, 2),
(31, 24, '科室编辑', 'outpatient:department:edit', 2, NULL, NULL, NULL, 3),
(32, 24, '科室删除', 'outpatient:department:delete', 2, NULL, NULL, NULL, 4),
-- 医生管理按钮权限
(33, 25, '医生查询', 'outpatient:doctor:query', 2, NULL, NULL, NULL, 1),
(34, 25, '医生新增', 'outpatient:doctor:add', 2, NULL, NULL, NULL, 2),
(35, 25, '医生编辑', 'outpatient:doctor:edit', 2, NULL, NULL, NULL, 3),
(36, 25, '医生删除', 'outpatient:doctor:delete', 2, NULL, NULL, NULL, 4),
-- 排班管理按钮权限
(37, 26, '排班查询', 'outpatient:schedule:query', 2, NULL, NULL, NULL, 1),
(38, 26, '排班新增', 'outpatient:schedule:add', 2, NULL, NULL, NULL, 2),
(39, 26, '排班编辑', 'outpatient:schedule:edit', 2, NULL, NULL, NULL, 3),
(40, 26, '排班删除', 'outpatient:schedule:delete', 2, NULL, NULL, NULL, 4),
(41, 26, '排班停诊', 'outpatient:schedule:suspend', 2, NULL, NULL, NULL, 5),
(42, 26, '批量生成', 'outpatient:schedule:batchGenerate', 2, NULL, NULL, NULL, 6),
-- 挂号订单按钮权限
(43, 27, '订单查询', 'outpatient:registration:query', 2, NULL, NULL, NULL, 1),
(44, 27, '订单新增', 'outpatient:registration:add', 2, NULL, NULL, NULL, 2),
(45, 27, '订单编辑', 'outpatient:registration:edit', 2, NULL, NULL, NULL, 3),
(46, 27, '订单删除', 'outpatient:registration:delete', 2, NULL, NULL, NULL, 4),
(47, 27, '订单签到', 'outpatient:registration:checkin', 2, NULL, NULL, NULL, 5),
(48, 27, '订单完成', 'outpatient:registration:finish', 2, NULL, NULL, NULL, 6),
(49, 27, '订单退费', 'outpatient:registration:refund', 2, NULL, NULL, NULL, 7),
-- 症状管理按钮权限
(50, 28, '症状查询', 'outpatient:symptom:query', 2, NULL, NULL, NULL, 1),
(51, 28, '症状新增', 'outpatient:symptom:add', 2, NULL, NULL, NULL, 2),
(52, 28, '症状编辑', 'outpatient:symptom:edit', 2, NULL, NULL, NULL, 3),
(53, 28, '症状删除', 'outpatient:symptom:delete', 2, NULL, NULL, NULL, 4);

-- 分配门诊管理权限给超级管理员角色（role_id=1）
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 23), (1, 24), (1, 25), (1, 26), (1, 27), (1, 28),
(1, 29), (1, 30), (1, 31), (1, 32),
(1, 33), (1, 34), (1, 35), (1, 36),
(1, 37), (1, 38), (1, 39), (1, 40), (1, 41), (1, 42),
(1, 43), (1, 44), (1, 45), (1, 46), (1, 47), (1, 48), (1, 49),
(1, 50), (1, 51), (1, 52), (1, 53);
