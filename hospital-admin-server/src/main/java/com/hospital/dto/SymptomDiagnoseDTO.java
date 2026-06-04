package com.hospital.dto;

import java.util.List;

public class SymptomDiagnoseDTO {

    private Long patientId;

    private List<Long> symptomIds;

    private String symptomDescription;

    private Integer duration;

    private String durationUnit;

    private Integer severity;

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public List<Long> getSymptomIds() {
        return symptomIds;
    }

    public void setSymptomIds(List<Long> symptomIds) {
        this.symptomIds = symptomIds;
    }

    public String getSymptomDescription() {
        return symptomDescription;
    }

    public void setSymptomDescription(String symptomDescription) {
        this.symptomDescription = symptomDescription;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }

    public Integer getSeverity() {
        return severity;
    }

    public void setSeverity(Integer severity) {
        this.severity = severity;
    }
}
