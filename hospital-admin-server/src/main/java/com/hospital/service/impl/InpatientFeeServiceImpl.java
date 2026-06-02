package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.InpatientFee;
import com.hospital.mapper.InpatientFeeMapper;
import com.hospital.service.InpatientFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class InpatientFeeServiceImpl extends ServiceImpl<InpatientFeeMapper, InpatientFee> implements InpatientFeeService {

    @Autowired
    private InpatientFeeMapper inpatientFeeMapper;

    @Override
    public PageResult<InpatientFee> getFeePage(PageQuery pageQuery, String keyword, Long inpatientId, String feeDate) {
        Page<InpatientFee> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = (Page<InpatientFee>) inpatientFeeMapper.selectFeePage(page, keyword, inpatientId, feeDate);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public BigDecimal getTotalAmount(Long inpatientId) {
        return inpatientFeeMapper.selectTotalAmount(inpatientId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveFee(InpatientFee fee) {
        fee.setFeeNo(generateFeeNo());
        if (fee.getFeeDate() == null) {
            fee.setFeeDate(java.time.LocalDate.now());
        }
        if (fee.getQuantity() != null && fee.getUnitPrice() != null && fee.getAmount() == null) {
            fee.setAmount(fee.getUnitPrice().multiply(new BigDecimal(fee.getQuantity())));
        }
        return save(fee);
    }

    private String generateFeeNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = lambdaQuery()
                .likeRight(InpatientFee::getFeeNo, "F" + date)
                .count() + 1;
        return "F" + date + String.format("%04d", count);
    }
}
