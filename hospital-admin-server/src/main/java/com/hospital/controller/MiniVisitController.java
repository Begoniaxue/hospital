package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.entity.VisitCode;
import com.hospital.service.VisitCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mini/visit")
public class MiniVisitController {

    @Autowired
    private VisitCodeService visitCodeService;

    @GetMapping("/code/{patientId}")
    public Result<VisitCode> getVisitCode(@PathVariable Long patientId,
                                          @RequestParam(required = false, defaultValue = "PATIENT") String codeType,
                                          @RequestParam(required = false, defaultValue = "GENERAL") String useScene) {
        VisitCode code = visitCodeService.generateVisitCode(patientId, codeType, useScene);
        return Result.success(code);
    }

    @GetMapping("/code/latest/{patientId}")
    public Result<VisitCode> getLatestVisitCode(@PathVariable Long patientId) {
        VisitCode code = visitCodeService.getLatestByPatientId(patientId);
        return Result.success(code);
    }

    @PostMapping("/code/verify")
    public Result<?> verifyVisitCode(@RequestParam String codeContent) {
        boolean success = visitCodeService.verifyVisitCode(codeContent);
        return success ? Result.success("核验成功") : Result.error("二维码无效或已过期");
    }
}
