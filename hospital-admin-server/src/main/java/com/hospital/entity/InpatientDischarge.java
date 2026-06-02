package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@TableName("inpatient_discharge")
public class InpatientDischarge {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String dischargeNo;

    private Long inpatientId;

    private String inpatientNo;

    private Long patientId;

    private String patientName;

    private LocalDateTime admissionDate;

    private LocalDateTime dischargeDate;

    private Integer actualDays;

    private BigDecimal totalAmount;

    private BigDecimal depositAmount;

    private BigDecimal refundAmount;

    private BigDecimal supplementAmount;

    private BigDecimal actualPay;

    private Integer payMethod;

    private LocalDateTime payTime;

    private String dischargeDiagnosis;

    private Integer dischargeResult;

    private Long operatorId;

    private String operatorName;

    private Integer status;

    private String invoiceNo;

    private String remark;

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

    public String getDischargeNo() {
        return dischargeNo;
    }

    public void setDischargeNo(String dischargeNo) {
        this.dischargeNo = dischargeNo;
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

    public Integer getActualDays() {
        return actualDays;
    }

    public void setActualDays(Integer actualDays) {
        this.actualDays = actualDays;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getSupplementAmount() {
        return supplementAmount;
    }

    public void setSupplementAmount(BigDecimal supplementAmount) {
        this.supplementAmount = supplementAmount;
    }

    public BigDecimal getActualPay() {
        return actualPay;
    }

    public void setActualPay(BigDecimal actualPay) {
        this.actualPay = actualPay;
    }

    public Integer getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(Integer payMethod) {
        this.payMethod = payMethod;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public String getDischargeDiagnosis() {
        return dischargeDiagnosis;
    }

    public void setDischargeDiagnosis(String dischargeDiagnosis) {
        this.dischargeDiagnosis = dischargeDiagnosis;
    }

    public Integer getDischargeResult() {
        return dischargeResult;
    }

    public void setDischargeResult(Integer dischargeResult) {
        this.dischargeResult = dischargeResult;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
