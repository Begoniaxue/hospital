package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.Doctor;
import com.hospital.mapper.DoctorMapper;
import com.hospital.mapper.DoctorScheduleMapper;
import com.hospital.mapper.RegistrationMapper;
import com.hospital.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private RegistrationMapper registrationMapper;

    @Autowired
    private DoctorScheduleMapper doctorScheduleMapper;

    @Override
    public PageResult<Doctor> getDoctorPage(PageQuery pageQuery, String keyword, Long departmentId, Integer status) {
        Page<Doctor> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        QueryWrapper<Doctor> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword).or().like("name_pinyin", keyword);
        }
        if (departmentId != null) {
            wrapper.eq("department_id", departmentId);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByAsc("sort");
        page = doctorMapper.selectPage(page, wrapper);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public Doctor getDoctorDetail(Long id) {
        return getById(id);
    }

    @Override
    public List<Doctor> getByDepartmentId(Long departmentId) {
        return lambdaQuery().eq(Doctor::getDepartmentId, departmentId).eq(Doctor::getStatus, 1).list();
    }

    @Override
    public List<Doctor> getByStatus(Integer status) {
        return lambdaQuery().eq(Doctor::getStatus, status).list();
    }

    @Override
    public Doctor getByDoctorNo(String doctorNo) {
        return lambdaQuery().eq(Doctor::getDoctorNo, doctorNo).one();
    }

    @Override
    public boolean saveDoctor(Doctor doctor) {
        if (doctor.getDoctorNo() == null || doctor.getDoctorNo().isEmpty()) {
            doctor.setDoctorNo(generateDoctorNo());
        }
        doctor.setCreateTime(LocalDateTime.now());
        doctor.setUpdateTime(LocalDateTime.now());
        return save(doctor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDoctor(Doctor doctor) {
        Doctor oldDoctor = getById(doctor.getId());
        if (oldDoctor == null) {
            return false;
        }
        
        doctor.setUpdateTime(LocalDateTime.now());
        boolean result = updateById(doctor);
        
        if (result) {
            if (doctor.getName() != null && !doctor.getName().equals(oldDoctor.getName())) {
                registrationMapper.updateDoctorNameByDoctorId(doctor.getId(), doctor.getName());
                doctorScheduleMapper.updateDoctorNameByDoctorId(doctor.getId(), doctor.getName());
            }
            
            if (doctor.getDepartmentId() != null && !doctor.getDepartmentId().equals(oldDoctor.getDepartmentId())) {
                String departmentName = doctor.getDepartmentName() != null ? doctor.getDepartmentName() : oldDoctor.getDepartmentName();
                doctorScheduleMapper.updateDepartmentByDoctorId(doctor.getId(), doctor.getDepartmentId(), departmentName);
            }
        }
        
        return result;
    }

    @Override
    public boolean deleteDoctor(Long id) {
        return removeById(id);
    }

    @Override
    public List<Doctor> search(String keyword) {
        return lambdaQuery()
                .eq(Doctor::getStatus, 1)
                .and(wrapper -> wrapper
                        .like(Doctor::getName, keyword)
                        .or()
                        .like(Doctor::getNamePinyin, keyword))
                .orderByAsc(Doctor::getSort)
                .list();
    }

    @Override
    public List<Doctor> getRecommend(Long departmentId, Integer limit) {
        return lambdaQuery()
                .eq(Doctor::getStatus, 1)
                .eq(departmentId != null, Doctor::getDepartmentId, departmentId)
                .orderByAsc(Doctor::getConsultationFee)
                .last("LIMIT " + limit)
                .list();
    }

    @Override
    public String generateDoctorNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = lambdaQuery()
                .likeRight(Doctor::getDoctorNo, "DOC" + date)
                .count() + 1;
        return "DOC" + date + String.format("%04d", count);
    }
}
