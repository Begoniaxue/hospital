package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.InpatientDischarge;
import com.hospital.service.InpatientDischargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping("/inpatient/discharge")
public class InpatientDischargeController {

    @Autowired
    private InpatientDischargeService inpatientDischargeService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('inpatient:discharge:query')")
    public Result<PageResult<InpatientDischarge>> page(PageQuery pageQuery,
                                                       @RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) Integer status) {
        return Result.success(inpatientDischargeService.getDischargePage(pageQuery, keyword, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('inpatient:discharge:query')")
    public Result<InpatientDischarge> getById(@PathVariable Long id) {
        return Result.success(inpatientDischargeService.getById(id));
    }

    @PostMapping("/settle")
    @PreAuthorize("hasAuthority('inpatient:discharge:settle')")
    public Result<?> settle(@RequestParam Long inpatientId, @RequestBody InpatientDischarge discharge) {
        InpatientDischarge result = inpatientDischargeService.settleDischarge(inpatientId, discharge);
        return result != null ? Result.success("出院结算成功") : Result.error("出院结算失败");
    }

    @GetMapping("/{id}/print")
    @PreAuthorize("hasAuthority('inpatient:discharge:print')")
    public void print(@PathVariable Long id, HttpServletResponse response) throws IOException {
        byte[] pdfData = inpatientDischargeService.printDischarge(id);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=结算单-" + id + ".pdf");
        response.setContentLength(pdfData.length);
        try (OutputStream out = response.getOutputStream()) {
            out.write(pdfData);
            out.flush();
        }
    }
}
