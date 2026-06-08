package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.Complaint;
import com.hospital.mapper.ComplaintMapper;
import com.hospital.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint> implements ComplaintService {

    @Autowired
    private ComplaintMapper complaintMapper;

    @Override
    public Complaint saveComplaint(Complaint complaint) {
        if (complaint.getComplaintNo() == null || complaint.getComplaintNo().isEmpty()) {
            complaint.setComplaintNo(generateComplaintNo());
        }
        save(complaint);
        return complaint;
    }

    @Override
    public Complaint getById(Long id) {
        return super.getById(id);
    }

    @Override
    public List<Complaint> getByPatientId(Long patientId) {
        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Complaint::getPatientId, patientId);
        wrapper.orderByDesc(Complaint::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<Complaint> listByPatientId(Long patientId) {
        return getByPatientId(patientId);
    }

    @Override
    public PageResult<Complaint> getPage(PageQuery query) {
        Page<Complaint> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Complaint::getCreateTime);
        page = page(page, wrapper);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public PageResult<Complaint> adminPage(PageQuery query, Integer type, Integer status, String keyword) {
        Page<Complaint> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(Complaint::getType, type);
        }
        if (status != null) {
            wrapper.eq(Complaint::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Complaint::getTitle, keyword)
                    .or().like(Complaint::getContent, keyword)
                    .or().like(Complaint::getPatientName, keyword)
                    .or().like(Complaint::getComplaintNo, keyword));
        }
        wrapper.orderByDesc(Complaint::getCreateTime);
        page = page(page, wrapper);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public boolean handle(Long id, Long handleUserId, String handleUserName, String reply) {
        Complaint complaint = getById(id);
        if (complaint == null) {
            return false;
        }
        complaint.setHandleUserId(handleUserId);
        complaint.setHandleUserName(handleUserName);
        complaint.setHandleTime(LocalDateTime.now());
        complaint.setReply(reply);
        return updateById(complaint);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Complaint complaint = getById(id);
        if (complaint == null) {
            return false;
        }
        complaint.setStatus(status);
        return updateById(complaint);
    }

    private String generateComplaintNo() {
        return "CP" + System.currentTimeMillis();
    }
}
