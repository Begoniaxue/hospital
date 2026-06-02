package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.dto.SettlementCreateDTO;
import com.hospital.entity.Settlement;
import com.hospital.entity.SysUser;
import com.hospital.service.SettlementService;
import com.hospital.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/settlement")
public class SettlementController {

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('settlement:list:query')")
    public Result<PageResult<Settlement>> page(PageQuery pageQuery,
                                               @RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) Integer payMethod,
                                               @RequestParam(required = false) Integer status,
                                               @RequestParam(required = false) String startTime,
                                               @RequestParam(required = false) String endTime) {
        return Result.success(settlementService.getSettlementPage(pageQuery, keyword, payMethod, status, startTime, endTime));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('settlement:list:query')")
    public Result<Settlement> getById(@PathVariable Long id) {
        return Result.success(settlementService.getSettlementDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('settlement:list:charge')")
    public Result<?> create(@RequestBody SettlementCreateDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser currentUser = sysUserService.getByUsername(username);
        boolean success = settlementService.createSettlement(dto, currentUser.getId(), currentUser.getUsername());
        return success ? Result.success("收费成功") : Result.error("收费失败");
    }

    @GetMapping("/daily")
    @PreAuthorize("hasAuthority('settlement:report:query')")
    public Result<Map<String, Object>> daily(@RequestParam String date) {
        return Result.success(settlementService.getDailySettlement(date));
    }

    @GetMapping("/monthly")
    @PreAuthorize("hasAuthority('settlement:report:query')")
    public Result<Map<String, Object>> monthly(@RequestParam String month) {
        return Result.success(settlementService.getMonthlySettlement(month));
    }
}
