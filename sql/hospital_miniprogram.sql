-- 微信小程序扩展表

-- 微信用户表
CREATE TABLE IF NOT EXISTS wechat_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    openid VARCHAR(100) UNIQUE COMMENT '微信openid',
    unionid VARCHAR(100) UNIQUE COMMENT '微信unionid',
    session_key VARCHAR(100) COMMENT '会话密钥',
    nickname VARCHAR(100) COMMENT '微信昵称',
    avatar_url VARCHAR(255) COMMENT '头像地址',
    gender TINYINT COMMENT '性别 0:女 1:男',
    country VARCHAR(50) COMMENT '国家',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    phone VARCHAR(20) COMMENT '手机号',
    phone_encrypted VARCHAR(255) COMMENT '加密手机号数据',
    phone_iv VARCHAR(100) COMMENT '加密偏移量',
    current_patient_id BIGINT COMMENT '当前选择的就诊人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信用户表';

-- 就诊人（家庭成员）表
CREATE TABLE IF NOT EXISTS patient_family (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    wechat_user_id BIGINT NOT NULL COMMENT '微信用户ID',
    patient_id BIGINT COMMENT '关联患者ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender TINYINT COMMENT '性别 0:女 1:男',
    id_card VARCHAR(18) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '手机号',
    birthday VARCHAR(20) COMMENT '出生日期',
    relation VARCHAR(20) COMMENT '与本人关系 本人/父亲/母亲/配偶/子女/其他',
    status TINYINT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    has_visit_record TINYINT DEFAULT 0 COMMENT '是否有就诊记录 0:否 1:是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除',
    INDEX idx_wechat_user_id (wechat_user_id),
    INDEX idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='就诊人（家庭成员）表';

-- 健康档案表
CREATE TABLE IF NOT EXISTS health_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_id BIGINT NOT NULL UNIQUE COMMENT '患者ID',
    allergy_history TEXT COMMENT '过敏史',
    medical_history TEXT COMMENT '既往病史',
    chronic_disease TEXT COMMENT '慢病',
    operation_history TEXT COMMENT '手术史',
    drug_contraindication TEXT COMMENT '药物禁忌',
    blood_type VARCHAR(10) COMMENT '血型',
    height VARCHAR(10) COMMENT '身高(cm)',
    weight VARCHAR(10) COMMENT '体重(kg)',
    marital_status VARCHAR(20) COMMENT '婚姻状况',
    occupation VARCHAR(50) COMMENT '职业',
    smoking_status VARCHAR(20) COMMENT '吸烟情况',
    drinking_status VARCHAR(20) COMMENT '饮酒情况',
    exercise_habit VARCHAR(100) COMMENT '运动习惯',
    dietary_habit VARCHAR(100) COMMENT '饮食习惯',
    sleep_habit VARCHAR(100) COMMENT '睡眠习惯',
    defecation_habit VARCHAR(100) COMMENT '排便习惯',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康档案表';

-- 健康附件表
CREATE TABLE IF NOT EXISTS health_attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    category VARCHAR(30) NOT NULL COMMENT '分类 CT/验血/体检/病历/其他',
    file_type VARCHAR(20) COMMENT '文件类型 image/pdf',
    file_name VARCHAR(255) COMMENT '文件名',
    file_url VARCHAR(255) COMMENT '文件地址',
    file_size VARCHAR(20) COMMENT '文件大小',
    description TEXT COMMENT '描述',
    exam_date VARCHAR(20) COMMENT '检查日期',
    exam_dept VARCHAR(50) COMMENT '检查科室',
    exam_doctor VARCHAR(50) COMMENT '检查医生',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除',
    INDEX idx_patient_id (patient_id),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康附件表';

-- 就诊码表
CREATE TABLE IF NOT EXISTS visit_code (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    code_type VARCHAR(20) DEFAULT 'PATIENT' COMMENT '码类型 PATIENT/REGISTER/PAYMENT/PHARMACY',
    code_content VARCHAR(100) UNIQUE COMMENT '码内容',
    qr_code_url VARCHAR(500) COMMENT '二维码地址',
    expire_time DATETIME COMMENT '过期时间',
    status TINYINT DEFAULT 1 COMMENT '状态 0:已使用 1:有效 2:已过期',
    use_scene VARCHAR(50) COMMENT '使用场景',
    use_record TEXT COMMENT '使用记录',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除',
    INDEX idx_patient_id (patient_id),
    INDEX idx_code_content (code_content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='就诊码表';

-- 插入测试微信用户数据
INSERT INTO wechat_user (openid, nickname, avatar_url, gender, phone, current_patient_id) VALUES
('mock_openid_001', '微信用户001', 'https://thirdwx.qlogo.cn/mmopen/vi_32/123456789', 1, '13800138001', 1),
('mock_openid_002', '微信用户002', 'https://thirdwx.qlogo.cn/mmopen/vi_32/987654321', 0, '13800138003', 2);

-- 插入测试就诊人数据
INSERT INTO patient_family (wechat_user_id, patient_id, name, gender, id_card, phone, birthday, relation, status, has_visit_record) VALUES
(1, 1, '张三', 1, '110101198901011234', '13800138001', '1989-01-01', '本人', 1, 1),
(1, NULL, '张父', 1, '110101196501011234', '13800138010', '1965-01-01', '父亲', 1, 0),
(1, NULL, '张母', 0, '110101196702021234', '13800138011', '1967-02-02', '母亲', 1, 0),
(2, 2, '李四', 0, '110101199602021234', '13800138003', '1996-02-02', '本人', 1, 1);

-- 插入测试健康档案数据
INSERT INTO health_record (patient_id, allergy_history, medical_history, chronic_disease, blood_type, height, weight, smoking_status, drinking_status) VALUES
(1, '青霉素、海鲜过敏', '高血压', '原发性高血压', 'A型', '175', '75', '不吸烟', '偶尔饮酒'),
(2, '无', '无', '无', 'B型', '165', '55', '不吸烟', '不饮酒'),
(3, '海鲜过敏', '糖尿病', '2型糖尿病', 'O型', '172', '80', '已戒烟', '不饮酒');

-- 插入测试健康附件数据
INSERT INTO health_attachment (patient_id, category, file_type, file_name, file_url, file_size, description, exam_date, exam_dept, exam_doctor) VALUES
(1, 'CT', 'image', '胸部CT.png', 'https://hospital-assets.oss-cn-beijing.aliyuncs.com/ct/202401/chest_ct_001.png', '2.5MB', '年度体检胸部CT', '2024-01-15', '放射科', '王医生'),
(1, '验血', 'image', '血常规报告.png', 'https://hospital-assets.oss-cn-beijing.aliyuncs.com/blood/202401/blood_001.png', '1.2MB', '常规血检', '2024-01-15', '检验科', '李医生'),
(1, '体检', 'pdf', '年度体检报告.pdf', 'https://hospital-assets.oss-cn-beijing.aliyuncs.com/physical/202401/physical_001.pdf', '5.8MB', '2024年度全面体检报告', '2024-01-15', '体检中心', '张医生'),
(3, '验血', 'image', '血糖检测.png', 'https://hospital-assets.oss-cn-beijing.aliyuncs.com/blood/202402/blood_sugar_001.png', '0.8MB', '空腹血糖检测', '2024-02-20', '检验科', '赵医生');
