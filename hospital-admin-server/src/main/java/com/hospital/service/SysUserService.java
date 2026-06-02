package com.hospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.SysUser;

import java.util.List;

public interface SysUserService extends IService<SysUser> {

    SysUser getByUsername(String username);

    SysUser getUserWithRolesAndPermissions(Long userId);

    PageResult<SysUser> getUserPage(PageQuery pageQuery, String keyword);

    boolean saveUser(SysUser user, List<Long> roleIds);

    boolean updateUser(SysUser user, List<Long> roleIds);

    boolean deleteUser(Long id);

    boolean updateStatus(Long id, Integer status);

    boolean resetPassword(Long id, String newPassword);

    boolean updatePassword(Long userId, String oldPassword, String newPassword);
}
