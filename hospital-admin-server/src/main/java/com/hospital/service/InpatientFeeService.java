package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.InpatientFee;

import java.math.BigDecimal;

public interface InpatientFeeService extends IService<InpatientFee> {

    PageResult<InpatientFee> getFeePage(PageQuery pageQuery, String keyword, Long inpatientId, String feeDate);

    BigDecimal getTotalAmount(Long inpatientId);

    boolean saveFee(InpatientFee fee);
}
