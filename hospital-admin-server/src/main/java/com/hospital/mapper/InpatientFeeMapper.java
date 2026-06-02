package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.InpatientFee;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface InpatientFeeMapper extends BaseMapper<InpatientFee> {

    IPage<InpatientFee> selectFeePage(Page<InpatientFee> page,
                                       @Param("keyword") String keyword,
                                       @Param("inpatientId") Long inpatientId,
                                       @Param("feeDate") String feeDate);

    BigDecimal selectTotalAmount(@Param("inpatientId") Long inpatientId);
}
