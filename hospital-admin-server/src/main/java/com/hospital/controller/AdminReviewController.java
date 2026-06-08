package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.DoctorReview;
import com.hospital.entity.SysUser;
import com.hospital.service.DoctorReviewService;
import com.hospital.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/review")
public class AdminReviewController {

    @Autowired
    private DoctorReviewService doctorReviewService;

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/page")
    public Result<PageResult<DoctorReview>> page(PageQuery query,
                                                 @RequestParam(required = false) Long doctorId,
                                                 @RequestParam(required = false) Integer rating,
                                                 @RequestParam(required = false) Integer status) {
        PageResult<DoctorReview> page = doctorReviewService.adminPage(query, doctorId, rating, status);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<DoctorReview> getById(@PathVariable Long id) {
        DoctorReview review = doctorReviewService.getById(id);
        return Result.success(review);
    }

    @PostMapping("/reply")
    public Result<Void> reply(@RequestParam Long id, @RequestParam String reply) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser currentUser = sysUserService.getByUsername(username);
        doctorReviewService.reply(id, currentUser.getId(), currentUser.getUsername(), reply);
        return Result.success();
    }

    @PostMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        doctorReviewService.updateStatus(id, status);
        return Result.success();
    }
}
