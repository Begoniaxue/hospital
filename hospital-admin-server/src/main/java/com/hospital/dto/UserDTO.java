package com.hospital.dto;

import com.hospital.entity.SysUser;

import java.util.List;

public class UserDTO {
    private SysUser user;
    private List<Long> roleIds;

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
