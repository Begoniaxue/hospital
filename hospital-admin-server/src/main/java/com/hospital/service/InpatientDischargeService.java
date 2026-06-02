package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.InpatientDischarge;

public interface InpatientDischargeService extends IService<InpatientDischarge> {

    PageResult<InpatientDischarge> getDischargePage(PageQuery pageQuery, String keyword, Integer status);

    InpatientDischarge settleDischarge(Long inpatientId, InpatientDischarge discharge);

    byte[] printDischarge(Long id);
}
