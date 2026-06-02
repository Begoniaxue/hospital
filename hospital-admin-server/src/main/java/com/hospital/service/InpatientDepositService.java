package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.InpatientDeposit;

public interface InpatientDepositService extends IService<InpatientDeposit> {

    PageResult<InpatientDeposit> getDepositPage(PageQuery pageQuery, String keyword, Long inpatientId, Integer status);

    boolean payDeposit(InpatientDeposit deposit);

    boolean refundDeposit(Long id);
}
