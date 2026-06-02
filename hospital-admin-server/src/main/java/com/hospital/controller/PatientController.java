package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.Patient;
import com.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('patient:list:query')")
    public Result<PageResult<Patient>> page(PageQuery pageQuery,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) String startTime,
                                          @RequestParam(required = false) String endTime) {
        return Result.success(patientService.getPatientPage(pageQuery, keyword, startTime, endTime));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('patient:list:query')")
    public Result<Patient> getById(@PathVariable Long id) {
        return Result.success(patientService.getPatientDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('patient:list:add')")
    public Result<?> save(@RequestBody Patient patient) {
        boolean success = patientService.savePatient(patient);
        return success ? Result.success("新增成功") : Result.error("新增失败");
    }

    @PutMapping
    @PreAuthorize("hasAuthority('patient:list:edit')")
    public Result<?> update(@RequestBody Patient patient) {
        boolean success = patientService.updatePatient(patient);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('patient:list:delete')")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = patientService.deletePatient(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
