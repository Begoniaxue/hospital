package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.dto.UserDTO;
import com.hospital.entity.SysUser;
import com.hospital.mapper.SysUserMapper;
import com.hospital.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<PageResult<SysUser>> page(PageQuery pageQuery,
                                              @RequestParam(required = false) String keyword) {
        return Result.success(sysUserService.getUserPage(pageQuery, keyword));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        user.setRoleIds(sysUserMapper.selectRoleIdsByUserId(id));
        return Result.success(user);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result<?> save(@RequestBody UserDTO userDTO) {
        boolean success = sysUserService.saveUser(userDTO.getUser(), userDTO.getRoleIds());
        return success ? Result.success("新增成功") : Result.error("新增失败");
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<?> update(@RequestBody UserDTO userDTO) {
        boolean success = sysUserService.updateUser(userDTO.getUser(), userDTO.getRoleIds());
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = sysUserService.deleteUser(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PutMapping("/status")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<?> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        boolean success = sysUserService.updateStatus(id, status);
        return success ? Result.success("状态修改成功") : Result.error("状态修改失败");
    }

    @PutMapping("/resetPassword")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<?> resetPassword(@RequestParam Long id, @RequestParam String newPassword) {
        boolean success = sysUserService.resetPassword(id, newPassword);
        return success ? Result.success("密码重置成功") : Result.error("密码重置失败");
    }
}
