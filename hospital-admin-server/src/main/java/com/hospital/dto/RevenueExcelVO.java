package com.hospital.dto;

import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;

public class RevenueExcelVO {

    @ExcelProperty("日期")
    private String date;

    @ExcelProperty("科室")
    private String department;

    @ExcelProperty("挂号费")
    private BigDecimal registrationFee;

    @ExcelProperty("药品费")
    private BigDecimal drugFee;

    @ExcelProperty("检查费")
    private BigDecimal examFee;

    @ExcelProperty("治疗费")
    private BigDecimal treatmentFee;

    @ExcelProperty("住院费")
    private BigDecimal inpatientFee;

    @ExcelProperty("其他费用")
    private BigDecimal otherFee;

    @ExcelProperty("合计")
    private BigDecimal totalAmount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public BigDecimal getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(BigDecimal registrationFee) {
        this.registrationFee = registrationFee;
    }

    public BigDecimal getDrugFee() {
        return drugFee;
    }

    public void setDrugFee(BigDecimal drugFee) {
        this.drugFee = drugFee;
    }

    public BigDecimal getExamFee() {
        return examFee;
    }

    public void setExamFee(BigDecimal examFee) {
        this.examFee = examFee;
    }

    public BigDecimal getTreatmentFee() {
        return treatmentFee;
    }

    public void setTreatmentFee(BigDecimal treatmentFee) {
        this.treatmentFee = treatmentFee;
    }

    public BigDecimal getInpatientFee() {
        return inpatientFee;
    }

    public void setInpatientFee(BigDecimal inpatientFee) {
        this.inpatientFee = inpatientFee;
    }

    public BigDecimal getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(BigDecimal otherFee) {
        this.otherFee = otherFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
