package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.InpatientOrder;
import org.apache.ibatis.annotations.Param;

public interface InpatientOrderMapper extends BaseMapper<InpatientOrder> {

    IPage<InpatientOrder> selectOrderPage(Page<InpatientOrder> page,
                                           @Param("keyword") String keyword,
                                           @Param("inpatientId") Long inpatientId,
                                           @Param("status") Integer status);
}
