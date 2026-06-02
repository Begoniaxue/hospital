package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.dto.PrescriptionAuditDTO;
import com.hospital.entity.Prescription;
import com.hospital.entity.SysUser;
import com.hospital.service.PrescriptionService;
import com.hospital.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pharmacy/prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('pharmacy:prescription:query')")
    public Result<PageResult<Prescription>> page(PageQuery pageQuery,
                                                 @RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) Integer status,
                                                 @RequestParam(required = false) String startTime,
                                                 @RequestParam(required = false) String endTime) {
        return Result.success(prescriptionService.getPrescriptionPage(pageQuery, keyword, status, startTime, endTime));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('pharmacy:prescription:query')")
    public Result<Prescription> getById(@PathVariable Long id) {
        return Result.success(prescriptionService.getPrescriptionDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('pharmacy:prescription:query')")
    public Result<?> save(@RequestBody Prescription prescription) {
        boolean success = prescriptionService.savePrescription(prescription, prescription.getItems());
        return success ? Result.success("新增成功") : Result.error("新增失败");
    }

    @PutMapping("/audit")
    @PreAuthorize("hasAuthority('pharmacy:prescription:audit')")
    public Result<?> audit(@RequestBody PrescriptionAuditDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser currentUser = sysUserService.getByUsername(username);
        boolean success = prescriptionService.auditPrescription(dto, currentUser.getId(), currentUser.getUsername());
        return success ? Result.success("审核成功") : Result.error("审核失败");
    }

    @PutMapping("/dispense/{id}")
    @PreAuthorize("hasAuthority('pharmacy:prescription:dispense')")
    public Result<?> dispense(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser currentUser = sysUserService.getByUsername(username);
        boolean success = prescriptionService.dispensePrescription(id, currentUser.getId(), currentUser.getUsername());
        return success ? Result.success("配药成功") : Result.error("配药失败");
    }

    @PutMapping("/issue/{id}")
    @PreAuthorize("hasAuthority('pharmacy:prescription:issue')")
    public Result<?> issue(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser currentUser = sysUserService.getByUsername(username);
        boolean success = prescriptionService.issuePrescription(id, currentUser.getId(), currentUser.getUsername());
        return success ? Result.success("发药成功") : Result.error("发药失败");
    }
}
