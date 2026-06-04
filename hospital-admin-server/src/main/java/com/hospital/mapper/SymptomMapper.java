package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.Symptom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SymptomMapper extends BaseMapper<Symptom> {

    @Select("SELECT * FROM symptom WHERE parent_id = #{parentId} AND status = 1 AND deleted = 0 ORDER BY sort ASC")
    List<Symptom> selectByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM symptom WHERE (symptom_name LIKE CONCAT('%', #{keyword}, '%')) AND status = 1 AND deleted = 0 ORDER BY sort ASC")
    List<Symptom> searchByKeyword(@Param("keyword") String keyword);
}
