package com.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.Department;
import com.hospital.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/department")
public class AdminDepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/page")
    public Result<PageResult<Department>> page(PageQuery query,
                                               @RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) Integer status) {
        QueryWrapper<Department> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("dept_name", keyword).or().like("dept_code", keyword);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByAsc("sort", "id");
        Page<Department> page = departmentService.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return Result.success(PageResult.of(page));
    }

    @GetMapping("/list")
    public Result<List<Department>> list(@RequestParam(required = false) Integer status) {
        QueryWrapper<Department> wrapper = new QueryWrapper<>();
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByAsc("sort", "id");
        List<Department> list = departmentService.list(wrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Department> getById(@PathVariable Long id) {
        Department department = departmentService.getById(id);
        return Result.success(department);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Department department) {
        departmentService.save(department);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Department department) {
        departmentService.updateById(department);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.removeById(id);
        return Result.success();
    }

    @PostMapping("/batch-delete")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        departmentService.removeByIds(ids);
        return Result.success();
    }
}
