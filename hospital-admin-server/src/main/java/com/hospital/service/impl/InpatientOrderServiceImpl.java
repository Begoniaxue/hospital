package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.InpatientOrder;
import com.hospital.mapper.InpatientOrderMapper;
import com.hospital.service.InpatientOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class InpatientOrderServiceImpl extends ServiceImpl<InpatientOrderMapper, InpatientOrder> implements InpatientOrderService {

    @Autowired
    private InpatientOrderMapper inpatientOrderMapper;

    @Override
    public PageResult<InpatientOrder> getOrderPage(PageQuery pageQuery, String keyword, Long inpatientId, Integer status) {
        Page<InpatientOrder> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = (Page<InpatientOrder>) inpatientOrderMapper.selectOrderPage(page, keyword, inpatientId, status);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrder(InpatientOrder order) {
        order.setOrderNo(generateOrderNo());
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(0);
        return save(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean executeOrder(Long id, Long userId, String userName) {
        InpatientOrder order = getById(id);
        if (order == null) {
            throw new RuntimeException("医嘱不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("医嘱状态异常，无法执行");
        }

        order.setStatus(1);
        order.setExecuteTime(LocalDateTime.now());
        order.setExecuteUserId(userId);
        order.setExecuteUserName(userName);
        return updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long id) {
        InpatientOrder order = getById(id);
        if (order == null) {
            throw new RuntimeException("医嘱不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("医嘱状态异常，无法取消");
        }

        order.setStatus(2);
        return updateById(order);
    }

    private String generateOrderNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = lambdaQuery()
                .likeRight(InpatientOrder::getOrderNo, "O" + date)
                .count() + 1;
        return "O" + date + String.format("%04d", count);
    }
}
