package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.HealthAttachment;

import java.util.List;

public interface HealthAttachmentService extends IService<HealthAttachment> {

    PageResult<HealthAttachment> getAttachmentPage(PageQuery pageQuery, Long patientId, String category);

    List<HealthAttachment> getAttachmentList(Long patientId, String category);

    boolean saveAttachment(HealthAttachment attachment);

    boolean deleteAttachment(Long id);
}
