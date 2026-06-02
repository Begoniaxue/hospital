package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.entity.SysPermission;

import java.util.List;

public interface SysPermissionService extends IService<SysPermission> {

    List<SysPermission> getPermissionsByUserId(Long userId);

    List<SysPermission> getPermissionsByRoleId(Long roleId);

    List<SysPermission> getMenuTree(Long userId);

    List<SysPermission> getAllMenuTree();

    List<String> getPermissionCodesByUserId(Long userId);
}
