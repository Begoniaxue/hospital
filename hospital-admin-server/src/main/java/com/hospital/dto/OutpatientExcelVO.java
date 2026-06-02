package com.hospital.dto;

import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;

public class OutpatientExcelVO {

    @ExcelProperty("日期")
    private String date;

    @ExcelProperty("科室")
    private String department;

    @ExcelProperty("医生")
    private String doctorName;

    @ExcelProperty("接诊人数")
    private Integer count;

    @ExcelProperty("诊疗总费用")
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

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
