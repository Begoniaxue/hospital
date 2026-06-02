package com.hospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    List<SysRole> getRolesByUserId(Long userId);

    PageResult<SysRole> getRolePage(PageQuery pageQuery, String keyword);

    boolean saveRole(SysRole role, List<Long> permissionIds);

    boolean updateRole(SysRole role, List<Long> permissionIds);

    boolean deleteRole(Long id);

    boolean assignPermissions(Long roleId, List<Long> permissionIds);

    List<Long> getPermissionIdsByRoleId(Long roleId);
}
