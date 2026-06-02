package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    List<SysPermission> selectPermissionsByUserId(@Param("userId") Long userId);

    List<SysPermission> selectPermissionsByRoleId(@Param("roleId") Long roleId);
}
