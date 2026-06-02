package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.dto.PatientFamilyDTO;
import com.hospital.dto.PatientRealNameDTO;
import com.hospital.entity.Patient;
import com.hospital.entity.PatientFamily;
import com.hospital.entity.WechatUser;
import com.hospital.mapper.PatientFamilyMapper;
import com.hospital.mapper.PatientMapper;
import com.hospital.mapper.WechatUserMapper;
import com.hospital.service.PatientFamilyService;
import com.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatientFamilyServiceImpl extends ServiceImpl<PatientFamilyMapper, PatientFamily> implements PatientFamilyService {

    @Autowired
    private PatientFamilyMapper patientFamilyMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private WechatUserMapper wechatUserMapper;

    @Autowired
    private PatientService patientService;

    @Override
    public List<PatientFamily> getFamilyList(Long wechatUserId) {
        return patientFamilyMapper.selectByWechatUserId(wechatUserId);
    }

    @Override
    @Transactional
    public Map<String, Object> addFamilyMember(PatientFamilyDTO dto) {
        Map<String, Object> result = new HashMap<>();

        long count = lambdaQuery().eq(PatientFamily::getWechatUserId, dto.getWechatUserId()).count();
        if (count >= 5) {
            result.put("success", false);
            result.put("message", "最多只能绑定5位家庭成员");
            return result;
        }

        Patient patient = getPatientByIdCard(dto.getIdCard());
        if (patient == null) {
            patient = new Patient();
            patient.setName(dto.getName());
            patient.setGender(dto.getGender());
            patient.setIdCard(dto.getIdCard());
            patient.setPhone(dto.getPhone());
            patient.setPatientNo(patientService.generatePatientNo());
            if (dto.getBirthday() != null && !dto.getBirthday().isEmpty()) {
                int age = LocalDate.now().getYear() - LocalDate.parse(dto.getBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).getYear();
                patient.setAge(age);
            }
            patientMapper.insert(patient);
        }

        PatientFamily family = new PatientFamily();
        family.setWechatUserId(dto.getWechatUserId());
        family.setPatientId(patient.getId());
        family.setName(dto.getName());
        family.setGender(dto.getGender());
        family.setIdCard(dto.getIdCard());
        family.setPhone(dto.getPhone());
        family.setBirthday(dto.getBirthday());
        family.setRelation(dto.getRelation());
        family.setStatus(1);
        family.setHasVisitRecord(0);
        save(family);

        result.put("success", true);
        result.put("message", "添加成功");
        result.put("family", family);
        result.put("patient", patient);
        return result;
    }

    @Override
    public boolean updateFamilyMember(PatientFamily family) {
        return updateById(family);
    }

    @Override
    @Transactional
    public boolean deleteFamilyMember(Long id) {
        PatientFamily family = getById(id);
        if (family != null && family.getHasVisitRecord() != null && family.getHasVisitRecord() == 1) {
            return false;
        }
        return removeById(id);
    }

    @Override
    public boolean disableFamilyMember(Long id) {
        PatientFamily family = getById(id);
        if (family != null) {
            family.setStatus(0);
            return updateById(family);
        }
        return false;
    }

    @Override
    public Patient switchCurrentPatient(Long wechatUserId, Long patientId) {
        WechatUser wechatUser = wechatUserMapper.selectById(wechatUserId);
        if (wechatUser != null) {
            wechatUser.setCurrentPatientId(patientId);
            wechatUserMapper.updateById(wechatUser);
        }
        return patientMapper.selectById(patientId);
    }

    @Override
    @Transactional
    public Map<String, Object> realNameAuth(PatientRealNameDTO dto) {
        Map<String, Object> result = new HashMap<>();

        Patient patient = getPatientByIdCard(dto.getIdCard());
        boolean isNew = false;
        if (patient == null) {
            patient = new Patient();
            patient.setPatientNo(patientService.generatePatientNo());
            isNew = true;
        }
        patient.setName(dto.getName());
        patient.setIdCard(dto.getIdCard());
        patient.setPhone(dto.getPhone());
        if (dto.getGender() != null) {
            patient.setGender(dto.getGender());
        }
        if (dto.getAddress() != null) {
            patient.setAddress(dto.getAddress());
        }
        if (dto.getBirthday() != null && !dto.getBirthday().isEmpty()) {
            int age = LocalDate.now().getYear() - LocalDate.parse(dto.getBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).getYear();
            patient.setAge(age);
        }

        if (isNew) {
            patientMapper.insert(patient);
        } else {
            patientMapper.updateById(patient);
        }

        WechatUser wechatUser = wechatUserMapper.selectById(dto.getWechatUserId());
        if (wechatUser != null) {
            wechatUser.setCurrentPatientId(patient.getId());
            wechatUser.setPhone(dto.getPhone());
            wechatUserMapper.updateById(wechatUser);

            PatientFamily selfFamily = getOne(new LambdaQueryWrapper<PatientFamily>()
                    .eq(PatientFamily::getWechatUserId, dto.getWechatUserId())
                    .eq(PatientFamily::getRelation, "本人"));
            if (selfFamily == null) {
                selfFamily = new PatientFamily();
                selfFamily.setWechatUserId(dto.getWechatUserId());
                selfFamily.setPatientId(patient.getId());
                selfFamily.setName(patient.getName());
                selfFamily.setGender(patient.getGender());
                selfFamily.setIdCard(patient.getIdCard());
                selfFamily.setPhone(patient.getPhone());
                selfFamily.setRelation("本人");
                selfFamily.setStatus(1);
                selfFamily.setHasVisitRecord(0);
                save(selfFamily);
            }
        }

        result.put("success", true);
        result.put("message", isNew ? "实名注册成功，已自动创建就诊档案" : "实名认证成功，已匹配原有就诊档案");
        result.put("patient", patient);
        return result;
    }

    @Override
    public Patient getPatientByIdCard(String idCard) {
        return patientMapper.selectOne(new LambdaQueryWrapper<Patient>().eq(Patient::getIdCard, idCard));
    }
}
