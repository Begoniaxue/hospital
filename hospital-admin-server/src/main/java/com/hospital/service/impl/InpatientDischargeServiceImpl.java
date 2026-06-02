package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.Inpatient;
import com.hospital.entity.InpatientDischarge;
import com.hospital.mapper.InpatientDischargeMapper;
import com.hospital.service.InpatientDischargeService;
import com.hospital.service.InpatientFeeService;
import com.hospital.service.InpatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class InpatientDischargeServiceImpl extends ServiceImpl<InpatientDischargeMapper, InpatientDischarge> implements InpatientDischargeService {

    @Autowired
    private InpatientDischargeMapper inpatientDischargeMapper;

    @Autowired
    private InpatientService inpatientService;

    @Autowired
    private InpatientFeeService inpatientFeeService;

    @Override
    public PageResult<InpatientDischarge> getDischargePage(PageQuery pageQuery, String keyword, Integer status) {
        Page<InpatientDischarge> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = (Page<InpatientDischarge>) inpatientDischargeMapper.selectDischargePage(page, keyword, status);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InpatientDischarge settleDischarge(Long inpatientId, InpatientDischarge discharge) {
        Inpatient inpatient = inpatientService.getById(inpatientId);
        if (inpatient == null) {
            throw new RuntimeException("住院记录不存在");
        }

        BigDecimal totalAmount = inpatientFeeService.getTotalAmount(inpatientId);
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }

        discharge.setDischargeNo(generateDischargeNo());
        discharge.setInpatientId(inpatientId);
        discharge.setInpatientNo(inpatient.getInpatientNo());
        discharge.setPatientId(inpatient.getPatientId());
        discharge.setPatientName(inpatient.getPatientName());
        discharge.setAdmissionDate(inpatient.getAdmissionDate());
        discharge.setDischargeDate(LocalDateTime.now());
        discharge.setTotalAmount(totalAmount);
        discharge.setStatus(0);

        BigDecimal depositAmount = discharge.getDepositAmount() != null ? discharge.getDepositAmount() : BigDecimal.ZERO;
        discharge.setDepositAmount(depositAmount);

        BigDecimal diff = depositAmount.subtract(totalAmount);
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            discharge.setRefundAmount(diff);
            discharge.setSupplementAmount(BigDecimal.ZERO);
            discharge.setActualPay(BigDecimal.ZERO);
        } else {
            discharge.setRefundAmount(BigDecimal.ZERO);
            discharge.setSupplementAmount(diff.abs());
            discharge.setActualPay(diff.abs());
        }

        save(discharge);

        inpatient.setStatus(2);
        inpatientService.updateById(inpatient);

        return discharge;
    }

    @Override
    public byte[] printDischarge(Long id) {
        InpatientDischarge discharge = getById(id);
        if (discharge == null) {
            throw new RuntimeException("出院记录不存在");
        }
        return new byte[0];
    }

    private String generateDischargeNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = lambdaQuery()
                .likeRight(InpatientDischarge::getDischargeNo, "DC" + date)
                .count() + 1;
        return "DC" + date + String.format("%04d", count);
    }
}
