package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.InpatientOrder;
import com.hospital.service.InpatientOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inpatient/order")
public class InpatientOrderController {

    @Autowired
    private InpatientOrderService inpatientOrderService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('inpatient:order:query')")
    public Result<PageResult<InpatientOrder>> page(PageQuery pageQuery,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) Long inpatientId,
                                                   @RequestParam(required = false) Integer status) {
        return Result.success(inpatientOrderService.getOrderPage(pageQuery, keyword, inpatientId, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('inpatient:order:query')")
    public Result<InpatientOrder> getById(@PathVariable Long id) {
        return Result.success(inpatientOrderService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('inpatient:order:add')")
    public Result<?> save(@RequestBody InpatientOrder order) {
        boolean success = inpatientOrderService.saveOrder(order);
        return success ? Result.success("新增医嘱成功") : Result.error("新增医嘱失败");
    }

    @PutMapping("/{id}/execute")
    @PreAuthorize("hasAuthority('inpatient:order:execute')")
    public Result<?> execute(@PathVariable Long id,
                             @RequestParam Long userId,
                             @RequestParam String userName) {
        boolean success = inpatientOrderService.executeOrder(id, userId, userName);
        return success ? Result.success("执行医嘱成功") : Result.error("执行医嘱失败");
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('inpatient:order:cancel')")
    public Result<?> cancel(@PathVariable Long id) {
        boolean success = inpatientOrderService.cancelOrder(id);
        return success ? Result.success("取消医嘱成功") : Result.error("取消医嘱失败");
    }
}
