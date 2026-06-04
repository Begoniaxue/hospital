package com.hospital.dto;

import java.util.List;

public class DepartmentTreeVO {

    private Long id;

    private Long parentId;

    private String deptCode;

    private String deptName;

    private String deptNamePinyin;

    private Integer deptType;

    private Integer deptLevel;

    private String location;

    private String phone;

    private String description;

    private Integer sort;

    private Integer status;

    private List<DepartmentTreeVO> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptNamePinyin() {
        return deptNamePinyin;
    }

    public void setDeptNamePinyin(String deptNamePinyin) {
        this.deptNamePinyin = deptNamePinyin;
    }

    public Integer getDeptType() {
        return deptType;
    }

    public void setDeptType(Integer deptType) {
        this.deptType = deptType;
    }

    public Integer getDeptLevel() {
        return deptLevel;
    }

    public void setDeptLevel(Integer deptLevel) {
        this.deptLevel = deptLevel;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<DepartmentTreeVO> getChildren() {
        return children;
    }

    public void setChildren(List<DepartmentTreeVO> children) {
        this.children = children;
    }
}
