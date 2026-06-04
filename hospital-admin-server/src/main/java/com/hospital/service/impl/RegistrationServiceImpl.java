package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.dto.RegistrationCancelDTO;
import com.hospital.dto.RegistrationCreateDTO;
import com.hospital.dto.RegistrationPayDTO;
import com.hospital.dto.RegistrationVO;
import com.hospital.entity.CallingQueue;
import com.hospital.entity.Doctor;
import com.hospital.entity.DoctorSchedule;
import com.hospital.entity.Registration;
import com.hospital.mapper.DoctorMapper;
import com.hospital.mapper.DoctorScheduleMapper;
import com.hospital.mapper.RegistrationMapper;
import com.hospital.service.CallingQueueService;
import com.hospital.service.DoctorScheduleService;
import com.hospital.service.MessageNotificationService;
import com.hospital.service.RegistrationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration> implements RegistrationService {

    @Autowired
    private RegistrationMapper registrationMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private DoctorScheduleMapper doctorScheduleMapper;

    @Autowired
    private DoctorScheduleService doctorScheduleService;

    @Autowired
    private CallingQueueService callingQueueService;

    @Autowired
    private MessageNotificationService messageNotificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegistrationVO create(RegistrationCreateDTO dto, Long operatorId, String operatorName) {
        if (dto.getScheduleId() != null) {
            DoctorSchedule schedule = doctorScheduleMapper.selectById(dto.getScheduleId());
            if (schedule == null || schedule.getRemainingCount() <= 0) {
                throw new RuntimeException("号源不足");
            }
            if (schedule.getIsSuspended() == 1) {
                throw new RuntimeException("该号源已停诊");
            }
        }

        Registration registration = new Registration();
        BeanUtils.copyProperties(dto, registration);
        registration.setRegistrationNo(generateRegistrationNo());
        registration.setDepartment(dto.getDepartmentName());
        registration.setStatus(0);
        registration.setOperatorId(operatorId);
        registration.setOperatorName(operatorName);
        registration.setCreateTime(LocalDateTime.now());
        registration.setUpdateTime(LocalDateTime.now());
        registration.setCreateBy(operatorId);
        registration.setUpdateBy(operatorId);
        save(registration);

        if (dto.getScheduleId() != null) {
            doctorScheduleService.decreaseRemainingCount(dto.getScheduleId());
        }

        callingQueueService.createQueue(
                registration.getId(),
                dto.getDoctorId(),
                dto.getDepartmentId(),
                dto.getVisitDate(),
                dto.getVisitTimeSlot()
        );

        return convertToVO(registration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(RegistrationCancelDTO dto, Long operatorId, String operatorName) {
        Registration registration = getById(dto.getRegistrationId());
        if (registration == null) {
            return false;
        }
        if (registration.getStatus() != 0 && registration.getStatus() != 1) {
            throw new RuntimeException("当前状态不允许取消");
        }

        registration.setStatus(3);
        registration.setUpdateTime(LocalDateTime.now());
        registration.setUpdateBy(operatorId);
        registration.setRemark(dto.getCancelReason());
        registration.setCancelTime(LocalDateTime.now());
        registration.setCancelReason(dto.getCancelReason());
        boolean result = updateById(registration);

        if (result) {
            DoctorSchedule schedule = doctorScheduleMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<DoctorSchedule>()
                            .eq("doctor_id", registration.getDoctorId())
                            .eq("schedule_date", registration.getVisitDate())
                            .eq("time_slot", registration.getVisitTimeSlot())
            );
            if (schedule != null) {
                doctorScheduleService.increaseRemainingCount(schedule.getId());
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegistrationVO pay(RegistrationPayDTO dto, Long operatorId, String operatorName) {
        Registration registration = getById(dto.getRegistrationId());
        if (registration == null) {
            return null;
        }
        if (registration.getStatus() != 0) {
            throw new RuntimeException("当前状态不允许支付");
        }

        registration.setStatus(1);
        registration.setPayMethod(dto.getPayMethod());
        registration.setPayTime(LocalDateTime.now());
        registration.setUpdateTime(LocalDateTime.now());
        registration.setUpdateBy(operatorId);
        updateById(registration);

        messageNotificationService.sendRegistrationSuccess(
                registration.getId(),
                null,
                registration.getPatientId(),
                registration.getPatientName()
        );

        return convertToVO(registration);
    }

    @Override
    public List<RegistrationVO> listByPatientId(Long patientId, Integer status, Integer offset, Integer limit) {
        List<Registration> list;
        if (status != null) {
            list = registrationMapper.selectByPatientIdAndStatus(patientId, status);
        } else {
            if (offset == null) offset = 0;
            if (limit == null) limit = 20;
            list = registrationMapper.selectByPatientId(patientId, offset, limit);
        }
        return convertToVOList(list);
    }

    @Override
    public RegistrationVO getDetail(Long id) {
        Registration registration = getById(id);
        if (registration == null) {
            return null;
        }
        RegistrationVO vo = convertToVO(registration);
        CallingQueue queue = callingQueueService.getByRegistrationId(id);
        if (queue != null) {
            vo.setQueueNumber(queue.getQueueNumber());
            vo.setRoomNo(queue.getRoomNo());
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegistrationVO checkin(Long id) {
        Registration registration = getById(id);
        if (registration == null) {
            return null;
        }
        if (registration.getStatus() != 1) {
            throw new RuntimeException("当前状态不允许签到");
        }

        registration.setStatus(2);
        registration.setCheckinTime(LocalDateTime.now());
        registration.setUpdateTime(LocalDateTime.now());
        updateById(registration);

        return convertToVO(registration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegistrationVO finish(Long id) {
        Registration registration = getById(id);
        if (registration == null) {
            return null;
        }
        if (registration.getStatus() != 2) {
            throw new RuntimeException("当前状态不允许完成");
        }

        registration.setStatus(2);
        registration.setFinishTime(LocalDateTime.now());
        registration.setUpdateTime(LocalDateTime.now());
        updateById(registration);

        callingQueueService.completeVisit(id);

        return convertToVO(registration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegistrationVO refund(Long id, String reason) {
        Registration registration = getById(id);
        if (registration == null) {
            return null;
        }
        if (registration.getStatus() != 1 && registration.getStatus() != 2) {
            throw new RuntimeException("当前状态不允许退费");
        }

        registration.setStatus(4);
        registration.setRefundTime(LocalDateTime.now());
        registration.setRefundAmount(registration.getRegistrationFee());
        registration.setRemark(reason);
        registration.setUpdateTime(LocalDateTime.now());
        updateById(registration);

        return convertToVO(registration);
    }

    @Override
    public RegistrationVO getQueueInfo(Long id) {
        Registration registration = getById(id);
        if (registration == null) {
            return null;
        }
        RegistrationVO vo = convertToVO(registration);
        CallingQueue queue = callingQueueService.getByRegistrationId(id);
        if (queue != null) {
            vo.setQueueNumber(queue.getQueueNumber());
            vo.setRoomNo(queue.getRoomNo());
            Integer position = callingQueueService.getQueuePosition(id);
            if (position != null) {
                vo.setQueueNumber(position);
            }
        }
        return vo;
    }

    @Override
    public PageResult<RegistrationVO> adminPage(PageQuery query, String registrationNo, String patientName, Long departmentId, Long doctorId, LocalDate startDate, LocalDate endDate, Integer status) {
        Page<Registration> page = new Page<>(query.getPageNum(), query.getPageSize());
        QueryWrapper<Registration> wrapper = new QueryWrapper<>();
        if (registrationNo != null && !registrationNo.isEmpty()) {
            wrapper.like("registration_no", registrationNo);
        }
        if (patientName != null && !patientName.isEmpty()) {
            wrapper.like("patient_name", patientName);
        }
        if (departmentId != null) {
            wrapper.eq("department_id", departmentId);
        }
        if (doctorId != null) {
            wrapper.eq("doctor_id", doctorId);
        }
        if (startDate != null) {
            wrapper.ge("visit_date", startDate);
        }
        if (endDate != null) {
            wrapper.le("visit_date", endDate);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        page = registrationMapper.selectPage(page, wrapper);
        List<Registration> records = page.getRecords();
        List<RegistrationVO> voList = convertToVOList(records);
        return PageResult.of(page.getTotal(), voList);
    }

    @Override
    public String generateRegistrationNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = lambdaQuery()
                .likeRight(Registration::getRegistrationNo, "REG" + date)
                .count() + 1;
        return "REG" + date + String.format("%06d", count);
    }

    private List<RegistrationVO> convertToVOList(List<Registration> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> doctorIds = list.stream().map(Registration::getDoctorId).distinct().collect(Collectors.toList());
        List<Doctor> doctors = doctorMapper.selectBatchIds(doctorIds);
        Map<Long, String> doctorTitleMap = doctors.stream().collect(Collectors.toMap(Doctor::getId, Doctor::getTitle));
        List<RegistrationVO> voList = new ArrayList<>();
        for (Registration registration : list) {
            RegistrationVO vo = convertToVO(registration);
            vo.setDoctorTitle(doctorTitleMap.get(registration.getDoctorId()));
            voList.add(vo);
        }
        return voList;
    }

    private RegistrationVO convertToVO(Registration registration) {
        RegistrationVO vo = new RegistrationVO();
        BeanUtils.copyProperties(registration, vo);
        vo.setDepartmentName(registration.getDepartment());
        vo.setStatusName(getStatusName(registration.getStatus()));
        vo.setRegistrationTypeName(getRegistrationTypeName(registration.getRegistrationType()));
        vo.setPayMethodName(getPayMethodName(registration.getPayMethod()));
        return vo;
    }

    private String getStatusName(Integer status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case 0:
                return "待支付";
            case 1:
                return "已支付";
            case 2:
                return "已就诊";
            case 3:
                return "已取消";
            case 4:
                return "已退费";
            default:
                return "未知";
        }
    }

    private String getRegistrationTypeName(Integer type) {
        if (type == null) {
            return "";
        }
        switch (type) {
            case 1:
                return "普通门诊";
            case 2:
                return "专家门诊";
            case 3:
                return "特需门诊";
            default:
                return "未知";
        }
    }

    private String getPayMethodName(Integer method) {
        if (method == null) {
            return "";
        }
        switch (method) {
            case 1:
                return "微信支付";
            case 2:
                return "支付宝";
            case 3:
                return "医保支付";
            case 4:
                return "现金";
            case 5:
                return "银行卡";
            default:
                return "未知";
        }
    }
}
