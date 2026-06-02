package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.dto.SettlementCreateDTO;
import com.hospital.dto.SettlementItemDTO;
import com.hospital.entity.Settlement;
import com.hospital.entity.SettlementItem;
import com.hospital.mapper.SettlementItemMapper;
import com.hospital.mapper.SettlementMapper;
import com.hospital.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class SettlementServiceImpl extends ServiceImpl<SettlementMapper, Settlement> implements SettlementService {

    @Autowired
    private SettlementMapper settlementMapper;

    @Autowired
    private SettlementItemMapper settlementItemMapper;

    @Override
    public PageResult<Settlement> getSettlementPage(PageQuery pageQuery, String keyword, Integer payMethod, Integer status, String startTime, String endTime) {
        Page<Settlement> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = (Page<Settlement>) settlementMapper.selectSettlementPage(page, keyword, payMethod, status, startTime, endTime);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public Settlement getSettlementDetail(Long id) {
        Settlement settlement = getById(id);
        if (settlement != null) {
            QueryWrapper<SettlementItem> wrapper = new QueryWrapper<>();
            wrapper.eq("settlement_id", id);
            List<SettlementItem> items = settlementItemMapper.selectList(wrapper);
            settlement.setItems(items);
        }
        return settlement;
    }

    @Override
    public boolean createSettlement(SettlementCreateDTO dto, Long userId, String userName) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = lambdaQuery()
                .likeRight(Settlement::getSettlementNo, "S" + date)
                .count() + 1;
        long invCount = lambdaQuery()
                .likeRight(Settlement::getInvoiceNo, "INV" + date)
                .count() + 1;

        Settlement settlement = new Settlement();
        settlement.setSettlementNo("S" + date + String.format("%04d", count));
        settlement.setInvoiceNo("INV" + date + String.format("%04d", invCount));
        settlement.setPatientId(dto.getPatientId());
        settlement.setPatientName(dto.getPatientName());
        settlement.setPrescriptionId(dto.getPrescriptionId());
        settlement.setRegistrationFee(dto.getRegistrationFee());
        settlement.setDrugFee(dto.getDrugFee());
        settlement.setExamFee(dto.getExamFee());
        settlement.setOtherFee(dto.getOtherFee());
        settlement.setTotalAmount(dto.getTotalAmount());
        settlement.setDiscountAmount(dto.getDiscountAmount());
        settlement.setActualAmount(dto.getActualAmount());
        settlement.setPayMethod(dto.getPayMethod());
        settlement.setStatus(1);
        settlement.setOperatorId(userId);
        settlement.setOperatorName(userName);
        settlement.setSettleTime(LocalDateTime.now());
        settlement.setRemark(dto.getRemark());
        save(settlement);

        if (dto.getItems() != null) {
            for (SettlementItemDTO itemDTO : dto.getItems()) {
                SettlementItem item = new SettlementItem();
                item.setSettlementId(settlement.getId());
                item.setType(itemDTO.getType());
                item.setName(itemDTO.getName());
                item.setQuantity(itemDTO.getQuantity());
                item.setPrice(itemDTO.getPrice());
                item.setAmount(itemDTO.getAmount());
                item.setRemark(itemDTO.getRemark());
                item.setCreateTime(LocalDateTime.now());
                settlementItemMapper.insert(item);
            }
        }
        return true;
    }

    @Override
    public boolean refundSettlement(Long settlementId) {
        Settlement settlement = getById(settlementId);
        settlement.setStatus(2);
        return updateById(settlement);
    }

    @Override
    public Map<String, Object> getDailySettlement(String date) {
        return settlementMapper.selectDailySettlement(date);
    }

    @Override
    public Map<String, Object> getMonthlySettlement(String month) {
        return settlementMapper.selectMonthlySettlement(month);
    }
}
