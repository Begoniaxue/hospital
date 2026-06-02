package com.hospital.controller;

import com.alibaba.excel.EasyExcel;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.InpatientFee;
import com.hospital.service.InpatientFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inpatient/fee")
public class InpatientFeeController {

    @Autowired
    private InpatientFeeService inpatientFeeService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('inpatient:fee:query')")
    public Result<PageResult<InpatientFee>> page(PageQuery pageQuery,
                                                 @RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) Long inpatientId,
                                                 @RequestParam(required = false) String feeDate) {
        return Result.success(inpatientFeeService.getFeePage(pageQuery, keyword, inpatientId, feeDate));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('inpatient:fee:query')")
    public Result<InpatientFee> getById(@PathVariable Long id) {
        return Result.success(inpatientFeeService.getById(id));
    }

    @GetMapping("/summary/{inpatientId}")
    @PreAuthorize("hasAuthority('inpatient:fee:query')")
    public Result<Map<String, Object>> summary(@PathVariable Long inpatientId) {
        Map<String, Object> summary = new HashMap<>();
        BigDecimal totalAmount = inpatientFeeService.getTotalAmount(inpatientId);
        summary.put("totalAmount", totalAmount);
        return Result.success(summary);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('inpatient:fee:add')")
    public Result<?> save(@RequestBody InpatientFee fee) {
        boolean success = inpatientFeeService.saveFee(fee);
        return success ? Result.success("新增费用成功") : Result.error("新增费用失败");
    }

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('inpatient:fee:export')")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long inpatientId,
                       @RequestParam(required = false) String feeDate) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("费用明细", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        List<InpatientFee> list = inpatientFeeService.list();
        EasyExcel.write(response.getOutputStream(), InpatientFee.class)
                .sheet("费用明细")
                .doWrite(list);
    }
}
