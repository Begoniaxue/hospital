package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.InpatientDeposit;
import com.hospital.service.InpatientDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inpatient/deposit")
public class InpatientDepositController {

    @Autowired
    private InpatientDepositService inpatientDepositService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('inpatient:deposit:query')")
    public Result<PageResult<InpatientDeposit>> page(PageQuery pageQuery,
                                                     @RequestParam(required = false) String keyword,
                                                     @RequestParam(required = false) Long inpatientId,
                                                     @RequestParam(required = false) Integer status) {
        return Result.success(inpatientDepositService.getDepositPage(pageQuery, keyword, inpatientId, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('inpatient:deposit:query')")
    public Result<InpatientDeposit> getById(@PathVariable Long id) {
        return Result.success(inpatientDepositService.getById(id));
    }

    @GetMapping("/summary/{inpatientId}")
    @PreAuthorize("hasAuthority('inpatient:deposit:query')")
    public Result<Map<String, Object>> summary(@PathVariable Long inpatientId) {
        Map<String, Object> summary = new HashMap<>();
        List<InpatientDeposit> list = inpatientDepositService.lambdaQuery()
                .eq(InpatientDeposit::getInpatientId, inpatientId)
                .list();
        BigDecimal totalPay = list.stream()
                .filter(d -> d.getStatus() == 0)
                .map(InpatientDeposit::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRefund = list.stream()
                .filter(d -> d.getStatus() == 1)
                .map(InpatientDeposit::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("totalPay", totalPay);
        summary.put("totalRefund", totalRefund);
        summary.put("balance", totalPay.subtract(totalRefund));
        return Result.success(summary);
    }

    @PostMapping("/pay")
    @PreAuthorize("hasAuthority('inpatient:deposit:pay')")
    public Result<?> pay(@RequestBody InpatientDeposit deposit) {
        boolean success = inpatientDepositService.payDeposit(deposit);
        return success ? Result.success("缴纳押金成功") : Result.error("缴纳押金失败");
    }

    @PutMapping("/{id}/refund")
    @PreAuthorize("hasAuthority('inpatient:deposit:refund')")
    public Result<?> refund(@PathVariable Long id) {
        boolean success = inpatientDepositService.refundDeposit(id);
        return success ? Result.success("退还押金成功") : Result.error("退还押金失败");
    }
}
