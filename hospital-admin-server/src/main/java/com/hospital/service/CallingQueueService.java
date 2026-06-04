package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.entity.CallingQueue;

import java.time.LocalDate;
import java.util.List;

public interface CallingQueueService extends IService<CallingQueue> {

    CallingQueue createQueue(Long registrationId, Long doctorId, Long departmentId, LocalDate visitDate, String timeSlot);

    CallingQueue getCurrentNumber(Long doctorId, LocalDate visitDate, String timeSlot);

    List<CallingQueue> getByDoctorAndDate(Long doctorId, LocalDate visitDate);

    CallingQueue getCurrentQueue(Long doctorId, LocalDate visitDate);

    List<CallingQueue> getQueueList(Long doctorId, LocalDate visitDate, String timeSlot);

    CallingQueue callNext(Long doctorId, LocalDate visitDate, String timeSlot);

    CallingQueue recall(Long doctorId, LocalDate visitDate, String timeSlot);

    boolean passNumber(Long doctorId, LocalDate visitDate, String timeSlot);

    boolean completeVisit(Long registrationId);

    Integer getQueuePosition(Long registrationId);

    CallingQueue getByRegistrationId(Long registrationId);
}
