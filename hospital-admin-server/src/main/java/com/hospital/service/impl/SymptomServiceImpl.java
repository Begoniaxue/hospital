package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.dto.SymptomDiagnoseDTO;
import com.hospital.entity.Department;
import com.hospital.entity.Symptom;
import com.hospital.entity.SymptomDepartment;
import com.hospital.mapper.DepartmentMapper;
import com.hospital.mapper.SymptomDepartmentMapper;
import com.hospital.mapper.SymptomMapper;
import com.hospital.service.SymptomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SymptomServiceImpl extends ServiceImpl<SymptomMapper, Symptom> implements SymptomService {

    @Autowired
    private SymptomMapper symptomMapper;

    @Autowired
    private SymptomDepartmentMapper symptomDepartmentMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public List<Symptom> getSymptomTree() {
        List<Symptom> allSymptoms = lambdaQuery().eq(Symptom::getStatus, 1).orderByAsc(Symptom::getSort).list();
        Map<Long, List<Symptom>> parentMap = allSymptoms.stream()
                .collect(Collectors.groupingBy(symptom -> symptom.getParentId() == null ? 0L : symptom.getParentId()));
        return buildTree(0L, parentMap);
    }

    private List<Symptom> buildTree(Long parentId, Map<Long, List<Symptom>> parentMap) {
        List<Symptom> children = parentMap.get(parentId);
        if (children == null || children.isEmpty()) {
            return new ArrayList<>();
        }
        return children;
    }

    @Override
    public List<Symptom> getByParentId(Long parentId) {
        return lambdaQuery().eq(Symptom::getParentId, parentId).eq(Symptom::getStatus, 1).list();
    }

    @Override
    public List<Symptom> getRecommendDepartments(SymptomDiagnoseDTO dto) {
        if (dto == null || dto.getSymptomIds() == null || dto.getSymptomIds().isEmpty()) {
            return new ArrayList<>();
        }
        List<SymptomDepartment> symptomDepartments = symptomDepartmentMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SymptomDepartment>()
                        .in("symptom_id", dto.getSymptomIds())
        );
        List<Long> symptomIds = symptomDepartments.stream()
                .map(SymptomDepartment::getSymptomId)
                .distinct()
                .collect(Collectors.toList());
        if (symptomIds.isEmpty()) {
            return new ArrayList<>();
        }
        return symptomMapper.selectBatchIds(symptomIds);
    }

    @Override
    public List<Symptom> search(String keyword) {
        return lambdaQuery()
                .eq(Symptom::getStatus, 1)
                .like(Symptom::getSymptomName, keyword)
                .orderByAsc(Symptom::getSort)
                .list();
    }

    @Override
    public List<Department> diagnose(SymptomDiagnoseDTO dto) {
        if (dto == null || dto.getSymptomIds() == null || dto.getSymptomIds().isEmpty()) {
            return new ArrayList<>();
        }
        List<SymptomDepartment> symptomDepartments = symptomDepartmentMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SymptomDepartment>()
                        .in("symptom_id", dto.getSymptomIds())
        );
        if (symptomDepartments.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, Integer> departmentWeightMap = symptomDepartments.stream()
                .collect(Collectors.groupingBy(
                        SymptomDepartment::getDepartmentId,
                        Collectors.summingInt(sd -> sd.getMatchWeight() != null ? sd.getMatchWeight() : 1)
                ));
        List<Long> departmentIds = departmentWeightMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        if (departmentIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Department> departments = departmentMapper.selectBatchIds(departmentIds);
        Map<Long, Department> departmentMap = departments.stream()
                .collect(Collectors.toMap(Department::getId, d -> d));
        List<Department> sortedDepartments = new ArrayList<>();
        for (Long id : departmentIds) {
            Department dept = departmentMap.get(id);
            if (dept != null && dept.getStatus() == 1) {
                sortedDepartments.add(dept);
            }
        }
        return sortedDepartments;
    }

    @Override
    public List<Department> aiDiagnose(String symptomText) {
        if (symptomText == null || symptomText.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<Symptom> allSymptoms = lambdaQuery().eq(Symptom::getStatus, 1).list();
        List<Long> matchedSymptomIds = new ArrayList<>();
        for (Symptom symptom : allSymptoms) {
            if (symptom.getSymptomName() != null && symptomText.contains(symptom.getSymptomName())) {
                matchedSymptomIds.add(symptom.getId());
            }
        }
        if (matchedSymptomIds.isEmpty()) {
            return new ArrayList<>();
        }
        SymptomDiagnoseDTO dto = new SymptomDiagnoseDTO();
        dto.setSymptomIds(matchedSymptomIds);
        return diagnose(dto);
    }

    @Override
    public Symptom getBySymptomCode(String symptomCode) {
        return lambdaQuery().eq(Symptom::getSymptomCode, symptomCode).one();
    }

    @Override
    public boolean saveSymptom(Symptom symptom) {
        symptom.setCreateTime(LocalDateTime.now());
        symptom.setUpdateTime(LocalDateTime.now());
        return save(symptom);
    }

    @Override
    public boolean updateSymptom(Symptom symptom) {
        symptom.setUpdateTime(LocalDateTime.now());
        return updateById(symptom);
    }

    @Override
    public boolean deleteSymptom(Long id) {
        return removeById(id);
    }
}
