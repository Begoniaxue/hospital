package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.dto.DepartmentTreeVO;
import com.hospital.entity.Department;

import java.util.List;

public interface DepartmentService extends IService<Department> {

    List<DepartmentTreeVO> getDepartmentTree();

    List<Department> getByParentId(Long parentId);

    List<Department> getByLevel(Integer level);

    List<Department> getByDeptType(Integer deptType);

    List<Department> search(String keyword);

    Department getByDeptCode(String deptCode);

    boolean saveDepartment(Department department);

    boolean updateDepartment(Department department);

    boolean deleteDepartment(Long id);

    String generateDeptCode();
}
