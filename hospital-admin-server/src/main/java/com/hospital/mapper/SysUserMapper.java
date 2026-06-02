package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper extends BaseMapper<SysUser> {

    SysUser selectByUsername(@Param("username") String username);

    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    void insertUserRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    void deleteUserRolesByUserId(@Param("userId") Long userId);
}
