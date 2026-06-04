package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    @Select("SELECT * FROM department WHERE parent_id = #{parentId} AND status = 1 AND deleted = 0 ORDER BY sort ASC")
    List<Department> selectByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM department WHERE dept_level = #{level} AND status = 1 AND deleted = 0 ORDER BY sort ASC")
    List<Department> selectByLevel(@Param("level") Integer level);

    @Select("SELECT * FROM department WHERE (dept_name LIKE CONCAT('%', #{keyword}, '%') OR dept_name_pinyin LIKE CONCAT('%', #{keyword}, '%')) AND status = 1 AND deleted = 0 ORDER BY sort ASC")
    List<Department> searchByKeyword(@Param("keyword") String keyword);
}
