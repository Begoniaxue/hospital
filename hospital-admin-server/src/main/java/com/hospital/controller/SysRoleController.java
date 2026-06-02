package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.dto.RoleDTO;
import com.hospital.entity.SysRole;
import com.hospital.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/list")
    public Result<List<SysRole>> list() {
        return Result.success(sysRoleService.list());
    }

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<PageResult<SysRole>> page(PageQuery pageQuery,
                                           @RequestParam(required = false) String keyword) {
        return Result.success(sysRoleService.getRolePage(pageQuery, keyword));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<SysRole> getById(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        role.setPermissionIds(sysRoleService.getPermissionIdsByRoleId(id));
        return Result.success(role);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<?> save(@RequestBody RoleDTO roleDTO) {
        boolean success = sysRoleService.saveRole(roleDTO.getRole(), roleDTO.getPermissionIds());
        return success ? Result.success("新增成功") : Result.error("新增失败");
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<?> update(@RequestBody RoleDTO roleDTO) {
        boolean success = sysRoleService.updateRole(roleDTO.getRole(), roleDTO.getPermissionIds());
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = sysRoleService.deleteRole(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PutMapping("/permission")
    @PreAuthorize("hasAuthority('system:role:permission')")
    public Result<?> assignPermissions(@RequestParam Long roleId, @RequestBody List<Long> permissionIds) {
        boolean success = sysRoleService.assignPermissions(roleId, permissionIds);
        return success ? Result.success("权限分配成功") : Result.error("权限分配失败");
    }
}
