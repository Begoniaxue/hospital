package com.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.Doctor;
import com.hospital.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/doctor")
public class AdminDoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/page")
    public Result<PageResult<Doctor>> page(PageQuery query,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) String jobNo,
                                           @RequestParam(required = false) String title,
                                           @RequestParam(required = false) Long departmentId,
                                           @RequestParam(required = false) Integer status) {
        QueryWrapper<Doctor> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword).or().like("doctor_no", keyword);
        }
        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        if (jobNo != null && !jobNo.isEmpty()) {
            wrapper.like("doctor_no", jobNo);
        }
        if (title != null && !title.isEmpty()) {
            wrapper.like("title", title);
        }
        if (departmentId != null) {
            wrapper.eq("department_id", departmentId);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByAsc("sort", "id");
        Page<Doctor> page = doctorService.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return Result.success(PageResult.of(page));
    }

    @GetMapping("/list")
    public Result<List<Doctor>> list(@RequestParam(required = false) Long departmentId,
                                     @RequestParam(required = false) Integer status) {
        QueryWrapper<Doctor> wrapper = new QueryWrapper<>();
        if (departmentId != null) {
            wrapper.eq("department_id", departmentId);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByAsc("sort", "id");
        List<Doctor> list = doctorService.list(wrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Doctor> getById(@PathVariable Long id) {
        Doctor doctor = doctorService.getById(id);
        return Result.success(doctor);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Doctor doctor) {
        doctorService.save(doctor);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Doctor doctor) {
        doctorService.updateById(doctor);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        doctorService.removeById(id);
        return Result.success();
    }

    @PostMapping("/batch-delete")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        doctorService.removeByIds(ids);
        return Result.success();
    }
}
