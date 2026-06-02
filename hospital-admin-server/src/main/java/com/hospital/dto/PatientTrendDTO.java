package com.hospital.dto;

public class PatientTrendDTO {

    private String date;

    private Integer registrationCount;

    private Integer outpatientCount;

    private Integer inpatientCount;

    private Integer totalCount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
