package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.InpatientDeposit;
import com.hospital.mapper.InpatientDepositMapper;
import com.hospital.service.InpatientDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class InpatientDepositServiceImpl extends ServiceImpl<InpatientDepositMapper, InpatientDeposit> implements InpatientDepositService {

    @Autowired
    private InpatientDepositMapper inpatientDepositMapper;

    @Override
    public PageResult<InpatientDeposit> getDepositPage(PageQuery pageQuery, String keyword, Long inpatientId, Integer status) {
        Page<InpatientDeposit> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = (Page<InpatientDeposit>) inpatientDepositMapper.selectDepositPage(page, keyword, inpatientId, status);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payDeposit(InpatientDeposit deposit) {
        deposit.setDepositNo(generateDepositNo());
        deposit.setPayTime(LocalDateTime.now());
        deposit.setStatus(1);
        return save(deposit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refundDeposit(Long id) {
        InpatientDeposit deposit = getById(id);
        if (deposit == null) {
            throw new RuntimeException("押金记录不存在");
        }
        if (deposit.getStatus() != 1) {
            throw new RuntimeException("押金状态异常，无法退款");
        }

        deposit.setStatus(2);
        deposit.setRefundTime(LocalDateTime.now());
        return updateById(deposit);
    }

    private String generateDepositNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = lambdaQuery()
                .likeRight(InpatientDeposit::getDepositNo, "D" + date)
                .count() + 1;
        return "D" + date + String.format("%04d", count);
    }
}
