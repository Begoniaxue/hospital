package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.HealthAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HealthAttachmentMapper extends BaseMapper<HealthAttachment> {

    IPage<HealthAttachment> selectByPatientId(Page<HealthAttachment> page,
                                              @Param("patientId") Long patientId,
                                              @Param("category") String category);

    List<HealthAttachment> selectListByPatientId(@Param("patientId") Long patientId,
                                                 @Param("category") String category);
}
