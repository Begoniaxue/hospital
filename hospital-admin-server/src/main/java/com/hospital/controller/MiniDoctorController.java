package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.entity.Doctor;
import com.hospital.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mini/doctor")
public class MiniDoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/department/{departmentId}")
    public Result<List<Doctor>> getDoctorsByDepartment(@PathVariable Long departmentId) {
        List<Doctor> doctors = doctorService.getByDepartmentId(departmentId);
        return Result.success(doctors);
    }

    @GetMapping("/{id}")
    public Result<Doctor> getDoctorDetail(@PathVariable Long id) {
        Doctor doctor = doctorService.getById(id);
        return Result.success(doctor);
    }

    @GetMapping("/search")
    public Result<List<Doctor>> searchDoctors(@RequestParam String keyword) {
        List<Doctor> doctors = doctorService.search(keyword);
        return Result.success(doctors);
    }

    @GetMapping("/recommend")
    public Result<List<Doctor>> getRecommendDoctors(@RequestParam(required = false) Long departmentId,
                                                    @RequestParam(defaultValue = "10") Integer limit) {
        List<Doctor> doctors = doctorService.getRecommend(departmentId, limit);
        return Result.success(doctors);
    }
}
