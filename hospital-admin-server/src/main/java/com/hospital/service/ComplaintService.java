package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.Complaint;

import java.util.List;

public interface ComplaintService extends IService<Complaint> {

    Complaint saveComplaint(Complaint complaint);

    Complaint getById(Long id);

    List<Complaint> getByPatientId(Long patientId);

    List<Complaint> listByPatientId(Long patientId);

    PageResult<Complaint> getPage(PageQuery query);

    PageResult<Complaint> adminPage(PageQuery query, Integer type, Integer status, String keyword);

    boolean handle(Long id, Long handleUserId, String handleUserName, String reply);

    boolean updateStatus(Long id, Integer status);
}
