package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

@TableName("discharge_summary")
public class DischargeSummary {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long inpatientId;

    private String inpatientNo;

    private Long patientId;

    private String patientName;

    private LocalDateTime admissionDate;

    private LocalDateTime dischargeDate;

    private String admissionDiagnosis;

    private String dischargeDiagnosis;

    private String hospitalizationSummary;

    private String examinationSummary;

    private String treatmentSummary;

    private String dischargeAdvice;

    private String followUpAdvice;

    private Long doctorId;

    private String doctorName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Long createBy;

    private Long updateBy;

    @TableLogic
    private Integer deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInpatientId() {
        return inpatientId;
    }

    public void setInpatientId(Long inpatientId) {
        this.inpatientId = inpatientId;
    }

    public String getInpatientNo() {
        return inpatientNo;
    }

    public void setInpatientNo(String inpatientNo) {
        this.inpatientNo = inpatientNo;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public LocalDateTime getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDateTime admissionDate) {
        this.admissionDate = admissionDate;
    }

    public LocalDateTime getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(LocalDateTime dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public String getAdmissionDiagnosis() {
        return admissionDiagnosis;
    }

    public void setAdmissionDiagnosis(String admissionDiagnosis) {
        this.admissionDiagnosis = admissionDiagnosis;
    }

    public String getDischargeDiagnosis() {
        return dischargeDiagnosis;
    }

    public void setDischargeDiagnosis(String dischargeDiagnosis) {
        this.dischargeDiagnosis = dischargeDiagnosis;
    }

    public String getHospitalizationSummary() {
        return hospitalizationSummary;
    }

    public void setHospitalizationSummary(String hospitalizationSummary) {
        this.hospitalizationSummary = hospitalizationSummary;
    }

    public String getExaminationSummary() {
        return examinationSummary;
    }

    public void setExaminationSummary(String examinationSummary) {
        this.examinationSummary = examinationSummary;
    }

    public String getTreatmentSummary() {
        return treatmentSummary;
    }

    public void setTreatmentSummary(String treatmentSummary) {
        this.treatmentSummary = treatmentSummary;
    }

    public String getDischargeAdvice() {
        return dischargeAdvice;
    }

    public void setDischargeAdvice(String dischargeAdvice) {
        this.dischargeAdvice = dischargeAdvice;
    }

    public String getFollowUpAdvice() {
        return followUpAdvice;
    }

    public void setFollowUpAdvice(String followUpAdvice) {
        this.followUpAdvice = followUpAdvice;
    }

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
