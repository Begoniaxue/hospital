package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.dto.PatientFamilyDTO;
import com.hospital.dto.PatientRealNameDTO;
import com.hospital.entity.Patient;
import com.hospital.entity.PatientFamily;

import java.util.List;
import java.util.Map;

public interface PatientFamilyService extends IService<PatientFamily> {

    List<PatientFamily> getFamilyList(Long wechatUserId);

    Map<String, Object> addFamilyMember(PatientFamilyDTO dto);

    boolean updateFamilyMember(PatientFamily family);

    boolean deleteFamilyMember(Long id);

    boolean disableFamilyMember(Long id);

    Patient switchCurrentPatient(Long wechatUserId, Long patientId);

    Map<String, Object> realNameAuth(PatientRealNameDTO dto);

    Patient getPatientByIdCard(String idCard);
}
