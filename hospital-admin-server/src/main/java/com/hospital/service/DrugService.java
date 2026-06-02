package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.dto.DrugStockDTO;
import com.hospital.entity.Drug;
import com.hospital.entity.DrugStockLog;

import java.util.List;

public interface DrugService extends IService<Drug> {

    PageResult<Drug> getDrugPage(PageQuery pageQuery, String keyword, String category, Integer status);

    Drug getDrugDetail(Long id);

    boolean saveDrug(Drug drug);

    boolean updateDrug(Drug drug);

    boolean deleteDrug(Long id);

    String generateDrugNo();

    boolean stockIn(DrugStockDTO dto, Long userId, String userName);

    boolean stockOut(DrugStockDTO dto, Long userId, String userName);

    PageResult<DrugStockLog> getStockLogPage(PageQuery pageQuery, Long drugId, Integer type, String startTime, String endTime);

    List<Drug> getWarningDrugs();

    List<Drug> getExpiredDrugs();
}
