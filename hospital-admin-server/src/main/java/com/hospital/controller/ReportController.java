package com.hospital.controller;

import com.alibaba.excel.EasyExcel;
import com.hospital.common.Result;
import com.hospital.dto.*;
import com.hospital.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/registration")
    @PreAuthorize("hasAuthority('report:registration:query')")
    public Result<?> registration(@RequestParam(required = false) String startDate,
                                  @RequestParam(required = false) String endDate,
                                  @RequestParam(required = false) String department,
                                  @RequestParam(required = false) String format,
                                  HttpServletResponse response) throws IOException {
        List<RegistrationReportDTO> list = reportService.getRegistrationReport(startDate, endDate, department);
        if ("excel".equals(format)) {
            exportExcel(response, list, RegistrationExcelVO.class, "挂号量统计");
            return null;
        }
        return Result.success(list);
    }

    @GetMapping("/registration/summary")
    @PreAuthorize("hasAuthority('report:registration:query')")
    public Result<Map<String, Object>> registrationSummary(@RequestParam(required = false) String startDate,
                                                           @RequestParam(required = false) String endDate,
                                                           @RequestParam(required = false) String department) {
        List<RegistrationReportDTO> list = reportService.getRegistrationReport(startDate, endDate, department);
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCount", list.size());
        summary.put("list", list);
        return Result.success(summary);
    }

    @GetMapping("/outpatient")
    @PreAuthorize("hasAuthority('report:outpatient:query')")
    public Result<?> outpatient(@RequestParam(required = false) String startDate,
                                @RequestParam(required = false) String endDate,
                                @RequestParam(required = false) String department,
                                @RequestParam(required = false) Long doctorId,
                                @RequestParam(required = false) String format,
                                HttpServletResponse response) throws IOException {
        List<OutpatientReportDTO> list = reportService.getOutpatientReport(startDate, endDate, department, doctorId);
        if ("excel".equals(format)) {
            exportExcel(response, list, OutpatientExcelVO.class, "门诊量统计");
            return null;
        }
        return Result.success(list);
    }

    @GetMapping("/outpatient/summary")
    @PreAuthorize("hasAuthority('report:outpatient:query')")
    public Result<Map<String, Object>> outpatientSummary(@RequestParam(required = false) String startDate,
                                                         @RequestParam(required = false) String endDate,
                                                         @RequestParam(required = false) String department,
                                                         @RequestParam(required = false) Long doctorId) {
        List<OutpatientReportDTO> list = reportService.getOutpatientReport(startDate, endDate, department, doctorId);
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCount", list.size());
        summary.put("list", list);
        return Result.success(summary);
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasAuthority('report:doctor:query')")
    public Result<?> doctor(@RequestParam(required = false) String startDate,
                            @RequestParam(required = false) String endDate,
                            @RequestParam(required = false) String department,
                            @RequestParam(required = false) String format,
                            HttpServletResponse response) throws IOException {
        List<DoctorWorkloadDTO> list = reportService.getDoctorWorkloadReport(startDate, endDate, department);
        if ("excel".equals(format)) {
            exportExcel(response, list, DoctorWorkloadExcelVO.class, "医生接诊量统计");
            return null;
        }
        return Result.success(list);
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasAuthority('report:revenue:query')")
    public Result<?> revenue(@RequestParam(required = false) String startDate,
                             @RequestParam(required = false) String endDate,
                             @RequestParam(required = false) String dimension,
                             @RequestParam(required = false) String department,
                             @RequestParam(required = false) Long doctorId,
                             @RequestParam(required = false) String format,
                             HttpServletResponse response) throws IOException {
        List<RevenueReportDTO> list = reportService.getRevenueReport(startDate, endDate, dimension, department, doctorId);
        if ("excel".equals(format)) {
            exportExcel(response, list, RevenueExcelVO.class, "营收统计");
            return null;
        }
        return Result.success(list);
    }

    @GetMapping("/revenue/summary")
    @PreAuthorize("hasAuthority('report:revenue:query')")
    public Result<Map<String, Object>> revenueSummary(@RequestParam(required = false) String startDate,
                                                      @RequestParam(required = false) String endDate,
                                                      @RequestParam(required = false) String dimension,
                                                      @RequestParam(required = false) String department,
                                                      @RequestParam(required = false) Long doctorId) {
        List<RevenueReportDTO> list = reportService.getRevenueReport(startDate, endDate, dimension, department, doctorId);
        Map<String, Object> summary = new HashMap<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal registrationFee = BigDecimal.ZERO;
        BigDecimal drugFee = BigDecimal.ZERO;
        BigDecimal examFee = BigDecimal.ZERO;
        BigDecimal treatmentFee = BigDecimal.ZERO;
        BigDecimal inpatientFee = BigDecimal.ZERO;
        BigDecimal otherFee = BigDecimal.ZERO;
        for (RevenueReportDTO dto : list) {
            totalAmount = totalAmount.add(dto.getTotalAmount() != null ? dto.getTotalAmount() : BigDecimal.ZERO);
            registrationFee = registrationFee.add(dto.getRegistrationFee() != null ? dto.getRegistrationFee() : BigDecimal.ZERO);
            drugFee = drugFee.add(dto.getDrugFee() != null ? dto.getDrugFee() : BigDecimal.ZERO);
            examFee = examFee.add(dto.getExamFee() != null ? dto.getExamFee() : BigDecimal.ZERO);
            treatmentFee = treatmentFee.add(dto.getTreatmentFee() != null ? dto.getTreatmentFee() : BigDecimal.ZERO);
            inpatientFee = inpatientFee.add(dto.getInpatientFee() != null ? dto.getInpatientFee() : BigDecimal.ZERO);
            otherFee = otherFee.add(dto.getOtherFee() != null ? dto.getOtherFee() : BigDecimal.ZERO);
        }
        summary.put("totalAmount", totalAmount);
        summary.put("registrationFee", registrationFee);
        summary.put("drugFee", drugFee);
        summary.put("examFee", examFee);
        summary.put("treatmentFee", treatmentFee);
        summary.put("inpatientFee", inpatientFee);
        summary.put("otherFee", otherFee);
        summary.put("list", list);
        return Result.success(summary);
    }

    @GetMapping("/drug")
    @PreAuthorize("hasAuthority('report:drug:query')")
    public Result<?> drug(@RequestParam(required = false) String startDate,
                          @RequestParam(required = false) String endDate,
                          @RequestParam(required = false) String format,
                          HttpServletResponse response) throws IOException {
        List<DrugStockReportDTO> list = reportService.getDrugStockReport(startDate, endDate);
        if ("excel".equals(format)) {
            exportExcel(response, list, DrugStockExcelVO.class, "药品进销存报表");
            return null;
        }
        return Result.success(list);
    }

    @GetMapping("/trend")
    @PreAuthorize("hasAuthority('report:trend:query')")
    public Result<?> trend(@RequestParam(required = false) String startDate,
                           @RequestParam(required = false) String endDate,
                           @RequestParam(required = false) String format,
                           HttpServletResponse response) throws IOException {
        List<PatientTrendDTO> list = reportService.getPatientTrendReport(startDate, endDate);
        if ("excel".equals(format)) {
            exportExcel(response, list, PatientTrendDTO.class, "患者就诊趋势图");
            return null;
        }
        return Result.success(list);
    }

    private <T> void exportExcel(HttpServletResponse response, List<?> data, Class<T> clazz, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), clazz)
                .sheet(fileName)
                .doWrite(data);
    }
}
