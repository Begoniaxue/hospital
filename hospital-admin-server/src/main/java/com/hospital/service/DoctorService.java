package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.Doctor;

import java.util.List;

public interface DoctorService extends IService<Doctor> {

    PageResult<Doctor> getDoctorPage(PageQuery pageQuery, String keyword, Long departmentId, Integer status);

    Doctor getDoctorDetail(Long id);

    List<Doctor> getByDepartmentId(Long departmentId);

    List<Doctor> getByStatus(Integer status);

    List<Doctor> search(String keyword);

    List<Doctor> getRecommend(Long departmentId, Integer limit);

    Doctor getByDoctorNo(String doctorNo);

    boolean saveDoctor(Doctor doctor);

    boolean updateDoctor(Doctor doctor);

    boolean deleteDoctor(Long id);

    String generateDoctorNo();
}
