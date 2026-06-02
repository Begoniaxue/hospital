package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.entity.HealthRecord;

public interface HealthRecordService extends IService<HealthRecord> {

    HealthRecord getByPatientId(Long patientId);

    boolean saveOrUpdateHealthRecord(HealthRecord record);
}
