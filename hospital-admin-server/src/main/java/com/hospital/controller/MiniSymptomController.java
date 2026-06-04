package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.dto.SymptomDiagnoseDTO;
import com.hospital.entity.Department;
import com.hospital.entity.Symptom;
import com.hospital.service.SymptomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mini/symptom")
public class MiniSymptomController {

    @Autowired
    private SymptomService symptomService;

    @GetMapping("/list")
    public Result<List<Symptom>> getSymptomList(@RequestParam(required = false, defaultValue = "0") Long parentId) {
        List<Symptom> symptoms = symptomService.getByParentId(parentId);
        return Result.success(symptoms);
    }

    @GetMapping("/search")
    public Result<List<Symptom>> searchSymptoms(@RequestParam String keyword) {
        List<Symptom> symptoms = symptomService.search(keyword);
        return Result.success(symptoms);
    }

    @PostMapping("/diagnose")
    public Result<List<Department>> diagnose(@RequestBody SymptomDiagnoseDTO dto) {
        List<Department> departments = symptomService.diagnose(dto);
        return Result.success(departments);
    }

    @PostMapping("/ai-diagnose")
    public Result<List<Department>> aiDiagnose(@RequestParam String symptomText) {
        List<Department> departments = symptomService.aiDiagnose(symptomText);
        return Result.success(departments);
    }
}
