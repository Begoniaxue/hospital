package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.DrugStockLog;
import org.apache.ibatis.annotations.Param;

public interface DrugStockLogMapper extends BaseMapper<DrugStockLog> {

    IPage<DrugStockLog> selectStockLogPage(Page<DrugStockLog> page,
                                           @Param("drugId") Long drugId,
                                           @Param("type") Integer type,
                                           @Param("startTime") String startTime,
                                           @Param("endTime") String endTime);
}
