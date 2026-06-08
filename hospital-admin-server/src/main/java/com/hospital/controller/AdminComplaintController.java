package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.Complaint;
import com.hospital.entity.SysUser;
import com.hospital.service.ComplaintService;
import com.hospital.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/complaint")
public class AdminComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/page")
    public Result<PageResult<Complaint>> page(PageQuery query,
                                              @RequestParam(required = false) Integer type,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) String keyword) {
        PageResult<Complaint> page = complaintService.adminPage(query, type, status, keyword);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<Complaint> getById(@PathVariable Long id) {
        Complaint complaint = complaintService.getById(id);
        return Result.success(complaint);
    }

    @PostMapping("/handle")
    public Result<Void> handle(@RequestParam Long id, @RequestParam String reply) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser currentUser = sysUserService.getByUsername(username);
        complaintService.handle(id, currentUser.getId(), currentUser.getUsername(), reply);
        return Result.success();
    }

    @PostMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        complaintService.updateStatus(id, status);
        return Result.success();
    }
}
