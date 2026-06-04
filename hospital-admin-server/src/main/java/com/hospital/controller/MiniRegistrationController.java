package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.dto.RegistrationCancelDTO;
import com.hospital.dto.RegistrationCreateDTO;
import com.hospital.dto.RegistrationPayDTO;
import com.hospital.dto.RegistrationVO;
import com.hospital.entity.SysUser;
import com.hospital.entity.WechatUser;
import com.hospital.service.RegistrationService;
import com.hospital.service.SysUserService;
import com.hospital.service.WechatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/mini/registration")
public class MiniRegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private WechatUserService wechatUserService;

    @PostMapping("/create")
    public Result<RegistrationVO> createRegistration(@RequestBody RegistrationCreateDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long operatorId = null;
        String operatorName = null;

        if (username.startsWith("WECHAT_")) {
            String openid = username.substring(7);
            WechatUser wechatUser = wechatUserService.getByOpenid(openid);
            if (wechatUser != null) {
                operatorId = wechatUser.getId();
                operatorName = wechatUser.getNickname();
            }
            if (operatorId == null) {
                operatorId = 0L;
                operatorName = "小程序用户";
            }
        } else {
            SysUser currentUser = sysUserService.getByUsername(username);
            if (currentUser != null) {
                operatorId = currentUser.getId();
                operatorName = currentUser.getNickname();
            }
        }

        RegistrationVO registration = registrationService.create(dto, operatorId, operatorName);
        return Result.success(registration);
    }

    @PostMapping("/pay")
    public Result<RegistrationVO> payRegistration(@RequestBody RegistrationPayDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long operatorId = null;
        String operatorName = null;

        if (username.startsWith("WECHAT_")) {
            operatorId = 0L;
            operatorName = "小程序用户";
        } else {
            SysUser currentUser = sysUserService.getByUsername(username);
            if (currentUser != null) {
                operatorId = currentUser.getId();
                operatorName = currentUser.getNickname();
            }
        }

        RegistrationVO registration = registrationService.pay(dto, operatorId, operatorName);
        return Result.success(registration);
    }

    @PostMapping("/cancel")
    public Result<Boolean> cancelRegistration(@RequestBody RegistrationCancelDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long operatorId = null;
        String operatorName = null;

        if (username.startsWith("WECHAT_")) {
            operatorId = 0L;
            operatorName = "小程序用户";
        } else {
            SysUser currentUser = sysUserService.getByUsername(username);
            if (currentUser != null) {
                operatorId = currentUser.getId();
                operatorName = currentUser.getNickname();
            }
        }

        boolean result = registrationService.cancel(dto, operatorId, operatorName);
        return Result.success(result);
    }

    @GetMapping("/patient/{patientId}")
    public Result<List<RegistrationVO>> getPatientRegistrations(
            @PathVariable Long patientId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer limit) {
        List<RegistrationVO> registrations = registrationService.listByPatientId(patientId, status, offset, limit);
        return Result.success(registrations);
    }

    @GetMapping("/{id}")
    public Result<RegistrationVO> getRegistrationDetail(@PathVariable Long id) {
        RegistrationVO registration = registrationService.getDetail(id);
        return Result.success(registration);
    }

    @GetMapping("/checkin/{id}")
    public Result<RegistrationVO> checkin(@PathVariable Long id) {
        RegistrationVO registration = registrationService.checkin(id);
        return Result.success(registration);
    }

    @GetMapping("/finish/{id}")
    public Result<RegistrationVO> finish(@PathVariable Long id) {
        RegistrationVO registration = registrationService.finish(id);
        return Result.success(registration);
    }

    @GetMapping("/queue/{id}")
    public Result<RegistrationVO> getQueueInfo(@PathVariable Long id) {
        RegistrationVO registration = registrationService.getQueueInfo(id);
        return Result.success(registration);
    }

    @PostMapping("/refund/{id}")
    public Result<RegistrationVO> refund(@PathVariable Long id, @RequestParam(required = false) String reason) {
        RegistrationVO registration = registrationService.refund(id, reason);
        return Result.success(registration);
    }

    @GetMapping("/admin/page")
    public Result<PageResult<RegistrationVO>> adminPage(PageQuery query,
                                                        @RequestParam(required = false) String registrationNo,
                                                        @RequestParam(required = false) String patientName,
                                                        @RequestParam(required = false) Long departmentId,
                                                        @RequestParam(required = false) Long doctorId,
                                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                        @RequestParam(required = false) Integer status) {
        PageResult<RegistrationVO> page = registrationService.adminPage(query, registrationNo, patientName,
                departmentId, doctorId, startDate, endDate, status);
        return Result.success(page);
    }
}
