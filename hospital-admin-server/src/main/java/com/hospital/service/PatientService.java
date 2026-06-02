package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.Patient;

public interface PatientService extends IService<Patient> {

    PageResult<Patient> getPatientPage(PageQuery pageQuery, String keyword, String startTime, String endTime);

    Patient getPatientDetail(Long id);

    boolean savePatient(Patient patient);

    boolean updatePatient(Patient patient);

    boolean deletePatient(Long id);

    String generatePatientNo();
}
