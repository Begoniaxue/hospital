package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.Inpatient;
import com.hospital.mapper.InpatientMapper;
import com.hospital.service.InpatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class InpatientServiceImpl extends ServiceImpl<InpatientMapper, Inpatient> implements InpatientService {

    @Autowired
    private InpatientMapper inpatientMapper;

    @Override
    public PageResult<Inpatient> getInpatientPage(PageQuery pageQuery, String keyword, Integer status) {
        Page<Inpatient> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = (Page<Inpatient>) inpatientMapper.selectInpatientPage(page, keyword, status);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public boolean saveInpatient(Inpatient inpatient) {
        if (inpatient.getInpatientNo() == null || inpatient.getInpatientNo().isEmpty()) {
            inpatient.setInpatientNo(generateInpatientNo());
        }
        return save(inpatient);
    }

    @Override
    public boolean updateInpatient(Inpatient inpatient) {
        return updateById(inpatient);
    }

    @Override
    public boolean deleteInpatient(Long id) {
        return removeById(id);
    }

    private String generateInpatientNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = lambdaQuery()
                .likeRight(Inpatient::getInpatientNo, "I" + date)
                .count() + 1;
        return "I" + date + String.format("%04d", count);
    }
}
