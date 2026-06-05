package com.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.DoctorSchedule;
import com.hospital.service.DoctorScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/schedule")
public class AdminScheduleController {

    @Autowired
    private DoctorScheduleService doctorScheduleService;

    @GetMapping("/page")
    public Result<PageResult<DoctorSchedule>> page(PageQuery query,
                                                   @RequestParam(required = false) Long doctorId,
                                                   @RequestParam(required = false) Long departmentId,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                   @RequestParam(required = false) Integer isSuspended,
                                                   @RequestParam(required = false) Integer status) {
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
        if (isSuspended != null) {
            wrapper.eq("is_suspended", isSuspended);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("schedule_date", "doctor_id");
        Page<DoctorSchedule> page = doctorScheduleService.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return Result.success(PageResult.of(page));
    }

    @GetMapping("/{id}")
    public Result<DoctorSchedule> getById(@PathVariable Long id) {
        DoctorSchedule schedule = doctorScheduleService.getById(id);
        return Result.success(schedule);
    }

    @PostMapping
    public Result<Void> add(@RequestBody DoctorSchedule schedule) {
        doctorScheduleService.save(schedule);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody DoctorSchedule schedule) {
        doctorScheduleService.updateById(schedule);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        doctorScheduleService.removeById(id);
        return Result.success();
    }

    @PostMapping("/suspend/{id}")
    public Result<Void> suspend(@PathVariable Long id, @RequestParam String reason) {
        doctorScheduleService.suspend(id, reason);
        return Result.success();
    }

    @PostMapping("/batch-generate")
    public Result<Void> batchGenerate(@RequestParam Long doctorId,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                      @RequestParam(required = false) Integer maxCount,
                                      @RequestParam(required = false) java.math.BigDecimal registrationFee,
                                      @RequestParam(required = false) List<Integer> weekdays,
                                      @RequestParam(required = false) Long departmentId,
                                      @RequestParam(required = false) String departmentName,
                                      @RequestParam(required = false) String doctorName,
                                      @RequestBody List<String> timeSlots) {
        doctorScheduleService.batchGenerate(doctorId, startDate, endDate, timeSlots, 
            maxCount, registrationFee, weekdays, departmentId, departmentName, doctorName);
        return Result.success();
    }
}
