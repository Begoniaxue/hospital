package com.hospital.dto;

import com.hospital.entity.SysRole;

import java.util.List;

public class RoleDTO {
    private SysRole role;
    private List<Long> permissionIds;

    public SysRole getRole() {
        return role;
    }

    public void setRole(SysRole role) {
        this.role = role;
    }

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
