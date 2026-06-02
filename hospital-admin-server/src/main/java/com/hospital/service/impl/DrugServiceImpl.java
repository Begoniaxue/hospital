package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.dto.DrugStockDTO;
import com.hospital.entity.Drug;
import com.hospital.entity.DrugStockLog;
import com.hospital.mapper.DrugMapper;
import com.hospital.mapper.DrugStockLogMapper;
import com.hospital.service.DrugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DrugServiceImpl extends ServiceImpl<DrugMapper, Drug> implements DrugService {

    @Autowired
    private DrugMapper drugMapper;

    @Autowired
    private DrugStockLogMapper drugStockLogMapper;

    @Override
    public PageResult<Drug> getDrugPage(PageQuery pageQuery, String keyword, String category, Integer status) {
        Page<Drug> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = (Page<Drug>) drugMapper.selectDrugPage(page, keyword, category, status);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public Drug getDrugDetail(Long id) {
        return getById(id);
    }

    @Override
    public boolean saveDrug(Drug drug) {
        if (drug.getDrugCode() == null || drug.getDrugCode().isEmpty()) {
            drug.setDrugCode(generateDrugNo());
        }
        return save(drug);
    }

    @Override
    public boolean updateDrug(Drug drug) {
        return updateById(drug);
    }

    @Override
    public boolean deleteDrug(Long id) {
        return removeById(id);
    }

    @Override
    public String generateDrugNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = lambdaQuery()
                .likeRight(Drug::getDrugCode, "D" + date)
                .count() + 1;
        return "D" + date + String.format("%04d", count);
    }

    @Override
    public boolean stockIn(DrugStockDTO dto, Long userId, String userName) {
        Drug drug = getById(dto.getDrugId());
        int beforeStock = drug.getStock();
        int afterStock = beforeStock + dto.getQuantity();
        drug.setStock(afterStock);
        updateById(drug);

        DrugStockLog log = new DrugStockLog();
        log.setDrugId(dto.getDrugId());
        log.setType(1);
        log.setQuantity(dto.getQuantity());
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setReason(dto.getReason());
        log.setRelatedId(dto.getRelatedId());
        log.setOperatorId(userId);
        log.setOperatorName(userName);
        log.setCreateTime(LocalDateTime.now());
        drugStockLogMapper.insert(log);
        return true;
    }

    @Override
    public boolean stockOut(DrugStockDTO dto, Long userId, String userName) {
        Drug drug = getById(dto.getDrugId());
        int beforeStock = drug.getStock();
        if (beforeStock < dto.getQuantity()) {
            return false;
        }
        int afterStock = beforeStock - dto.getQuantity();
        drug.setStock(afterStock);
        updateById(drug);

        DrugStockLog log = new DrugStockLog();
        log.setDrugId(dto.getDrugId());
        log.setType(2);
        log.setQuantity(dto.getQuantity());
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setReason(dto.getReason());
        log.setRelatedId(dto.getRelatedId());
        log.setOperatorId(userId);
        log.setOperatorName(userName);
        log.setCreateTime(LocalDateTime.now());
        drugStockLogMapper.insert(log);
        return true;
    }

    @Override
    public PageResult<DrugStockLog> getStockLogPage(PageQuery pageQuery, Long drugId, Integer type, String startTime, String endTime) {
        Page<DrugStockLog> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = (Page<DrugStockLog>) drugStockLogMapper.selectStockLogPage(page, drugId, type, startTime, endTime);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public List<Drug> getWarningDrugs() {
        QueryWrapper<Drug> wrapper = new QueryWrapper<>();
        wrapper.apply("stock < stock_threshold").eq("status", 1);
        return list(wrapper);
    }

    @Override
    public List<Drug> getExpiredDrugs() {
        QueryWrapper<Drug> wrapper = new QueryWrapper<>();
        wrapper.le("expiry_date", LocalDate.now()).eq("status", 1);
        return list(wrapper);
    }
}
