package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.dto.SymptomDiagnoseDTO;
import com.hospital.entity.Symptom;

import java.util.List;

public interface SymptomService extends IService<Symptom> {

    List<Symptom> getSymptomTree();

    List<Symptom> getByParentId(Long parentId);

    List<Symptom> search(String keyword);

    List<Symptom> getRecommendDepartments(SymptomDiagnoseDTO dto);

    List<com.hospital.entity.Department> diagnose(SymptomDiagnoseDTO dto);

    List<com.hospital.entity.Department> aiDiagnose(String symptomText);

    Symptom getBySymptomCode(String symptomCode);

    boolean saveSymptom(Symptom symptom);

    boolean updateSymptom(Symptom symptom);

    boolean deleteSymptom(Long id);
}
