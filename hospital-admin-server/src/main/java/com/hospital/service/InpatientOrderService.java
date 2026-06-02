package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.InpatientOrder;

public interface InpatientOrderService extends IService<InpatientOrder> {

    PageResult<InpatientOrder> getOrderPage(PageQuery pageQuery, String keyword, Long inpatientId, Integer status);

    boolean saveOrder(InpatientOrder order);

    boolean executeOrder(Long id, Long userId, String userName);

    boolean cancelOrder(Long id);
}
