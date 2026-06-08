package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.DoctorReview;
import com.hospital.mapper.DoctorReviewMapper;
import com.hospital.service.DoctorReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DoctorReviewServiceImpl extends ServiceImpl<DoctorReviewMapper, DoctorReview> implements DoctorReviewService {

    @Autowired
    private DoctorReviewMapper doctorReviewMapper;

    @Override
    public DoctorReview saveReview(DoctorReview review) {
        save(review);
        return review;
    }

    @Override
    public DoctorReview getById(Long id) {
        return super.getById(id);
    }

    @Override
    public List<DoctorReview> getByPatientId(Long patientId) {
        LambdaQueryWrapper<DoctorReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DoctorReview::getPatientId, patientId);
        wrapper.orderByDesc(DoctorReview::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<DoctorReview> listByPatientId(Long patientId) {
        return getByPatientId(patientId);
    }

    @Override
    public List<DoctorReview> getByDoctorId(Long doctorId) {
        LambdaQueryWrapper<DoctorReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DoctorReview::getDoctorId, doctorId);
        wrapper.orderByDesc(DoctorReview::getCreateTime);
        return list(wrapper);
    }

    @Override
    public PageResult<DoctorReview> getPage(PageQuery query) {
        Page<DoctorReview> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<DoctorReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(DoctorReview::getCreateTime);
        page = page(page, wrapper);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public PageResult<DoctorReview> adminPage(PageQuery query, Long doctorId, Integer rating, Integer status) {
        Page<DoctorReview> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<DoctorReview> wrapper = new LambdaQueryWrapper<>();
        if (doctorId != null) {
            wrapper.eq(DoctorReview::getDoctorId, doctorId);
        }
        if (rating != null) {
            wrapper.eq(DoctorReview::getRating, rating);
        }
        if (status != null) {
            wrapper.eq(DoctorReview::getStatus, status);
        }
        wrapper.orderByDesc(DoctorReview::getCreateTime);
        page = page(page, wrapper);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public boolean reply(Long id, String reply) {
        DoctorReview review = getById(id);
        if (review == null) {
            return false;
        }
        review.setReply(reply);
        review.setReplyTime(LocalDateTime.now());
        return updateById(review);
    }

    @Override
    public boolean reply(Long id, Long replyUserId, String replyUserName, String reply) {
        DoctorReview review = getById(id);
        if (review == null) {
            return false;
        }
        review.setReply(reply);
        review.setReplyUserId(replyUserId);
        review.setReplyUserName(replyUserName);
        review.setReplyTime(LocalDateTime.now());
        return updateById(review);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        DoctorReview review = getById(id);
        if (review == null) {
            return false;
        }
        review.setStatus(status);
        return updateById(review);
    }
}
