package com.hospital.security;

import com.hospital.entity.SysPermission;
import com.hospital.entity.SysRole;
import com.hospital.entity.SysUser;
import com.hospital.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        if (user.getStatus() == 0) {
            throw new UsernameNotFoundException("账号已被禁用");
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        List<SysRole> roles = user.getRoles();
        if (roles != null && !roles.isEmpty()) {
            for (SysRole role : roles) {
                authorities.add(new SimpleGrantedAuthority(role.getRoleCode()));
                if (role.getPermissions() != null) {
                    for (SysPermission perm : role.getPermissions()) {
                        if (perm.getCode() != null && !perm.getCode().isEmpty()) {
                            authorities.add(new SimpleGrantedAuthority(perm.getCode()));
                        }
                    }
                }
            }
        }

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
