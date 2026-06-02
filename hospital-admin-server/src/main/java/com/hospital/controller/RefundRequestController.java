package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.dto.RefundApplyDTO;
import com.hospital.dto.RefundAuditDTO;
import com.hospital.entity.RefundRequest;
import com.hospital.entity.SysUser;
import com.hospital.service.RefundRequestService;
import com.hospital.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settlement/refund")
public class RefundRequestController {

    @Autowired
    private RefundRequestService refundRequestService;

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('settlement:refund:query')")
    public Result<PageResult<RefundRequest>> page(PageQuery pageQuery,
                                                  @RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) Integer status,
                                                  @RequestParam(required = false) String startTime,
                                                  @RequestParam(required = false) String endTime) {
        return Result.success(refundRequestService.getRefundPage(pageQuery, keyword, status, startTime, endTime));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('settlement:refund:query')")
    public Result<RefundRequest> getById(@PathVariable Long id) {
        return Result.success(refundRequestService.getRefundDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('settlement:refund:apply')")
    public Result<?> apply(@RequestBody RefundApplyDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser currentUser = sysUserService.getByUsername(username);
        boolean success = refundRequestService.applyRefund(dto, currentUser.getId(), currentUser.getUsername());
        return success ? Result.success("申请成功") : Result.error("申请失败");
    }

    @PutMapping("/audit")
    @PreAuthorize("hasAuthority('settlement:refund:audit')")
    public Result<?> audit(@RequestBody RefundAuditDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser currentUser = sysUserService.getByUsername(username);
        boolean success = refundRequestService.auditRefund(dto, currentUser.getId(), currentUser.getUsername());
        return success ? Result.success("审核成功") : Result.error("审核失败");
    }
}
