USE hospital_admin;

-- 插入已就诊的挂号订单（status=4）
INSERT INTO registration 
(registration_no, patient_id, patient_name, patient_gender, patient_age, department, department_id, doctor_id, doctor_name, registration_type, registration_fee, pay_method, pay_time, visit_date, visit_time_slot, visit_location, queue_number, checkin_time, consult_time, finish_time, status, operator_id, operator_name, remark)
VALUES
('GH20240520001', 1, '张三', 1, 35, '心内科', 5, 3, '王医生', 1, 30.00, 1, '2024-05-20 08:30:00', '2024-05-20', '上午', '门诊楼2楼心内科', 5, '2024-05-20 09:00:00', '2024-05-20 09:15:00', '2024-05-20 09:45:00', 4, 1, '管理员', '高血压复诊'),
('GH20240525001', 1, '张三', 1, 35, '内分泌科', 6, 4, '李医生', 1, 25.00, 2, '2024-05-25 14:00:00', '2024-05-25', '下午', '门诊楼3楼内分泌科', 12, '2024-05-25 14:20:00', '2024-05-25 14:35:00', '2024-05-25 15:10:00', 4, 1, '管理员', '糖尿病随访'),
('GH20240528001', 1, '张三', 1, 35, '呼吸内科', 7, 5, '赵医生', 1, 20.00, 1, '2024-05-28 10:00:00', '2024-05-28', '上午', '门诊楼2楼呼吸内科', 8, '2024-05-28 10:15:00', '2024-05-28 10:30:00', '2024-05-28 11:00:00', 4, 1, '管理员', '咳嗽检查');

-- 插入投诉建议测试数据
INSERT INTO complaint (complaint_no, patient_id, patient_name, patient_phone, type, target_type, department_id, department_name, doctor_id, doctor_name, title, content, status, handle_user_id, handle_user_name, handle_time, reply, reply_time, images) VALUES
('CP20240601001', 1, '张三', '13800138000', 1, 1, 5, '心内科', NULL, NULL, '心内科等待时间过长', '在心内科就诊时等待了2个小时才叫到号，希望能优化叫号系统。', 2, 1, '管理员', '2024-06-02 10:30:00', '感谢您的反馈，我们已经优化了叫号系统，现在等待时间已缩短至30分钟内。', '2024-06-02 10:30:00', NULL),
('CP20240603001', 1, '张三', '13800138000', 2, 2, 6, '内分泌科', 4, '李医生', '建议增加自助查询设备', '建议在门诊楼各层增加自助查询设备，方便患者查询检验报告。', 0, NULL, NULL, NULL, NULL, NULL, NULL);

-- 插入历史评价数据
INSERT INTO doctor_review (registration_id, patient_id, patient_name, doctor_id, doctor_name, department_id, department_name, rating, content, status, reply, reply_user_id, reply_user_name, reply_time) VALUES
(1, 1, '张三', 1, '医生', 1, '内科', 5, '医生很专业，态度也很好，解答很详细。', 1, '感谢您的好评，我们会继续努力！', 1, '管理员', '2024-06-03 14:00:00'),
(4, 1, '张三', 1, '医生', 1, '内科', 4, '整体满意，就是等待时间稍微长了一点。', 1, '感谢您的反馈，我们会优化就诊流程。', 1, '管理员', '2024-06-05 09:00:00');

-- 验证数据
SELECT '已就诊挂号订单:' as type, COUNT(*) as count FROM registration WHERE patient_id = 1 AND status = 4
UNION ALL
SELECT '投诉建议:', COUNT(*) FROM complaint
UNION ALL
SELECT '医生评价:', COUNT(*) FROM doctor_review;
