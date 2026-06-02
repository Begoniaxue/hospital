package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.dto.PatientFamilyDTO;
import com.hospital.entity.Patient;
import com.hospital.entity.PatientFamily;
import com.hospital.service.PatientFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mini/family")
public class MiniPatientFamilyController {

    @Autowired
    private PatientFamilyService patientFamilyService;

    @GetMapping("/list")
    public Result<List<PatientFamily>> getFamilyList(@RequestParam Long wechatUserId) {
        List<PatientFamily> list = patientFamilyService.getFamilyList(wechatUserId);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<Map<String, Object>> addFamilyMember(@RequestBody PatientFamilyDTO dto) {
        Map<String, Object> result = patientFamilyService.addFamilyMember(dto);
        Boolean success = (Boolean) result.get("success");
        String message = (String) result.get("message");
        if (success) {
            return Result.success(message, result);
        } else {
            return Result.error(message);
        }
    }

    @PutMapping("/update")
    public Result<?> updateFamilyMember(@RequestBody PatientFamily family) {
        boolean success = patientFamilyService.updateFamilyMember(family);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteFamilyMember(@PathVariable Long id) {
        boolean success = patientFamilyService.deleteFamilyMember(id);
        if (!success) {
            return Result.error("该就诊人已有就诊记录，不可删除，仅可禁用");
        }
        return Result.success("删除成功");
    }

    @PutMapping("/disable/{id}")
    public Result<?> disableFamilyMember(@PathVariable Long id) {
        boolean success = patientFamilyService.disableFamilyMember(id);
        return success ? Result.success("禁用成功") : Result.error("禁用失败");
    }

    @PostMapping("/switchPatient")
    public Result<Patient> switchCurrentPatient(@RequestParam Long wechatUserId, @RequestParam Long patientId) {
        Patient patient = patientFamilyService.switchCurrentPatient(wechatUserId, patientId);
        return Result.success("切换成功", patient);
    }
}
