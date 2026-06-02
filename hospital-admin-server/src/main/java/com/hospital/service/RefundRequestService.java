package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.dto.RefundApplyDTO;
import com.hospital.dto.RefundAuditDTO;
import com.hospital.entity.RefundRequest;

public interface RefundRequestService extends IService<RefundRequest> {

    PageResult<RefundRequest> getRefundPage(PageQuery pageQuery, String keyword, Integer status, String startTime, String endTime);

    RefundRequest getRefundDetail(Long id);

    boolean applyRefund(RefundApplyDTO dto, Long userId, String userName);

    boolean auditRefund(RefundAuditDTO dto, Long userId, String userName);
}
