package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.entity.HealthRecord;
import com.hospital.mapper.HealthRecordMapper;
import com.hospital.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> implements HealthRecordService {

    @Autowired
    private HealthRecordMapper healthRecordMapper;

    @Override
    public HealthRecord getByPatientId(Long patientId) {
        return healthRecordMapper.selectByPatientId(patientId);
    }

    @Override
    public boolean saveOrUpdateHealthRecord(HealthRecord record) {
        HealthRecord existing = healthRecordMapper.selectByPatientId(record.getPatientId());
        if (existing != null) {
            record.setId(existing.getId());
            return updateById(record);
        } else {
            return save(record);
        }
    }
}
