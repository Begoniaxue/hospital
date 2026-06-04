package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.entity.CallingQueue;
import com.hospital.entity.Department;
import com.hospital.entity.Doctor;
import com.hospital.entity.Registration;
import com.hospital.mapper.CallingQueueMapper;
import com.hospital.mapper.DepartmentMapper;
import com.hospital.mapper.DoctorMapper;
import com.hospital.mapper.RegistrationMapper;
import com.hospital.service.CallingQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CallingQueueServiceImpl extends ServiceImpl<CallingQueueMapper, CallingQueue> implements CallingQueueService {

    @Autowired
    private CallingQueueMapper callingQueueMapper;

    @Autowired
    private RegistrationMapper registrationMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public CallingQueue createQueue(Long registrationId, Long doctorId, Long departmentId, LocalDate visitDate, String timeSlot) {
        Registration registration = registrationMapper.selectById(registrationId);
        if (registration == null) {
            return null;
        }
        Doctor doctor = doctorMapper.selectById(doctorId);
        Department department = departmentMapper.selectById(departmentId);
        int maxQueueNumber = getMaxQueueNumber(doctorId, visitDate, timeSlot);
        CallingQueue queue = new CallingQueue();
        queue.setRegistrationId(registrationId);
        queue.setRegistrationNo(registration.getRegistrationNo());
        queue.setPatientId(registration.getPatientId());
        queue.setPatientName(registration.getPatientName());
        queue.setDoctorId(doctorId);
        queue.setDoctorName(doctor != null ? doctor.getName() : "");
        queue.setDepartmentId(departmentId);
        queue.setDepartmentName(department != null ? department.getDeptName() : "");
        queue.setVisitDate(visitDate);
        queue.setTimeSlot(timeSlot);
        queue.setQueueNumber(maxQueueNumber + 1);
        queue.setCurrentNumber(0);
        queue.setQueueStatus(0);
        queue.setRoomNo(department != null ? department.getLocation() : "");
        queue.setCreateTime(LocalDateTime.now());
        queue.setUpdateTime(LocalDateTime.now());
        save(queue);
        return queue;
    }

    @Override
    public CallingQueue getCurrentNumber(Long doctorId, LocalDate visitDate, String timeSlot) {
        return lambdaQuery()
                .eq(CallingQueue::getDoctorId, doctorId)
                .eq(CallingQueue::getVisitDate, visitDate)
                .eq(CallingQueue::getTimeSlot, timeSlot)
                .eq(CallingQueue::getQueueStatus, 1)
                .one();
    }

    @Override
    public List<CallingQueue> getQueueList(Long doctorId, LocalDate visitDate, String timeSlot) {
        return lambdaQuery()
                .eq(CallingQueue::getDoctorId, doctorId)
                .eq(CallingQueue::getVisitDate, visitDate)
                .eq(CallingQueue::getTimeSlot, timeSlot)
                .orderByAsc(CallingQueue::getQueueNumber)
                .list();
    }

    @Override
    public CallingQueue callNext(Long doctorId, LocalDate visitDate, String timeSlot) {
        CallingQueue current = getCurrentNumber(doctorId, visitDate, timeSlot);
        if (current != null) {
            current.setQueueStatus(2);
            current.setUpdateTime(LocalDateTime.now());
            updateById(current);
        }
        CallingQueue next = lambdaQuery()
                .eq(CallingQueue::getDoctorId, doctorId)
                .eq(CallingQueue::getVisitDate, visitDate)
                .eq(CallingQueue::getTimeSlot, timeSlot)
                .eq(CallingQueue::getQueueStatus, 0)
                .orderByAsc(CallingQueue::getQueueNumber)
                .last("LIMIT 1")
                .one();
        if (next != null) {
            next.setQueueStatus(1);
            next.setUpdateTime(LocalDateTime.now());
            updateById(next);
        }
        return next;
    }

    @Override
    public CallingQueue recall(Long doctorId, LocalDate visitDate, String timeSlot) {
        CallingQueue current = getCurrentNumber(doctorId, visitDate, timeSlot);
        if (current != null) {
            current.setUpdateTime(LocalDateTime.now());
            updateById(current);
        }
        return current;
    }

    @Override
    public boolean passNumber(Long doctorId, LocalDate visitDate, String timeSlot) {
        CallingQueue current = getCurrentNumber(doctorId, visitDate, timeSlot);
        if (current == null) {
            return false;
        }
        current.setQueueStatus(3);
        current.setUpdateTime(LocalDateTime.now());
        return updateById(current);
    }

    @Override
    public boolean completeVisit(Long registrationId) {
        CallingQueue queue = lambdaQuery().eq(CallingQueue::getRegistrationId, registrationId).one();
        if (queue == null) {
            return false;
        }
        queue.setQueueStatus(2);
        queue.setUpdateTime(LocalDateTime.now());
        return updateById(queue);
    }

    @Override
    public Integer getQueuePosition(Long registrationId) {
        CallingQueue queue = lambdaQuery().eq(CallingQueue::getRegistrationId, registrationId).one();
        if (queue == null) {
            return null;
        }
        long waitingCount = lambdaQuery()
                .eq(CallingQueue::getDoctorId, queue.getDoctorId())
                .eq(CallingQueue::getVisitDate, queue.getVisitDate())
                .eq(CallingQueue::getTimeSlot, queue.getTimeSlot())
                .eq(CallingQueue::getQueueStatus, 0)
                .lt(CallingQueue::getQueueNumber, queue.getQueueNumber())
                .count();
        return (int) waitingCount + 1;
    }

    @Override
    public List<CallingQueue> getByDoctorAndDate(Long doctorId, LocalDate visitDate) {
        return lambdaQuery()
                .eq(CallingQueue::getDoctorId, doctorId)
                .eq(CallingQueue::getVisitDate, visitDate)
                .orderByAsc(CallingQueue::getTimeSlot, CallingQueue::getQueueNumber)
                .list();
    }

    @Override
    public CallingQueue getCurrentQueue(Long doctorId, LocalDate visitDate) {
        return lambdaQuery()
                .eq(CallingQueue::getDoctorId, doctorId)
                .eq(CallingQueue::getVisitDate, visitDate)
                .eq(CallingQueue::getQueueStatus, 1)
                .one();
    }

    @Override
    public CallingQueue getByRegistrationId(Long registrationId) {
        return lambdaQuery().eq(CallingQueue::getRegistrationId, registrationId).one();
    }

    private int getMaxQueueNumber(Long doctorId, LocalDate visitDate, String timeSlot) {
        QueryWrapper<CallingQueue> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(MAX(queue_number), 0) as max_number")
                .eq("doctor_id", doctorId)
                .eq("visit_date", visitDate)
                .eq("time_slot", timeSlot);
        Map<String, Object> result = getMap(wrapper);
        if (result == null || result.get("max_number") == null) {
            return 0;
        }
        return ((Number) result.get("max_number")).intValue();
    }
}
