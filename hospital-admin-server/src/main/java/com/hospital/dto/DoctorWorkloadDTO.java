package com.hospital.dto;

import java.math.BigDecimal;

public class DoctorWorkloadDTO {

    private Long doctorId;

    private String doctorName;

    private String department;

    private Integer registrationCount;

    private Integer outpatientCount;

    private Integer inpatientCount;

    private BigDecimal totalAmount;

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

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
