package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.dto.PatientRealNameDTO;
import com.hospital.dto.SmsCodeDTO;
import com.hospital.dto.WechatLoginDTO;
import com.hospital.service.PatientFamilyService;
import com.hospital.service.WechatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/mini/auth")
public class MiniAuthController {

    @Autowired
    private WechatUserService wechatUserService;

    @Autowired
    private PatientFamilyService patientFamilyService;

    @PostMapping("/login")
    public Result<Map<String, Object>> wechatLogin(@RequestBody WechatLoginDTO dto) {
        Map<String, Object> data = wechatUserService.wechatLogin(dto);
        return Result.success("登录成功", data);
    }

    @PostMapping("/sendSmsCode")
    public Result<?> sendSmsCode(@RequestBody SmsCodeDTO dto) {
        boolean success = wechatUserService.sendSmsCode(dto.getPhone(), dto.getSmsType());
        return success ? Result.success("验证码已发送") : Result.error("验证码发送失败");
    }

    @PostMapping("/verifySmsCode")
    public Result<?> verifySmsCode(@RequestBody SmsCodeDTO dto) {
        boolean success = wechatUserService.verifySmsCode(dto.getPhone(), dto.getCode(), dto.getSmsType());
        return success ? Result.success("验证成功") : Result.error("验证码错误或已过期");
    }

    @PostMapping("/realName")
    public Result<Map<String, Object>> realNameAuth(@Valid @RequestBody PatientRealNameDTO dto) {
        Map<String, Object> result = patientFamilyService.realNameAuth(dto);
        Boolean success = (Boolean) result.get("success");
        String message = (String) result.get("message");
        if (success) {
            return Result.success(message, result);
        } else {
            return Result.error(message);
        }
    }
}
