package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.dto.PrescriptionAuditDTO;
import com.hospital.entity.Prescription;
import com.hospital.entity.PrescriptionItem;

import java.util.List;

public interface PrescriptionService extends IService<Prescription> {

    PageResult<Prescription> getPrescriptionPage(PageQuery pageQuery, String keyword, Integer status, String startTime, String endTime);

    Prescription getPrescriptionDetail(Long id);

    boolean savePrescription(Prescription prescription, List<PrescriptionItem> items);

    boolean auditPrescription(PrescriptionAuditDTO dto, Long userId, String userName);

    boolean dispensePrescription(Long prescriptionId, Long userId, String userName);

    boolean issuePrescription(Long prescriptionId, Long userId, String userName);
}
