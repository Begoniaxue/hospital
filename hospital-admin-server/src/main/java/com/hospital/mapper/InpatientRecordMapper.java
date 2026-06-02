package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.InpatientRecord;
import org.apache.ibatis.annotations.Param;

public interface InpatientRecordMapper extends BaseMapper<InpatientRecord> {

    IPage<InpatientRecord> selectRecordPage(Page<InpatientRecord> page,
                                             @Param("keyword") String keyword,
                                             @Param("inpatientId") Long inpatientId);
}
