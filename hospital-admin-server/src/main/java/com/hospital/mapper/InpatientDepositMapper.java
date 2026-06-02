package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.InpatientDeposit;
import org.apache.ibatis.annotations.Param;

public interface InpatientDepositMapper extends BaseMapper<InpatientDeposit> {

    IPage<InpatientDeposit> selectDepositPage(Page<InpatientDeposit> page,
                                               @Param("keyword") String keyword,
                                               @Param("inpatientId") Long inpatientId,
                                               @Param("status") Integer status);
}
