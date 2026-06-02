package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.dto.SettlementCreateDTO;
import com.hospital.entity.Settlement;

import java.util.Map;

public interface SettlementService extends IService<Settlement> {

    PageResult<Settlement> getSettlementPage(PageQuery pageQuery, String keyword, Integer payMethod, Integer status, String startTime, String endTime);

    Settlement getSettlementDetail(Long id);

    boolean createSettlement(SettlementCreateDTO dto, Long userId, String userName);

    boolean refundSettlement(Long settlementId);

    Map<String, Object> getDailySettlement(String date);

    Map<String, Object> getMonthlySettlement(String month);
}
