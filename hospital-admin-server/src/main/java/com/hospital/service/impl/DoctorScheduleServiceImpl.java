package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.dto.DoctorScheduleVO;
import com.hospital.entity.Doctor;
import com.hospital.entity.DoctorSchedule;
import com.hospital.entity.Registration;
import com.hospital.entity.WechatUser;
import com.hospital.mapper.DoctorMapper;
import com.hospital.mapper.DoctorScheduleMapper;
import com.hospital.mapper.RegistrationMapper;
import com.hospital.mapper.WechatUserMapper;
import com.hospital.service.DoctorScheduleService;
import com.hospital.service.MessageNotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DoctorScheduleServiceImpl extends ServiceImpl<DoctorScheduleMapper, DoctorSchedule> implements DoctorScheduleService {

    @Autowired
    private DoctorScheduleMapper doctorScheduleMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private RegistrationMapper registrationMapper;

    @Autowired
    private WechatUserMapper wechatUserMapper;

    @Autowired
    private MessageNotificationService messageNotificationService;

    @Override
    public PageResult<DoctorScheduleVO> getSchedulePage(PageQuery pageQuery, Long doctorId, Long departmentId, LocalDate startDate, LocalDate endDate) {
        Page<DoctorSchedule> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        QueryWrapper<DoctorSchedule> wrapper = new QueryWrapper<>();
        if (doctorId != null) {
            wrapper.eq("doctor_id", doctorId);
        }
        if (departmentId != null) {
            wrapper.eq("department_id", departmentId);
        }
        if (startDate != null) {
            wrapper.ge("schedule_date", startDate);
        }
        if (endDate != null) {
            wrapper.le("schedule_date", endDate);
        }
        wrapper.orderByAsc("schedule_date", "start_time");
        page = doctorScheduleMapper.selectPage(page, wrapper);
        List<DoctorSchedule> records = page.getRecords();
        List<DoctorScheduleVO> voList = convertToVOList(records);
        return PageResult.of(page.getTotal(), voList);
    }

    @Override
    public DoctorScheduleVO getScheduleDetail(Long id) {
        DoctorSchedule schedule = getById(id);
        if (schedule == null) {
            return null;
        }
        return convertToVO(schedule);
    }

    @Override
    public List<DoctorScheduleVO> getByDoctorIdAndDate(Long doctorId, LocalDate scheduleDate) {
        List<DoctorSchedule> list = lambdaQuery()
                .eq(DoctorSchedule::getDoctorId, doctorId)
                .eq(DoctorSchedule::getScheduleDate, scheduleDate)
                .eq(DoctorSchedule::getStatus, 1)
                .list();
        return convertToVOList(list);
    }

    @Override
    public List<DoctorScheduleVO> getByDepartmentIdAndDate(Long departmentId, LocalDate scheduleDate) {
        List<DoctorSchedule> list = lambdaQuery()
                .eq(DoctorSchedule::getDepartmentId, departmentId)
                .eq(DoctorSchedule::getScheduleDate, scheduleDate)
                .eq(DoctorSchedule::getStatus, 1)
                .list();
        return convertToVOList(list);
    }

    @Override
    public List<DoctorScheduleVO> getAvailableSchedule(Long doctorId, LocalDate scheduleDate) {
        List<DoctorSchedule> list = lambdaQuery()
                .eq(DoctorSchedule::getDoctorId, doctorId)
                .eq(DoctorSchedule::getScheduleDate, scheduleDate)
                .eq(DoctorSchedule::getStatus, 1)
                .eq(DoctorSchedule::getIsSuspended, 0)
                .gt(DoctorSchedule::getRemainingCount, 0)
                .list();
        return convertToVOList(list);
    }

    @Override
    public List<DoctorScheduleVO> getByDoctorAndDateRange(Long doctorId, LocalDate startDate, LocalDate endDate) {
        List<DoctorSchedule> list = lambdaQuery()
                .eq(DoctorSchedule::getDoctorId, doctorId)
                .ge(DoctorSchedule::getScheduleDate, startDate)
                .le(DoctorSchedule::getScheduleDate, endDate)
                .eq(DoctorSchedule::getStatus, 1)
                .eq(DoctorSchedule::getIsSuspended, 0)
                .orderByAsc(DoctorSchedule::getScheduleDate, DoctorSchedule::getStartTime)
                .list();
        return convertToVOList(list);
    }

    @Override
    public List<DoctorSchedule> getByDepartmentAndDate(Long departmentId, LocalDate date) {
        return lambdaQuery()
                .eq(DoctorSchedule::getDepartmentId, departmentId)
                .eq(DoctorSchedule::getScheduleDate, date)
                .eq(DoctorSchedule::getStatus, 1)
                .eq(DoctorSchedule::getIsSuspended, 0)
                .orderByAsc(DoctorSchedule::getStartTime)
                .list();
    }

    @Override
    public List<DoctorSchedule> getByDate(LocalDate date) {
        return lambdaQuery()
                .eq(DoctorSchedule::getScheduleDate, date)
                .eq(DoctorSchedule::getStatus, 1)
                .eq(DoctorSchedule::getIsSuspended, 0)
                .orderByAsc(DoctorSchedule::getDepartmentId, DoctorSchedule::getStartTime)
                .list();
    }

    @Override
    public boolean saveSchedule(DoctorSchedule schedule) {
        schedule.setCreateTime(LocalDateTime.now());
        schedule.setUpdateTime(LocalDateTime.now());
        if (schedule.getRemainingCount() == null) {
            schedule.setRemainingCount(schedule.getMaxCount());
        }
        if (schedule.getIsSuspended() == null) {
            schedule.setIsSuspended(0);
        }
        return save(schedule);
    }

    @Override
    public boolean updateSchedule(DoctorSchedule schedule) {
        schedule.setUpdateTime(LocalDateTime.now());
        return updateById(schedule);
    }

    @Override
    public boolean deleteSchedule(Long id) {
        return removeById(id);
    }

    @Override
    public boolean suspendSchedule(Long id, String reason) {
        DoctorSchedule schedule = getById(id);
        if (schedule == null) {
            return false;
        }
        schedule.setIsSuspended(1);
        schedule.setSuspendReason(reason);
        schedule.setUpdateTime(LocalDateTime.now());
        return updateById(schedule);
    }

    @Override
    public boolean resumeSchedule(Long id) {
        DoctorSchedule schedule = getById(id);
        if (schedule == null) {
            return false;
        }
        schedule.setIsSuspended(0);
        schedule.setSuspendReason(null);
        schedule.setUpdateTime(LocalDateTime.now());
        return updateById(schedule);
    }

    @Override
    public boolean decreaseRemainingCount(Long id) {
        DoctorSchedule schedule = getById(id);
        if (schedule == null || schedule.getRemainingCount() <= 0) {
            return false;
        }
        schedule.setRemainingCount(schedule.getRemainingCount() - 1);
        schedule.setUpdateTime(LocalDateTime.now());
        return updateById(schedule);
    }

    @Override
    public boolean increaseRemainingCount(Long id) {
        DoctorSchedule schedule = getById(id);
        if (schedule == null) {
            return false;
        }
        if (schedule.getRemainingCount() < schedule.getMaxCount()) {
            schedule.setRemainingCount(schedule.getRemainingCount() + 1);
            schedule.setUpdateTime(LocalDateTime.now());
            return updateById(schedule);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean suspend(Long id, String reason) {
        boolean success = suspendSchedule(id, reason);
        if (success) {
            DoctorSchedule schedule = getById(id);
            if (schedule != null) {
                List<Registration> registrations = registrationMapper.selectList(
                        new QueryWrapper<Registration>()
                                .eq("schedule_id", id)
                                .in("status", 1, 2)
                );
                for (Registration registration : registrations) {
                    WechatUser wechatUser = wechatUserMapper.selectOne(
                            new QueryWrapper<WechatUser>()
                                    .eq("current_patient_id", registration.getPatientId())
                    );
                    if (wechatUser != null) {
                        messageNotificationService.sendRegistrationCancel(
                                registration.getId(),
                                wechatUser.getId(),
                                registration.getPatientId(),
                                registration.getPatientName()
                        );
                    }
                }
            }
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchGenerate(Long doctorId, LocalDate startDate, LocalDate endDate, List<String> timeSlots) {
        return batchGenerate(doctorId, startDate, endDate, timeSlots, null, null, null, null, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchGenerate(Long doctorId, LocalDate startDate, LocalDate endDate, List<String> timeSlots,
                                 Integer maxCount, java.math.BigDecimal registrationFee, List<Integer> weekdays,
                                 Long departmentId, String departmentName, String doctorName) {
        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null && departmentId == null) {
            throw new RuntimeException("医生不存在");
        }

        Long finalDepartmentId = departmentId != null ? departmentId : (doctor != null ? doctor.getDepartmentId() : null);
        String finalDepartmentName = departmentName != null ? departmentName : (doctor != null ? doctor.getDepartmentName() : null);
        String finalDoctorName = doctorName != null ? doctorName : (doctor != null ? doctor.getName() : null);
        java.math.BigDecimal finalFee = registrationFee != null ? registrationFee : (doctor != null ? doctor.getConsultationFee() : java.math.BigDecimal.ZERO);
        int finalMaxCount = maxCount != null ? maxCount : 30;

        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            int dayOfWeek = date.getDayOfWeek().getValue();
            if (dayOfWeek == 7) dayOfWeek = 0;

            if (weekdays == null || weekdays.isEmpty() || weekdays.contains(dayOfWeek)) {
                for (String timeSlot : timeSlots) {
                    DoctorSchedule existing = lambdaQuery()
                            .eq(DoctorSchedule::getDoctorId, doctorId)
                            .eq(DoctorSchedule::getScheduleDate, date)
                            .eq(DoctorSchedule::getTimeSlot, timeSlot)
                            .one();
                    if (existing == null) {
                        DoctorSchedule schedule = new DoctorSchedule();
                        schedule.setDoctorId(doctorId);
                        schedule.setDoctorName(finalDoctorName);
                        schedule.setDepartmentId(finalDepartmentId);
                        schedule.setDepartmentName(finalDepartmentName);
                        schedule.setScheduleDate(date);
                        schedule.setTimeSlot(timeSlot);

                        if ("上午".equals(timeSlot) || "AM".equals(timeSlot) || "morning".equals(timeSlot)) {
                            schedule.setStartTime(LocalTime.of(8, 0));
                            schedule.setEndTime(LocalTime.of(12, 0));
                        } else if ("下午".equals(timeSlot) || "PM".equals(timeSlot) || "afternoon".equals(timeSlot)) {
                            schedule.setStartTime(LocalTime.of(14, 0));
                            schedule.setEndTime(LocalTime.of(17, 30));
                        } else {
                            schedule.setStartTime(LocalTime.of(8, 0));
                            schedule.setEndTime(LocalTime.of(17, 30));
                        }

                        schedule.setMaxCount(finalMaxCount);
                        schedule.setRemainingCount(finalMaxCount);
                        schedule.setRegistrationFee(finalFee);
                        schedule.setIsSuspended(0);
                        schedule.setStatus(1);
                        schedule.setCreateTime(LocalDateTime.now());
                        schedule.setUpdateTime(LocalDateTime.now());
                        save(schedule);
                    }
                }
            }
            date = date.plusDays(1);
        }
        return true;
    }

    private List<DoctorScheduleVO> convertToVOList(List<DoctorSchedule> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> doctorIds = list.stream().map(DoctorSchedule::getDoctorId).distinct().collect(Collectors.toList());
        List<Doctor> doctors = doctorMapper.selectBatchIds(doctorIds);
        Map<Long, String> doctorTitleMap = doctors.stream().collect(Collectors.toMap(Doctor::getId, Doctor::getTitle));
        List<DoctorScheduleVO> voList = new ArrayList<>();
        for (DoctorSchedule schedule : list) {
            DoctorScheduleVO vo = convertToVO(schedule);
            vo.setDoctorTitle(doctorTitleMap.get(schedule.getDoctorId()));
            voList.add(vo);
        }
        return voList;
    }

    private DoctorScheduleVO convertToVO(DoctorSchedule schedule) {
        DoctorScheduleVO vo = new DoctorScheduleVO();
        BeanUtils.copyProperties(schedule, vo);
        return vo;
    }
}
