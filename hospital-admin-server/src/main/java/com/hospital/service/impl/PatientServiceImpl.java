package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.Patient;
import com.hospital.mapper.PatientMapper;
import com.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private PatientMapper patientMapper;

    @Override
    public PageResult<Patient> getPatientPage(PageQuery pageQuery, String keyword, String startTime, String endTime) {
        Page<Patient> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = (Page<Patient>) patientMapper.selectPatientPage(page, keyword, startTime, endTime);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public Patient getPatientDetail(Long id) {
        return getById(id);
    }

    @Override
    public boolean savePatient(Patient patient) {
        if (patient.getPatientNo() == null || patient.getPatientNo().isEmpty()) {
            patient.setPatientNo(generatePatientNo());
        }
        return save(patient);
    }

    @Override
    public boolean updatePatient(Patient patient) {
        return updateById(patient);
    }

    @Override
    public boolean deletePatient(Long id) {
        return removeById(id);
    }

    @Override
    public String generatePatientNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = lambdaQuery()
                .likeRight(Patient::getPatientNo, "P" + date)
                .count() + 1;
        return "P" + date + String.format("%04d", count);
    }
}
