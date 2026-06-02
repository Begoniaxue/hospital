package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

@TableName("health_record")
public class HealthRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long patientId;

    private String allergyHistory;

    private String medicalHistory;

    private String chronicDisease;

    private String operationHistory;

    private String drugContraindication;

    private String bloodType;

    private String height;

    private String weight;

    private String maritalStatus;

    private String occupation;

    private String smokingStatus;

    private String drinkingStatus;

    private String exerciseHabit;

    private String dietaryHabit;

    private String sleepHabit;

    private String defecationHabit;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getAllergyHistory() {
        return allergyHistory;
    }

    public void setAllergyHistory(String allergyHistory) {
        this.allergyHistory = allergyHistory;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getChronicDisease() {
        return chronicDisease;
    }

    public void setChronicDisease(String chronicDisease) {
        this.chronicDisease = chronicDisease;
    }

    public String getOperationHistory() {
        return operationHistory;
    }

    public void setOperationHistory(String operationHistory) {
        this.operationHistory = operationHistory;
    }

    public String getDrugContraindication() {
        return drugContraindication;
    }

    public void setDrugContraindication(String drugContraindication) {
        this.drugContraindication = drugContraindication;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getSmokingStatus() {
        return smokingStatus;
    }

    public void setSmokingStatus(String smokingStatus) {
        this.smokingStatus = smokingStatus;
    }

    public String getDrinkingStatus() {
        return drinkingStatus;
    }

    public void setDrinkingStatus(String drinkingStatus) {
        this.drinkingStatus = drinkingStatus;
    }

    public String getExerciseHabit() {
        return exerciseHabit;
    }

    public void setExerciseHabit(String exerciseHabit) {
        this.exerciseHabit = exerciseHabit;
    }

    public String getDietaryHabit() {
        return dietaryHabit;
    }

    public void setDietaryHabit(String dietaryHabit) {
        this.dietaryHabit = dietaryHabit;
    }

    public String getSleepHabit() {
        return sleepHabit;
    }

    public void setSleepHabit(String sleepHabit) {
        this.sleepHabit = sleepHabit;
    }

    public String getDefecationHabit() {
        return defecationHabit;
    }

    public void setDefecationHabit(String defecationHabit) {
        this.defecationHabit = defecationHabit;
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

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
