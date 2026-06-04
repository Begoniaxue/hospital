package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.SymptomDepartment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SymptomDepartmentMapper extends BaseMapper<SymptomDepartment> {

    @Select("SELECT sd.* FROM symptom_department sd " +
            "INNER JOIN department d ON sd.department_id = d.id " +
            "WHERE sd.symptom_id IN (#{symptomIds}) " +
            "AND d.status = 1 AND d.deleted = 0 " +
            "GROUP BY sd.department_id " +
            "ORDER BY SUM(sd.match_weight) DESC")
    List<SymptomDepartment> selectDepartmentsBySymptomIds(@Param("symptomIds") String symptomIds);
}
