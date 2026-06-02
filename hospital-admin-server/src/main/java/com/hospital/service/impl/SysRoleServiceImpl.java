package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.SysRole;
import com.hospital.mapper.SysRoleMapper;
import com.hospital.service.SysPermissionService;
import com.hospital.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysPermissionService sysPermissionService;

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        return sysRoleMapper.selectRolesByUserId(userId);
    }

    @Override
    public PageResult<SysRole> getRolePage(PageQuery pageQuery, String keyword) {
        Page<SysRole> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysRole::getRoleName, keyword)
                    .or().like(SysRole::getRoleCode, keyword);
        }
        wrapper.orderByAsc(SysRole::getSort);
        page = page(page, wrapper);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRole(SysRole role, List<Long> permissionIds) {
        boolean result = save(role);
        if (result && permissionIds != null && !permissionIds.isEmpty()) {
            sysRoleMapper.insertRolePermissions(role.getId(), permissionIds);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(SysRole role, List<Long> permissionIds) {
        boolean result = updateById(role);
        if (result) {
            sysRoleMapper.deleteRolePermissionsByRoleId(role.getId());
            if (permissionIds != null && !permissionIds.isEmpty()) {
                sysRoleMapper.insertRolePermissions(role.getId(), permissionIds);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long id) {
        sysRoleMapper.deleteRolePermissionsByRoleId(id);
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        sysRoleMapper.deleteRolePermissionsByRoleId(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            sysRoleMapper.insertRolePermissions(roleId, permissionIds);
        }
        return true;
    }

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        return sysRoleMapper.selectPermissionIdsByRoleId(roleId);
    }
}
