package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.HealthRecord;

public interface HealthRecordMapper extends BaseMapper<HealthRecord> {

    HealthRecord selectByPatientId(Long patientId);
}
