package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {

    @Select("SELECT * FROM doctor WHERE department_id = #{departmentId} AND status = 1 AND deleted = 0 ORDER BY sort ASC")
    List<Doctor> selectByDepartmentId(@Param("departmentId") Long departmentId);

    @Select("SELECT * FROM doctor WHERE (name LIKE CONCAT('%', #{keyword}, '%') OR name_pinyin LIKE CONCAT('%', #{keyword}, '%')) AND status = 1 AND deleted = 0 ORDER BY sort ASC")
    List<Doctor> searchByKeyword(@Param("keyword") String keyword);
}
