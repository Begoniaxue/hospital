package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.entity.SysPermission;
import com.hospital.mapper.SysPermissionMapper;
import com.hospital.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public List<SysPermission> getPermissionsByUserId(Long userId) {
        return sysPermissionMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public List<SysPermission> getPermissionsByRoleId(Long roleId) {
        return sysPermissionMapper.selectPermissionsByRoleId(roleId);
    }

    @Override
    public List<SysPermission> getMenuTree(Long userId) {
        List<SysPermission> allPermissions = sysPermissionMapper.selectPermissionsByUserId(userId);
        List<SysPermission> menus = allPermissions.stream()
                .filter(p -> p.getType() == 1)
                .collect(Collectors.toList());
        return buildTree(menus, 0L);
    }

    @Override
    public List<SysPermission> getAllMenuTree() {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getStatus, 1);
        wrapper.orderByAsc(SysPermission::getSort);
        List<SysPermission> allPermissions = list(wrapper);
        return buildTree(allPermissions, 0L);
    }

    @Override
    public List<String> getPermissionCodesByUserId(Long userId) {
        List<SysPermission> permissions = sysPermissionMapper.selectPermissionsByUserId(userId);
        return permissions.stream()
                .map(SysPermission::getCode)
                .filter(code -> code != null && !code.isEmpty())
                .collect(Collectors.toList());
    }

    private List<SysPermission> buildTree(List<SysPermission> permissions, Long parentId) {
        List<SysPermission> tree = new ArrayList<>();
        for (SysPermission permission : permissions) {
            if (permission.getParentId().equals(parentId)) {
                permission.setChildren(buildTree(permissions, permission.getId()));
                tree.add(permission);
            }
        }
        return tree;
    }
}
