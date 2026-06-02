package com.hospital.dto;

import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;

public class RegistrationExcelVO {

    @ExcelProperty("日期")
    private String date;

    @ExcelProperty("科室")
    private String department;

    @ExcelProperty("挂号类型")
    private String registrationTypeName;

    @ExcelProperty("挂号数量")
    private Integer count;

    @ExcelProperty("挂号费用")
    private BigDecimal totalFee;

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

    public String getRegistrationTypeName() {
        return registrationTypeName;
    }

    public void setRegistrationTypeName(String registrationTypeName) {
        this.registrationTypeName = registrationTypeName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }
}
