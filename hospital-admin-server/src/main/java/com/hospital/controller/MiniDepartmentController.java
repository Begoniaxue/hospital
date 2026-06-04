package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.dto.DepartmentTreeVO;
import com.hospital.entity.Department;
import com.hospital.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mini/department")
public class MiniDepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/tree")
    public Result<List<DepartmentTreeVO>> getDepartmentTree() {
        List<DepartmentTreeVO> tree = departmentService.getDepartmentTree();
        return Result.success(tree);
    }

    @GetMapping("/list/parent/{parentId}")
    public Result<List<Department>> getDepartmentsByParentId(@PathVariable Long parentId) {
        List<Department> departments = departmentService.getByParentId(parentId);
        return Result.success(departments);
    }

    @GetMapping("/level/{level}")
    public Result<List<Department>> getDepartmentsByLevel(@PathVariable Integer level) {
        List<Department> departments = departmentService.getByLevel(level);
        return Result.success(departments);
    }

    @GetMapping("/{id}")
    public Result<Department> getDepartmentDetail(@PathVariable Long id) {
        Department department = departmentService.getById(id);
        return Result.success(department);
    }

    @GetMapping("/search")
    public Result<List<Department>> searchDepartments(@RequestParam String keyword) {
        List<Department> departments = departmentService.search(keyword);
        return Result.success(departments);
    }
}
