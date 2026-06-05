-- ========================================
-- Mock数据初始化脚本
-- 执行前请确保数据库 hospital_admin 已存在
-- ========================================

USE hospital_admin;

-- ========================================
-- 1. 插入科室数据
-- ========================================
INSERT IGNORE INTO department (id, parent_id, dept_code, dept_name, dept_name_pinyin, dept_type, dept_level, location, phone, description, sort, status, create_time, update_time, deleted) VALUES
(1, 0, 'NK', '内科', 'neike', 1, 1, '门诊楼2楼', '010-12345678', '内科是集医疗、教学、科研为一体的综合性临床科室，拥有一支高素质的医疗团队。', 1, 1, NOW(), NOW(), 0),
(2, 0, 'WK', '外科', 'waike', 1, 1, '门诊楼3楼', '010-12345679', '外科是医院的重点科室，设有普外科、骨科、神经外科等专业。', 2, 1, NOW(), NOW(), 0),
(3, 0, 'FK', '妇科', 'fuke', 1, 1, '门诊楼4楼', '010-12345680', '妇科是专门诊治女性生殖系统疾病的科室，提供妇科常见病、多发病的诊治。', 3, 1, NOW(), NOW(), 0),
(4, 0, 'EK', '儿科', 'erke', 1, 1, '门诊楼5楼', '010-12345681', '儿科是专门诊治儿童疾病的科室，为0-14岁儿童提供全面的医疗服务。', 4, 1, NOW(), NOW(), 0),
(5, 0, 'YXYK', '心血管内科', 'xinxueguanneike', 1, 2, '门诊楼2楼东侧', '010-12345682', '心血管内科是医院的重点专科，主要诊治心血管系统疾病。', 5, 1, NOW(), NOW(), 0),
(6, 0, 'XHK', '呼吸内科', 'huxineike', 1, 2, '门诊楼2楼西侧', '010-12345683', '呼吸内科主要诊治呼吸系统疾病，包括肺部感染、慢性阻塞性肺疾病等。', 6, 1, NOW(), NOW(), 0),
(7, 0, 'XHNK', '消化内科', 'xiaohuaneike', 1, 2, '门诊楼2楼南侧', '010-12345684', '消化内科主要诊治消化系统疾病，包括胃肠、肝胆胰等疾病。', 7, 1, NOW(), NOW(), 0),
(8, 0, 'GKK', '骨科', 'guke', 1, 2, '门诊楼3楼东侧', '010-12345685', '骨科主要诊治骨骼、关节、肌肉等运动系统疾病。', 8, 1, NOW(), NOW(), 0),
(9, 0, 'PWK', '普外科', 'puwaike', 1, 2, '门诊楼3楼西侧', '010-12345686', '普外科主要诊治甲状腺、乳腺、胃肠、肝胆等外科疾病。', 9, 1, NOW(), NOW(), 0),
(10, 0, 'YK', '眼科', 'yanke', 1, 1, '门诊楼6楼', '010-12345687', '眼科是专门诊治眼部疾病的科室，提供视力保健和眼病诊治服务。', 10, 1, NOW(), NOW(), 0),
(11, 0, 'EBHK', '耳鼻喉科', 'erbihouke', 1, 1, '门诊楼6楼东侧', '010-12345688', '耳鼻喉科主要诊治耳、鼻、咽喉等部位的疾病。', 11, 1, NOW(), NOW(), 0),
(12, 0, 'KQK', '口腔科', 'kouqiangke', 1, 1, '门诊楼6楼西侧', '010-12345689', '口腔科主要诊治口腔疾病，包括牙齿、牙龈、口腔黏膜等疾病。', 12, 1, NOW(), NOW(), 0),
(13, 0, 'PFK', '皮肤科', 'pifuke', 1, 1, '门诊楼7楼', '010-12345690', '皮肤科主要诊治皮肤、毛发、甲等皮肤附属器疾病。', 13, 1, NOW(), NOW(), 0),
(14, 0, 'ZYYK', '中医科', 'zhongyike', 1, 1, '门诊楼7楼东侧', '010-12345691', '中医科采用传统中医理论和方法诊治疾病，提供中西医结合治疗。', 14, 1, NOW(), NOW(), 0),
(15, 0, 'SJNK', '神经内科', 'shenjingneike', 1, 2, '门诊楼2楼北侧', '010-12345692', '神经内科主要诊治神经系统疾病，包括脑血管病、癫痫、帕金森病等。', 15, 1, NOW(), NOW(), 0);

-- ========================================
-- 2. 插入医生数据（包含AI生成头像）
-- ========================================
INSERT IGNORE INTO doctor (id, doctor_no, name, name_pinyin, gender, title, specialty, department_id, department_name, consultation_fee, introduction, avatar, sort, status, create_time, update_time, deleted) VALUES
(1, 'D001', '张伟', 'zhangwei', 1, '主任医师', '冠心病、高血压、心律失常、心力衰竭等心血管疾病的诊治', 5, '心血管内科', 50.00, '从事心血管内科临床工作20余年，在冠心病、高血压、心律失常等疾病的诊治方面具有丰富的临床经验。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20male%20doctor%20portrait%20with%20white%20coat%20smiling%20friendly%20medical%20avatar&image_size=square_hd', 1, 1, NOW(), NOW(), 0),
(2, 'D002', '李芳', 'lifang', 0, '副主任医师', '冠脉介入治疗、心绞痛、心肌梗死', 5, '心血管内科', 40.00, '擅长冠心病的介入治疗，在急性心肌梗死的抢救方面具有丰富经验。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20female%20doctor%20portrait%20with%20white%20coat%20smiling%20friendly%20medical%20avatar&image_size=square_hd', 2, 1, NOW(), NOW(), 0),
(3, 'D003', '王强', 'wangqiang', 1, '主任医师', '慢性阻塞性肺疾病、哮喘、肺部感染', 6, '呼吸内科', 50.00, '从事呼吸内科临床工作25年，在慢性阻塞性肺疾病、哮喘等呼吸系统疾病的诊治方面造诣深厚。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=asian%20male%20doctor%20in%20white%20coat%20professional%20medical%20portrait%20stethoscope&image_size=square_hd', 3, 1, NOW(), NOW(), 0),
(4, 'D004', '赵敏', 'zhaomin', 0, '副主任医师', '肺癌的早期诊断、肺结节的鉴别诊断', 6, '呼吸内科', 40.00, '擅长肺癌的早期诊断和治疗，在肺结节的鉴别诊断方面具有丰富经验。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=asian%20female%20doctor%20in%20white%20coat%20professional%20portrait%20friendly%20smile&image_size=square_hd', 4, 1, NOW(), NOW(), 0),
(5, 'D005', '刘建国', 'liujianguo', 1, '主任医师', '胃炎、胃溃疡、结肠炎、肝病', 7, '消化内科', 50.00, '在消化系统疾病的诊治方面具有丰富经验，尤其擅长胃肠道疾病和肝病的诊治。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=middle%20aged%20asian%20male%20doctor%20white%20coat%20professional%20medical%20avatar&image_size=square_hd', 5, 1, NOW(), NOW(), 0),
(6, 'D006', '陈静', 'chenjing', 0, '主治医师', '消化内镜检查、胃肠息肉切除', 7, '消化内科', 30.00, '擅长消化内镜检查和治疗，包括胃镜、结肠镜检查及息肉切除。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=young%20asian%20female%20doctor%20white%20coat%20smiling%20professional%20portrait&image_size=square_hd', 6, 1, NOW(), NOW(), 0),
(7, 'D007', '杨磊', 'yanglei', 1, '副主任医师', '骨折、关节置换、脊柱疾病', 8, '骨科', 40.00, '擅长四肢骨折的手术治疗、人工关节置换及脊柱疾病的诊治。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=male%20surgeon%20doctor%20white%20coat%20professional%20portrait%20confident&image_size=square_hd', 7, 1, NOW(), NOW(), 0),
(8, 'D008', '孙丽华', 'sunlihua', 0, '主任医师', '运动损伤、关节镜手术', 8, '骨科', 50.00, '在运动损伤的诊治和关节镜手术方面具有丰富经验，擅长膝关节、肩关节疾病的微创治疗。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=female%20orthopedic%20doctor%20white%20coat%20professional%20medical%20portrait&image_size=square_hd', 8, 1, NOW(), NOW(), 0),
(9, 'D009', '周明', 'zhouming', 1, '副主任医师', '甲状腺疾病、乳腺疾病、胃肠肿瘤', 9, '普外科', 40.00, '擅长甲状腺、乳腺疾病的诊治及胃肠肿瘤的手术治疗。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=asian%20male%20surgeon%20white%20coat%20professional%20portrait%20friendly&image_size=square_hd', 9, 1, NOW(), NOW(), 0),
(10, 'D010', '吴秀兰', 'wuxiulan', 0, '主任医师', '子宫肌瘤、卵巢囊肿、妇科炎症', 3, '妇科', 50.00, '从事妇产科临床工作30年，在妇科常见病、多发病及疑难杂症的诊治方面具有丰富经验。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=senior%20female%20doctor%20gynecologist%20white%20coat%20professional%20portrait&image_size=square_hd', 10, 1, NOW(), NOW(), 0),
(11, 'D011', '郑晓燕', 'zhengxiaoyan', 0, '副主任医师', '月经不调、不孕不育、妇科肿瘤', 3, '妇科', 40.00, '擅长月经不调、不孕不育的诊治及妇科肿瘤的早期诊断和治疗。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=young%20female%20gynecologist%20white%20coat%20smiling%20professional%20avatar&image_size=square_hd', 11, 1, NOW(), NOW(), 0),
(12, 'D012', '马晓东', 'maxiaodong', 1, '主任医师', '小儿呼吸系统疾病、小儿消化系统疾病', 4, '儿科', 50.00, '从事儿科临床工作20余年，在小儿常见病、多发病的诊治方面具有丰富经验。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=male%20pediatrician%20doctor%20white%20coat%20friendly%20smile%20with%20children&image_size=square_hd', 12, 1, NOW(), NOW(), 0),
(13, 'D013', '林小燕', 'linxiaoyan', 0, '副主任医师', '新生儿疾病、儿童保健', 4, '儿科', 40.00, '擅长新生儿疾病的诊治和儿童保健，在婴幼儿喂养和生长发育评估方面经验丰富。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=female%20pediatrician%20white%20coat%20warm%20smile%20professional%20portrait&image_size=square_hd', 13, 1, NOW(), NOW(), 0),
(14, 'D014', '黄建国', 'huangjianguo', 1, '主任医师', '青光眼、白内障、近视眼手术', 10, '眼科', 50.00, '从事眼科临床工作25年，在青光眼、白内障的诊治及近视眼手术方面具有丰富经验。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=male%20eye%20doctor%20ophthalmologist%20white%20coat%20professional%20portrait&image_size=square_hd', 14, 1, NOW(), NOW(), 0),
(15, 'D015', '徐丽娟', 'xulijuan', 0, '副主任医师', '过敏性鼻炎、鼻窦炎、中耳炎', 11, '耳鼻喉科', 40.00, '擅长过敏性鼻炎、鼻窦炎、中耳炎等耳鼻喉科常见疾病的诊治。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=female%20ent%20doctor%20white%20coat%20professional%20medical%20portrait&image_size=square_hd', 15, 1, NOW(), NOW(), 0),
(16, 'D016', '何志远', 'hezhiyuan', 1, '主治医师', '龋齿、牙周病、牙齿矫正', 12, '口腔科', 30.00, '擅长龋齿、牙周病的诊治及牙齿矫正、牙齿修复等口腔治疗。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=male%20dentist%20white%20coat%20professional%20portrait%20friendly%20smile&image_size=square_hd', 16, 1, NOW(), NOW(), 0),
(17, 'D017', '潘美玲', 'panmeiling', 0, '副主任医师', '皮炎、湿疹、痤疮、皮肤美容', 13, '皮肤科', 40.00, '在皮肤科常见疾病的诊治方面经验丰富，擅长痤疮、皮炎湿疹及皮肤美容治疗。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=female%20dermatologist%20doctor%20white%20coat%20professional%20portrait&image_size=square_hd', 17, 1, NOW(), NOW(), 0),
(18, 'D018', '冯天华', 'fengtianhua', 1, '主任医师', '中医内科、针灸、推拿', 14, '中医科', 50.00, '从事中医临床工作30年，擅长运用中医中药治疗内科常见病、多发病。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=senior%20chinese%20medicine%20doctor%20traditional%20practitioner%20portrait&image_size=square_hd', 18, 1, NOW(), NOW(), 0),
(19, 'D019', '罗秀珍', 'luoxiuzhen', 0, '副主任医师', '脑血管病、头痛、眩晕、帕金森病', 15, '神经内科', 40.00, '在脑血管病、头痛、眩晕等神经系统疾病的诊治方面具有丰富经验。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=female%20neurologist%20doctor%20white%20coat%20professional%20medical%20portrait&image_size=square_hd', 19, 1, NOW(), NOW(), 0),
(20, 'D020', '蒋文杰', 'jiangwenjie', 1, '主治医师', '糖尿病、甲状腺疾病、内分泌失调', 1, '内科', 30.00, '擅长糖尿病、甲状腺疾病等内分泌代谢疾病的诊治。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=young%20male%20doctor%20endocrinologist%20white%20coat%20professional%20portrait&image_size=square_hd', 20, 1, NOW(), NOW(), 0);

-- ========================================
-- 3. 插入排班数据（未来14天）
-- ========================================
-- 注意：以下INSERT语句使用INSERT IGNORE避免重复插入
-- 排班规则：每个医生每周固定时间出诊
-- 周一至周五：上午8:00-12:00，下午14:00-17:30
-- 号量：上午30号，下午25号

-- 生成2025-06-01 至 2025-06-14 的排班数据

-- 心血管内科医生
INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    1, '张伟', 5, '心血管内科', 
    DATE_ADD('2025-06-01', INTERVAL (t.n * 7) DAY), 
    '上午', '08:00:00', '12:00:00', 30, 30, 50.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-01', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    1, '张伟', 5, '心血管内科', 
    DATE_ADD('2025-06-03', INTERVAL (t.n * 7) DAY), 
    '下午', '14:00:00', '17:30:00', 25, 25, 50.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-03', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    2, '李芳', 5, '心血管内科', 
    DATE_ADD('2025-06-02', INTERVAL (t.n * 7) DAY), 
    '上午', '08:00:00', '12:00:00', 30, 30, 40.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-02', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    2, '李芳', 5, '心血管内科', 
    DATE_ADD('2025-06-04', INTERVAL (t.n * 7) DAY), 
    '下午', '14:00:00', '17:30:00', 25, 25, 40.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-04', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

-- 呼吸内科医生
INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    3, '王强', 6, '呼吸内科', 
    DATE_ADD('2025-06-02', INTERVAL (t.n * 7) DAY), 
    '上午', '08:00:00', '12:00:00', 30, 30, 50.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-02', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    3, '王强', 6, '呼吸内科', 
    DATE_ADD('2025-06-05', INTERVAL (t.n * 7) DAY), 
    '下午', '14:00:00', '17:30:00', 25, 25, 50.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-05', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    4, '赵敏', 6, '呼吸内科', 
    DATE_ADD('2025-06-01', INTERVAL (t.n * 7) DAY), 
    '下午', '14:00:00', '17:30:00', 25, 25, 40.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-01', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    4, '赵敏', 6, '呼吸内科', 
    DATE_ADD('2025-06-03', INTERVAL (t.n * 7) DAY), 
    '上午', '08:00:00', '12:00:00', 30, 30, 40.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-03', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

-- 消化内科医生
INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    5, '刘建国', 7, '消化内科', 
    DATE_ADD('2025-06-01', INTERVAL (t.n * 7) DAY), 
    '上午', '08:00:00', '12:00:00', 30, 30, 50.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-01', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    5, '刘建国', 7, '消化内科', 
    DATE_ADD('2025-06-04', INTERVAL (t.n * 7) DAY), 
    '下午', '14:00:00', '17:30:00', 25, 25, 50.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-04', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    6, '陈静', 7, '消化内科', 
    DATE_ADD('2025-06-02', INTERVAL (t.n * 7) DAY), 
    '下午', '14:00:00', '17:30:00', 25, 25, 30.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-02', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    6, '陈静', 7, '消化内科', 
    DATE_ADD('2025-06-05', INTERVAL (t.n * 7) DAY), 
    '上午', '08:00:00', '12:00:00', 30, 30, 30.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-05', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

-- 骨科医生
INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    7, '杨磊', 8, '骨科', 
    DATE_ADD('2025-06-01', INTERVAL (t.n * 7) DAY), 
    '下午', '14:00:00', '17:30:00', 25, 25, 40.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-01', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    7, '杨磊', 8, '骨科', 
    DATE_ADD('2025-06-03', INTERVAL (t.n * 7) DAY), 
    '上午', '08:00:00', '12:00:00', 30, 30, 40.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-03', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    8, '孙丽华', 8, '骨科', 
    DATE_ADD('2025-06-02', INTERVAL (t.n * 7) DAY), 
    '上午', '08:00:00', '12:00:00', 30, 30, 50.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-02', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    8, '孙丽华', 8, '骨科', 
    DATE_ADD('2025-06-05', INTERVAL (t.n * 7) DAY), 
    '下午', '14:00:00', '17:30:00', 25, 25, 50.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-05', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

-- 普外科医生
INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    9, '周明', 9, '普外科', 
    DATE_ADD('2025-06-01', INTERVAL (t.n * 7) DAY), 
    '上午', '08:00:00', '12:00:00', 30, 30, 40.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-01', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    9, '周明', 9, '普外科', 
    DATE_ADD('2025-06-04', INTERVAL (t.n * 7) DAY), 
    '下午', '14:00:00', '17:30:00', 25, 25, 40.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-04', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

-- 妇科医生
INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    10, '吴秀兰', 3, '妇科', 
    DATE_ADD('2025-06-02', INTERVAL (t.n * 7) DAY), 
    '上午', '08:00:00', '12:00:00', 30, 30, 50.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-02', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    10, '吴秀兰', 3, '妇科', 
    DATE_ADD('2025-06-05', INTERVAL (t.n * 7) DAY), 
    '上午', '08:00:00', '12:00:00', 30, 30, 50.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-05', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    11, '郑晓燕', 3, '妇科', 
    DATE_ADD('2025-06-01', INTERVAL (t.n * 7) DAY), 
    '下午', '14:00:00', '17:30:00', 25, 25, 40.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-01', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    11, '郑晓燕', 3, '妇科', 
    DATE_ADD('2025-06-03', INTERVAL (t.n * 7) DAY), 
    '下午', '14:00:00', '17:30:00', 25, 25, 40.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-03', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

-- 儿科医生
INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    12, '马晓东', 4, '儿科', 
    DATE_ADD('2025-06-01', INTERVAL (t.n * 7) DAY), 
    '上午', '08:00:00', '12:00:00', 30, 30, 50.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-01', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    12, '马晓东', 4, '儿科', 
    DATE_ADD('2025-06-04', INTERVAL (t.n * 7) DAY), 
    '上午', '08:00:00', '12:00:00', 30, 30, 50.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-04', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    13, '林小燕', 4, '儿科', 
    DATE_ADD('2025-06-02', INTERVAL (t.n * 7) DAY), 
    '下午', '14:00:00', '17:30:00', 25, 25, 40.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-02', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted)
SELECT 
    13, '林小燕', 4, '儿科', 
    DATE_ADD('2025-06-05', INTERVAL (t.n * 7) DAY), 
    '下午', '14:00:00', '17:30:00', 25, 25, 40.00, 0, 1, NOW(), NOW(), 0
FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2) t
WHERE DATE_ADD('2025-06-05', INTERVAL (t.n * 7) DAY) BETWEEN '2025-06-01' AND '2025-06-14';

-- 其他科室医生（各加一些排班）
INSERT IGNORE INTO doctor_schedule (doctor_id, doctor_name, department_id, department_name, schedule_date, time_slot, start_time, end_time, max_count, remaining_count, registration_fee, is_suspended, status, create_time, update_time, deleted) VALUES
-- 眼科
(14, '黄建国', 10, '眼科', '2025-06-02', '上午', '08:00:00', '12:00:00', 30, 30, 50.00, 0, 1, NOW(), NOW(), 0),
(14, '黄建国', 10, '眼科', '2025-06-04', '上午', '08:00:00', '12:00:00', 30, 30, 50.00, 0, 1, NOW(), NOW(), 0),
(14, '黄建国', 10, '眼科', '2025-06-09', '上午', '08:00:00', '12:00:00', 30, 30, 50.00, 0, 1, NOW(), NOW(), 0),
(14, '黄建国', 10, '眼科', '2025-06-11', '上午', '08:00:00', '12:00:00', 30, 30, 50.00, 0, 1, NOW(), NOW(), 0),
-- 耳鼻喉科
(15, '徐丽娟', 11, '耳鼻喉科', '2025-06-01', '上午', '08:00:00', '12:00:00', 30, 30, 40.00, 0, 1, NOW(), NOW(), 0),
(15, '徐丽娟', 11, '耳鼻喉科', '2025-06-03', '下午', '14:00:00', '17:30:00', 25, 25, 40.00, 0, 1, NOW(), NOW(), 0),
(15, '徐丽娟', 11, '耳鼻喉科', '2025-06-08', '上午', '08:00:00', '12:00:00', 30, 30, 40.00, 0, 1, NOW(), NOW(), 0),
(15, '徐丽娟', 11, '耳鼻喉科', '2025-06-10', '下午', '14:00:00', '17:30:00', 25, 25, 40.00, 0, 1, NOW(), NOW(), 0),
-- 口腔科
(16, '何志远', 12, '口腔科', '2025-06-02', '下午', '14:00:00', '17:30:00', 25, 25, 30.00, 0, 1, NOW(), NOW(), 0),
(16, '何志远', 12, '口腔科', '2025-06-05', '上午', '08:00:00', '12:00:00', 30, 30, 30.00, 0, 1, NOW(), NOW(), 0),
(16, '何志远', 12, '口腔科', '2025-06-09', '下午', '14:00:00', '17:30:00', 25, 25, 30.00, 0, 1, NOW(), NOW(), 0),
(16, '何志远', 12, '口腔科', '2025-06-12', '上午', '08:00:00', '12:00:00', 30, 30, 30.00, 0, 1, NOW(), NOW(), 0),
-- 皮肤科
(17, '潘美玲', 13, '皮肤科', '2025-06-01', '下午', '14:00:00', '17:30:00', 25, 25, 40.00, 0, 1, NOW(), NOW(), 0),
(17, '潘美玲', 13, '皮肤科', '2025-06-04', '上午', '08:00:00', '12:00:00', 30, 30, 40.00, 0, 1, NOW(), NOW(), 0),
(17, '潘美玲', 13, '皮肤科', '2025-06-08', '下午', '14:00:00', '17:30:00', 25, 25, 40.00, 0, 1, NOW(), NOW(), 0),
(17, '潘美玲', 13, '皮肤科', '2025-06-11', '上午', '08:00:00', '12:00:00', 30, 30, 40.00, 0, 1, NOW(), NOW(), 0),
-- 中医科
(18, '冯天华', 14, '中医科', '2025-06-03', '上午', '08:00:00', '12:00:00', 30, 30, 50.00, 0, 1, NOW(), NOW(), 0),
(18, '冯天华', 14, '中医科', '2025-06-05', '下午', '14:00:00', '17:30:00', 25, 25, 50.00, 0, 1, NOW(), NOW(), 0),
(18, '冯天华', 14, '中医科', '2025-06-10', '上午', '08:00:00', '12:00:00', 30, 30, 50.00, 0, 1, NOW(), NOW(), 0),
(18, '冯天华', 14, '中医科', '2025-06-12', '下午', '14:00:00', '17:30:00', 25, 25, 50.00, 0, 1, NOW(), NOW(), 0),
-- 神经内科
(19, '罗秀珍', 15, '神经内科', '2025-06-02', '上午', '08:00:00', '12:00:00', 30, 30, 40.00, 0, 1, NOW(), NOW(), 0),
(19, '罗秀珍', 15, '神经内科', '2025-06-05', '上午', '08:00:00', '12:00:00', 30, 30, 40.00, 0, 1, NOW(), NOW(), 0),
(19, '罗秀珍', 15, '神经内科', '2025-06-09', '上午', '08:00:00', '12:00:00', 30, 30, 40.00, 0, 1, NOW(), NOW(), 0),
(19, '罗秀珍', 15, '神经内科', '2025-06-12', '上午', '08:00:00', '12:00:00', 30, 30, 40.00, 0, 1, NOW(), NOW(), 0),
-- 内科
(20, '蒋文杰', 1, '内科', '2025-06-01', '下午', '14:00:00', '17:30:00', 25, 25, 30.00, 0, 1, NOW(), NOW(), 0),
(20, '蒋文杰', 1, '内科', '2025-06-03', '下午', '14:00:00', '17:30:00', 25, 25, 30.00, 0, 1, NOW(), NOW(), 0),
(20, '蒋文杰', 1, '内科', '2025-06-05', '下午', '14:00:00', '17:30:00', 25, 25, 30.00, 0, 1, NOW(), NOW(), 0),
(20, '蒋文杰', 1, '内科', '2025-06-10', '下午', '14:00:00', '17:30:00', 25, 25, 30.00, 0, 1, NOW(), NOW(), 0),
(20, '蒋文杰', 1, '内科', '2025-06-12', '下午', '14:00:00', '17:30:00', 25, 25, 30.00, 0, 1, NOW(), NOW(), 0),
(20, '蒋文杰', 1, '内科', '2025-06-14', '下午', '14:00:00', '17:30:00', 25, 25, 30.00, 0, 1, NOW(), NOW(), 0);

-- ========================================
-- 4. 插入示例挂号记录（用于测试支付流程）
-- ========================================
-- 注意：这些挂号记录的状态为0（待支付），用于测试mock支付功能
-- 患者ID假设为1、2、3（需要已存在患者数据）

-- 先检查是否存在患者，如果不存在插入示例患者
INSERT IGNORE INTO patient (id, patient_no, name, gender, age, id_card, phone, address, medical_history, allergy_history, emergency_contact, emergency_phone, create_time, update_time, deleted) VALUES
(1, 'P001', '张三', 1, 35, '110101199001011234', '13800138001', '北京市朝阳区某某街道1号', '无', '青霉素过敏', '张某某', '13900139001', NOW(), NOW(), 0),
(2, 'P002', '李四', 0, 28, '110101199701015678', '13800138002', '北京市海淀区某某街道2号', '高血压', '无', '李某某', '13900139002', NOW(), NOW(), 0),
(3, 'P003', '王五', 1, 45, '110101198001019012', '13800138003', '北京市西城区某某街道3号', '糖尿病', '磺胺类过敏', '王某某', '13900139003', NOW(), NOW(), 0);

-- 插入示例挂号记录（状态0=待支付）
INSERT IGNORE INTO registration (id, patient_id, patient_name, doctor_id, doctor_name, department_id, department, schedule_id, visit_date, visit_time_slot, registration_fee, pay_method, status, registration_no, create_time, update_time, deleted) VALUES
(1, 1, '张三', 1, '张伟', 5, '心血管内科', 1, '2025-06-02', '上午', 50.00, NULL, 0, 'REG20250601001', NOW(), NOW(), 0),
(2, 2, '李四', 3, '王强', 6, '呼吸内科', NULL, '2025-06-03', '上午', 50.00, NULL, 0, 'REG20250601002', NOW(), NOW(), 0),
(3, 3, '王五', 12, '马晓东', 4, '儿科', NULL, '2025-06-04', '上午', 50.00, NULL, 0, 'REG20250601003', NOW(), NOW(), 0);

-- ========================================
-- 初始化完成
-- ========================================
SELECT 'Mock数据初始化完成！' AS message;
SELECT '科室数据: 15条' AS info UNION ALL
SELECT '医生数据: 20条' UNION ALL
SELECT '排班数据: 约120条' UNION ALL
SELECT '患者数据: 3条' UNION ALL
SELECT '待支付挂号记录: 3条';
