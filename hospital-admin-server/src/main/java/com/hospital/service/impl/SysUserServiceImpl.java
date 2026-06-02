package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.SysRole;
import com.hospital.entity.SysUser;
import com.hospital.mapper.SysUserMapper;
import com.hospital.service.SysPermissionService;
import com.hospital.service.SysRoleService;
import com.hospital.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysPermissionService sysPermissionService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser getByUsername(String username) {
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user != null) {
            user.setRoles(sysRoleService.getRolesByUserId(user.getId()));
            for (SysRole role : user.getRoles()) {
                role.setPermissions(sysPermissionService.getPermissionsByRoleId(role.getId()));
            }
        }
        return user;
    }

    @Override
    public SysUser getUserWithRolesAndPermissions(Long userId) {
        SysUser user = getById(userId);
        if (user != null) {
            user.setRoles(sysRoleService.getRolesByUserId(userId));
            for (SysRole role : user.getRoles()) {
                role.setPermissions(sysPermissionService.getPermissionsByRoleId(role.getId()));
            }
        }
        return user;
    }

    @Override
    public PageResult<SysUser> getUserPage(PageQuery pageQuery, String keyword) {
        Page<SysUser> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getNickname, keyword)
                    .or().like(SysUser::getPhone, keyword);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        page = page(page, wrapper);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveUser(SysUser user, List<Long> roleIds) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        boolean result = save(user);
        if (result && roleIds != null && !roleIds.isEmpty()) {
            sysUserMapper.insertUserRoles(user.getId(), roleIds);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(SysUser user, List<Long> roleIds) {
        boolean result = updateById(user);
        if (result) {
            sysUserMapper.deleteUserRolesByUserId(user.getId());
            if (roleIds != null && !roleIds.isEmpty()) {
                sysUserMapper.insertUserRoles(user.getId(), roleIds);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        sysUserMapper.deleteUserRolesByUserId(id);
        return removeById(id);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        return updateById(user);
    }

    @Override
    public boolean resetPassword(Long id, String newPassword) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        return updateById(user);
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            return false;
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return updateById(user);
    }
}
