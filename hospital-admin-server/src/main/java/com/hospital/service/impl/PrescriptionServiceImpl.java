package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.dto.PrescriptionAuditDTO;
import com.hospital.entity.Drug;
import com.hospital.entity.DrugStockLog;
import com.hospital.entity.Prescription;
import com.hospital.entity.PrescriptionItem;
import com.hospital.mapper.DrugMapper;
import com.hospital.mapper.DrugStockLogMapper;
import com.hospital.mapper.PrescriptionItemMapper;
import com.hospital.mapper.PrescriptionMapper;
import com.hospital.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PrescriptionServiceImpl extends ServiceImpl<PrescriptionMapper, Prescription> implements PrescriptionService {

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private PrescriptionItemMapper prescriptionItemMapper;

    @Autowired
    private DrugMapper drugMapper;

    @Autowired
    private DrugStockLogMapper drugStockLogMapper;

    @Override
    public PageResult<Prescription> getPrescriptionPage(PageQuery pageQuery, String keyword, Integer status, String startTime, String endTime) {
        Page<Prescription> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = (Page<Prescription>) prescriptionMapper.selectPrescriptionPage(page, keyword, status, startTime, endTime);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public Prescription getPrescriptionDetail(Long id) {
        Prescription prescription = getById(id);
        if (prescription != null) {
            QueryWrapper<PrescriptionItem> wrapper = new QueryWrapper<>();
            wrapper.eq("prescription_id", id);
            List<PrescriptionItem> items = prescriptionItemMapper.selectList(wrapper);
            prescription.setItems(items);
        }
        return prescription;
    }

    @Override
    public boolean savePrescription(Prescription prescription, List<PrescriptionItem> items) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = lambdaQuery()
                .likeRight(Prescription::getPrescriptionNo, "RX" + date)
                .count() + 1;
        prescription.setPrescriptionNo("RX" + date + String.format("%04d", count));
        save(prescription);
        for (PrescriptionItem item : items) {
            item.setPrescriptionId(prescription.getId());
            prescriptionItemMapper.insert(item);
        }
        return true;
    }

    @Override
    public boolean auditPrescription(PrescriptionAuditDTO dto, Long userId, String userName) {
        Prescription prescription = getById(dto.getPrescriptionId());
        prescription.setStatus(dto.getStatus());
        prescription.setAuditUserId(userId);
        prescription.setAuditUserName(userName);
        prescription.setAuditTime(LocalDateTime.now());
        return updateById(prescription);
    }

    @Override
    public boolean dispensePrescription(Long prescriptionId, Long userId, String userName) {
        Prescription prescription = getById(prescriptionId);
        prescription.setStatus(2);
        prescription.setDispenseUserId(userId);
        prescription.setDispenseUserName(userName);
        prescription.setDispenseTime(LocalDateTime.now());
        return updateById(prescription);
    }

    @Override
    public boolean issuePrescription(Long prescriptionId, Long userId, String userName) {
        Prescription prescription = getById(prescriptionId);
        prescription.setStatus(3);
        prescription.setIssueUserId(userId);
        prescription.setIssueUserName(userName);
        prescription.setIssueTime(LocalDateTime.now());
        updateById(prescription);

        QueryWrapper<PrescriptionItem> wrapper = new QueryWrapper<>();
        wrapper.eq("prescription_id", prescriptionId);
        List<PrescriptionItem> items = prescriptionItemMapper.selectList(wrapper);
        for (PrescriptionItem item : items) {
            Drug drug = drugMapper.selectById(item.getDrugId());
            int beforeStock = drug.getStock();
            int afterStock = beforeStock - item.getQuantity();
            drug.setStock(afterStock);
            drugMapper.updateById(drug);

            DrugStockLog log = new DrugStockLog();
            log.setDrugId(drug.getId());
            log.setType(2);
            log.setQuantity(item.getQuantity());
            log.setBeforeStock(beforeStock);
            log.setAfterStock(afterStock);
            log.setReason("处方发药");
            log.setRelatedId(prescriptionId);
            log.setOperatorId(userId);
            log.setOperatorName(userName);
            log.setCreateTime(LocalDateTime.now());
            drugStockLogMapper.insert(log);
        }
        return true;
    }
}
