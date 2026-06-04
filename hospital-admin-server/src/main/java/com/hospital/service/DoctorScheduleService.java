package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.dto.DoctorScheduleVO;
import com.hospital.entity.DoctorSchedule;

import java.time.LocalDate;
import java.util.List;

public interface DoctorScheduleService extends IService<DoctorSchedule> {

    PageResult<DoctorScheduleVO> getSchedulePage(PageQuery pageQuery, Long doctorId, Long departmentId, LocalDate startDate, LocalDate endDate);

    DoctorScheduleVO getScheduleDetail(Long id);

    List<DoctorScheduleVO> getByDoctorIdAndDate(Long doctorId, LocalDate scheduleDate);

    List<DoctorScheduleVO> getByDepartmentIdAndDate(Long departmentId, LocalDate scheduleDate);

    List<DoctorScheduleVO> getAvailableSchedule(Long doctorId, LocalDate scheduleDate);

    List<DoctorScheduleVO> getByDoctorAndDateRange(Long doctorId, LocalDate startDate, LocalDate endDate);

    List<DoctorSchedule> getByDepartmentAndDate(Long departmentId, LocalDate date);

    List<DoctorSchedule> getByDate(LocalDate date);

    boolean saveSchedule(DoctorSchedule schedule);

    boolean updateSchedule(DoctorSchedule schedule);

    boolean deleteSchedule(Long id);

    boolean suspendSchedule(Long id, String reason);

    boolean resumeSchedule(Long id);

    boolean decreaseRemainingCount(Long id);

    boolean increaseRemainingCount(Long id);

    boolean suspend(Long id, String reason);

    boolean batchGenerate(Long doctorId, LocalDate startDate, LocalDate endDate, List<String> timeSlots);
}
