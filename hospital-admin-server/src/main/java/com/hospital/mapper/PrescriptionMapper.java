package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.Prescription;
import org.apache.ibatis.annotations.Param;

public interface PrescriptionMapper extends BaseMapper<Prescription> {

    IPage<Prescription> selectPrescriptionPage(Page<Prescription> page,
                                               @Param("keyword") String keyword,
                                               @Param("status") Integer status,
                                               @Param("startTime") String startTime,
                                               @Param("endTime") String endTime);
}
