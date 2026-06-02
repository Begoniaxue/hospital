package com.hospital.dto;

import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;

public class DoctorWorkloadExcelVO {

    @ExcelProperty("医生姓名")
    private String doctorName;

    @ExcelProperty("科室")
    private String department;

    @ExcelProperty("接诊量")
    private Integer registrationCount;

    @ExcelProperty("门诊量")
    private Integer outpatientCount;

    @ExcelProperty("住院量")
    private Integer inpatientCount;

    @ExcelProperty("总费用")
    private BigDecimal totalAmount;

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getRegistrationCount() {
        return registrationCount;
    }

    public void setRegistrationCount(Integer registrationCount) {
        this.registrationCount = registrationCount;
    }

    public Integer getOutpatientCount() {
        return outpatientCount;
    }

    public void setOutpatientCount(Integer outpatientCount) {
        this.outpatientCount = outpatientCount;
    }

    public Integer getInpatientCount() {
        return inpatientCount;
    }

    public void setInpatientCount(Integer inpatientCount) {
        this.inpatientCount = inpatientCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
