package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.HealthAttachment;
import com.hospital.entity.HealthRecord;
import com.hospital.entity.Patient;
import com.hospital.service.HealthAttachmentService;
import com.hospital.service.HealthRecordService;
import com.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mini/health")
public class MiniHealthController {

    @Autowired
    private HealthRecordService healthRecordService;

    @Autowired
    private HealthAttachmentService healthAttachmentService;

    @Autowired
    private PatientService patientService;

    @GetMapping("/record/{patientId}")
    public Result<HealthRecord> getHealthRecord(@PathVariable Long patientId) {
        HealthRecord record = healthRecordService.getByPatientId(patientId);
        return Result.success(record);
    }

    @PostMapping("/record")
    public Result<?> saveHealthRecord(@RequestBody HealthRecord record) {
        boolean success = healthRecordService.saveOrUpdateHealthRecord(record);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }

    @GetMapping("/patient/{patientId}")
    public Result<Patient> getPatientInfo(@PathVariable Long patientId) {
        Patient patient = patientService.getPatientDetail(patientId);
        return Result.success(patient);
    }

    @PutMapping("/patient")
    public Result<?> updatePatientInfo(@RequestBody Patient patient) {
        boolean success = patientService.updatePatient(patient);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @GetMapping("/attachment/page")
    public Result<PageResult<HealthAttachment>> getAttachmentPage(PageQuery pageQuery,
                                                                  @RequestParam Long patientId,
                                                                  @RequestParam(required = false) String category) {
        PageResult<HealthAttachment> page = healthAttachmentService.getAttachmentPage(pageQuery, patientId, category);
        return Result.success(page);
    }

    @GetMapping("/attachment/list")
    public Result<List<HealthAttachment>> getAttachmentList(@RequestParam Long patientId,
                                                            @RequestParam(required = false) String category) {
        List<HealthAttachment> list = healthAttachmentService.getAttachmentList(patientId, category);
        return Result.success(list);
    }

    @PostMapping("/attachment")
    public Result<?> saveAttachment(@RequestBody HealthAttachment attachment) {
        boolean success = healthAttachmentService.saveAttachment(attachment);
        return success ? Result.success("上传成功") : Result.error("上传失败");
    }

    @DeleteMapping("/attachment/{id}")
    public Result<?> deleteAttachment(@PathVariable Long id) {
        boolean success = healthAttachmentService.deleteAttachment(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
