package com.hospital.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import com.hospital.common.Result;
import com.hospital.dto.LoginDTO;
import com.hospital.dto.UpdatePasswordDTO;
import com.hospital.entity.SysUser;
import com.hospital.service.SysPermissionService;
import com.hospital.service.SysUserService;
import com.hospital.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysPermissionService sysPermissionService;

    @Value("${captcha.expire:120}")
    private Long captchaExpire;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        String captchaKey = "captcha:" + loginDTO.getUuid();
        String storedCaptcha = redisTemplate.opsForValue().get(captchaKey);
        if (storedCaptcha == null) {
            return Result.error("验证码已过期");
        }
        if (!storedCaptcha.equalsIgnoreCase(loginDTO.getCode())) {
            return Result.error("验证码错误");
        }
        redisTemplate.delete(captchaKey);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails.getUsername());

            SysUser user = sysUserService.getByUsername(userDetails.getUsername());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            data.put("permissions", sysPermissionService.getPermissionCodesByUserId(user.getId()));

            return Result.success("登录成功", data);
        } catch (AuthenticationException e) {
            return Result.error("用户名或密码错误");
        }
    }

    @GetMapping("/captcha")
    public Result<Map<String, String>> captcha() {
        String uuid = UUID.randomUUID().toString();
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(111, 36, 4, 4);
        String capText = captcha.getCode();
        redisTemplate.opsForValue().set("captcha:" + uuid, capText, captchaExpire, TimeUnit.SECONDS);

        String base64Image = "data:image/png;base64," + captcha.getImageBase64();

        Map<String, String> data = new HashMap<>();
        data.put("uuid", uuid);
        data.put("img", base64Image);
        return Result.success(data);
    }

    @GetMapping("/userInfo")
    public Result<SysUser> getUserInfo(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error(401, "未登录或登录已过期");
        }
        String token = authHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);
        SysUser user = sysUserService.getByUsername(username);
        return Result.success(user);
    }

    @GetMapping("/menu")
    public Result<?> getMenu(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error(401, "未登录或登录已过期");
        }
        String token = authHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);
        SysUser user = sysUserService.getByUsername(username);
        return Result.success(sysPermissionService.getMenuTree(user.getId()));
    }

    @PostMapping("/updatePassword")
    public Result<?> updatePassword(@RequestBody UpdatePasswordDTO dto, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error(401, "未登录或登录已过期");
        }
        String token = authHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);
        SysUser user = sysUserService.getByUsername(username);
        boolean success = sysUserService.updatePassword(user.getId(), dto.getOldPassword(), dto.getNewPassword());
        return success ? Result.success("密码修改成功") : Result.error("原密码错误");
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        return Result.success("退出成功");
    }
}
