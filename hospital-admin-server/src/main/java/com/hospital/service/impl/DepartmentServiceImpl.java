package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.dto.DepartmentTreeVO;
import com.hospital.entity.Department;
import com.hospital.mapper.DepartmentMapper;
import com.hospital.service.DepartmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Override
    public List<DepartmentTreeVO> getDepartmentTree() {
        List<Department> allDepartments = list(new QueryWrapper<Department>().eq("status", 1).orderByAsc("sort"));
        Map<Long, List<Department>> parentMap = allDepartments.stream()
                .collect(Collectors.groupingBy(dept -> dept.getParentId() == null ? 0L : dept.getParentId()));
        return buildTree(0L, parentMap);
    }

    private List<DepartmentTreeVO> buildTree(Long parentId, Map<Long, List<Department>> parentMap) {
        List<Department> children = parentMap.get(parentId);
        if (children == null || children.isEmpty()) {
            return new ArrayList<>();
        }
        List<DepartmentTreeVO> result = new ArrayList<>();
        for (Department dept : children) {
            DepartmentTreeVO vo = new DepartmentTreeVO();
            BeanUtils.copyProperties(dept, vo);
            vo.setChildren(buildTree(dept.getId(), parentMap));
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<Department> getByParentId(Long parentId) {
        return lambdaQuery().eq(Department::getParentId, parentId).eq(Department::getStatus, 1).list();
    }

    @Override
    public List<Department> getByDeptType(Integer deptType) {
        return lambdaQuery().eq(Department::getDeptType, deptType).eq(Department::getStatus, 1).list();
    }

    @Override
    public Department getByDeptCode(String deptCode) {
        return lambdaQuery().eq(Department::getDeptCode, deptCode).one();
    }

    @Override
    public boolean saveDepartment(Department department) {
        if (department.getDeptCode() == null || department.getDeptCode().isEmpty()) {
            department.setDeptCode(generateDeptCode());
        }
        department.setCreateTime(LocalDateTime.now());
        department.setUpdateTime(LocalDateTime.now());
        return save(department);
    }

    @Override
    public boolean updateDepartment(Department department) {
        department.setUpdateTime(LocalDateTime.now());
        return updateById(department);
    }

    @Override
    public boolean deleteDepartment(Long id) {
        return removeById(id);
    }

    @Override
    public List<Department> getByLevel(Integer level) {
        return lambdaQuery()
                .eq(Department::getDeptLevel, level)
                .eq(Department::getStatus, 1)
                .orderByAsc(Department::getSort)
                .list();
    }

    @Override
    public List<Department> search(String keyword) {
        return lambdaQuery()
                .eq(Department::getStatus, 1)
                .and(wrapper -> wrapper
                        .like(Department::getDeptName, keyword)
                        .or()
                        .like(Department::getDeptNamePinyin, keyword))
                .orderByAsc(Department::getSort)
                .list();
    }

    @Override
    public String generateDeptCode() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = lambdaQuery()
                .likeRight(Department::getDeptCode, "DEPT" + date)
                .count() + 1;
        return "DEPT" + date + String.format("%04d", count);
    }
}
