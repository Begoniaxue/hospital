package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.Settlement;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface SettlementMapper extends BaseMapper<Settlement> {

    IPage<Settlement> selectSettlementPage(Page<Settlement> page,
                                           @Param("keyword") String keyword,
                                           @Param("payMethod") Integer payMethod,
                                           @Param("status") Integer status,
                                           @Param("startTime") String startTime,
                                           @Param("endTime") String endTime);

    Map<String, Object> selectDailySettlement(@Param("date") String date);

    Map<String, Object> selectMonthlySettlement(@Param("month") String month);
}
