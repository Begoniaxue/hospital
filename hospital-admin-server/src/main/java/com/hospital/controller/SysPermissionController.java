package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.entity.SysPermission;
import com.hospital.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/permission")
public class SysPermissionController {

    @Autowired
    private SysPermissionService sysPermissionService;

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:permission:query')")
    public Result<List<SysPermission>> tree() {
        return Result.success(sysPermissionService.getAllMenuTree());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:query')")
    public Result<SysPermission> getById(@PathVariable Long id) {
        return Result.success(sysPermissionService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:permission:add')")
    public Result<?> save(@RequestBody SysPermission permission) {
        boolean success = sysPermissionService.save(permission);
        return success ? Result.success("新增成功") : Result.error("新增失败");
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:permission:edit')")
    public Result<?> update(@RequestBody SysPermission permission) {
        boolean success = sysPermissionService.updateById(permission);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:delete')")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = sysPermissionService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
