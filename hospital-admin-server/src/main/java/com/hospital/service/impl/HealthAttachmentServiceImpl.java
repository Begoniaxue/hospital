package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.HealthAttachment;
import com.hospital.mapper.HealthAttachmentMapper;
import com.hospital.service.HealthAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthAttachmentServiceImpl extends ServiceImpl<HealthAttachmentMapper, HealthAttachment> implements HealthAttachmentService {

    @Autowired
    private HealthAttachmentMapper healthAttachmentMapper;

    @Override
    public PageResult<HealthAttachment> getAttachmentPage(PageQuery pageQuery, Long patientId, String category) {
        Page<HealthAttachment> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = (Page<HealthAttachment>) healthAttachmentMapper.selectByPatientId(page, patientId, category);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public List<HealthAttachment> getAttachmentList(Long patientId, String category) {
        return healthAttachmentMapper.selectListByPatientId(patientId, category);
    }

    @Override
    public boolean saveAttachment(HealthAttachment attachment) {
        return save(attachment);
    }

    @Override
    public boolean deleteAttachment(Long id) {
        return removeById(id);
    }
}
