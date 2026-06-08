package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.DoctorReview;

import java.util.List;

public interface DoctorReviewService extends IService<DoctorReview> {

    DoctorReview saveReview(DoctorReview review);

    DoctorReview getById(Long id);

    List<DoctorReview> getByPatientId(Long patientId);

    List<DoctorReview> listByPatientId(Long patientId);

    List<DoctorReview> getByDoctorId(Long doctorId);

    PageResult<DoctorReview> getPage(PageQuery query);

    PageResult<DoctorReview> adminPage(PageQuery query, Long doctorId, Integer rating, Integer status);

    boolean reply(Long id, String reply);

    boolean reply(Long id, Long replyUserId, String replyUserName, String reply);

    boolean updateStatus(Long id, Integer status);
}
