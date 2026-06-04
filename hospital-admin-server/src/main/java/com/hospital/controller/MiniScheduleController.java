package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.dto.DoctorScheduleVO;
import com.hospital.entity.DoctorSchedule;
import com.hospital.service.DoctorScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/mini/schedule")
public class MiniScheduleController {

    @Autowired
    private DoctorScheduleService doctorScheduleService;

    @GetMapping("/doctor/{doctorId}")
    public Result<List<DoctorScheduleVO>> getDoctorSchedules(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<DoctorScheduleVO> schedules = doctorScheduleService.getByDoctorAndDateRange(doctorId, startDate, endDate);
        return Result.success(schedules);
    }

    @GetMapping("/department/{departmentId}")
    public Result<List<DoctorSchedule>> getDepartmentSchedules(
            @PathVariable Long departmentId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<DoctorSchedule> schedules = doctorScheduleService.getByDepartmentAndDate(departmentId, date);
        return Result.success(schedules);
    }

    @GetMapping("/today")
    public Result<List<DoctorSchedule>> getTodaySchedules() {
        List<DoctorSchedule> schedules = doctorScheduleService.getByDate(LocalDate.now());
        return Result.success(schedules);
    }

    @GetMapping("/{id}")
    public Result<DoctorSchedule> getScheduleDetail(@PathVariable Long id) {
        DoctorSchedule schedule = doctorScheduleService.getById(id);
        return Result.success(schedule);
    }
}
