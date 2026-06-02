package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.dto.RefundApplyDTO;
import com.hospital.dto.RefundAuditDTO;
import com.hospital.entity.RefundRequest;
import com.hospital.entity.Settlement;
import com.hospital.mapper.RefundRequestMapper;
import com.hospital.mapper.SettlementMapper;
import com.hospital.service.RefundRequestService;
import com.hospital.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class RefundRequestServiceImpl extends ServiceImpl<RefundRequestMapper, RefundRequest> implements RefundRequestService {

    @Autowired
    private RefundRequestMapper refundRequestMapper;

    @Autowired
    private SettlementMapper settlementMapper;

    @Autowired
    private SettlementService settlementService;

    @Override
    public PageResult<RefundRequest> getRefundPage(PageQuery pageQuery, String keyword, Integer status, String startTime, String endTime) {
        Page<RefundRequest> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = (Page<RefundRequest>) refundRequestMapper.selectRefundPage(page, keyword, status, startTime, endTime);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public RefundRequest getRefundDetail(Long id) {
        return getById(id);
    }

    @Override
    public boolean applyRefund(RefundApplyDTO dto, Long userId, String userName) {
        Settlement settlement = settlementMapper.selectById(dto.getSettlementId());

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = lambdaQuery()
                .likeRight(RefundRequest::getRefundNo, "RF" + date)
                .count() + 1;

        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setRefundNo("RF" + date + String.format("%04d", count));
        refundRequest.setSettlementId(dto.getSettlementId());
        refundRequest.setSettlementNo(settlement.getSettlementNo());
        refundRequest.setPatientId(settlement.getPatientId());
        refundRequest.setPatientName(settlement.getPatientName());
        refundRequest.setRefundAmount(dto.getRefundAmount());
        refundRequest.setReason(dto.getReason());
        refundRequest.setStatus(0);
        refundRequest.setApplyUserId(userId);
        refundRequest.setApplyUserName(userName);
        refundRequest.setApplyTime(LocalDateTime.now());
        return save(refundRequest);
    }

    @Override
    public boolean auditRefund(RefundAuditDTO dto, Long userId, String userName) {
        RefundRequest refundRequest = getById(dto.getRefundId());
        refundRequest.setAuditUserId(userId);
        refundRequest.setAuditUserName(userName);
        refundRequest.setAuditTime(LocalDateTime.now());
        refundRequest.setAuditRemark(dto.getAuditRemark());

        if (dto.getStatus() == 1) {
            refundRequest.setStatus(1);
            updateById(refundRequest);
            settlementService.refundSettlement(refundRequest.getSettlementId());
        } else if (dto.getStatus() == 2) {
            refundRequest.setStatus(2);
            updateById(refundRequest);
        }
        return true;
    }
}
