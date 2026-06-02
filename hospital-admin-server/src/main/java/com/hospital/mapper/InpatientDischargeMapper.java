package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.InpatientDischarge;
import org.apache.ibatis.annotations.Param;

public interface InpatientDischargeMapper extends BaseMapper<InpatientDischarge> {

    IPage<InpatientDischarge> selectDischargePage(Page<InpatientDischarge> page,
                                                   @Param("keyword") String keyword,
                                                   @Param("status") Integer status);
}
