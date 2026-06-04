package com.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.dto.RegistrationVO;
import com.hospital.entity.Registration;
import com.hospital.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/registration")
public class AdminRegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @GetMapping("/page")
    public Result<PageResult<RegistrationVO>> page(PageQuery query,
                                                   @RequestParam(required = false) String registrationNo,
                                                   @RequestParam(required = false) String patientName,
                                                   @RequestParam(required = false) Long departmentId,
                                                   @RequestParam(required = false) Long doctorId,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                   @RequestParam(required = false) Integer status) {
        PageResult<RegistrationVO> page = registrationService.adminPage(query, registrationNo, patientName,
                departmentId, doctorId, startDate, endDate, status);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<RegistrationVO> getById(@PathVariable Long id) {
        RegistrationVO registration = registrationService.getDetail(id);
        return Result.success(registration);
    }

    @PostMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Registration registration = registrationService.getById(id);
        if (registration != null) {
            registration.setStatus(status);
            registrationService.updateById(registration);
        }
        return Result.success();
    }

    @PostMapping("/checkin/{id}")
    public Result<Void> checkin(@PathVariable Long id) {
        registrationService.checkin(id);
        return Result.success();
    }

    @PostMapping("/finish/{id}")
    public Result<Void> finish(@PathVariable Long id) {
        registrationService.finish(id);
        return Result.success();
    }

    @PostMapping("/refund/{id}")
    public Result<Void> refund(@PathVariable Long id, @RequestParam(required = false) String reason) {
        registrationService.refund(id, reason);
        return Result.success();
    }

    @GetMapping("/statistics")
    public Result<?> getStatistics(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                   @RequestParam(required = false) Long departmentId) {
        QueryWrapper<Registration> wrapper = new QueryWrapper<>();
        if (startDate != null) {
            wrapper.ge("visit_date", startDate);
        }
        if (endDate != null) {
            wrapper.le("visit_date", endDate);
        }
        if (departmentId != null) {
            wrapper.eq("department_id", departmentId);
        }
        long total = registrationService.count(wrapper);
        long pending = registrationService.count(wrapper.clone().eq("status", 1));
        long completed = registrationService.count(wrapper.clone().eq("status", 2));
        long cancelled = registrationService.count(wrapper.clone().eq("status", 3));
        long refunded = registrationService.count(wrapper.clone().eq("status", 4));

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("total", total);
        result.put("pending", pending);
        result.put("completed", completed);
        result.put("cancelled", cancelled);
        result.put("refunded", refunded);
        return Result.success(result);
    }
}
