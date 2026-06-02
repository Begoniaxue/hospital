package com.hospital.dto;

import java.math.BigDecimal;

public class RegistrationReportDTO {

    private String date;

    private Integer count;

    private String department;

    private Integer registrationType;

    private BigDecimal totalFee;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(Integer registrationType) {
        this.registrationType = registrationType;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }
}
