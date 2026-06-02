package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.Bed;
import com.hospital.entity.Inpatient;
import com.hospital.mapper.BedMapper;
import com.hospital.service.BedService;
import com.hospital.service.InpatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BedServiceImpl extends ServiceImpl<BedMapper, Bed> implements BedService {

    @Autowired
    private BedMapper bedMapper;

    @Autowired
    private InpatientService inpatientService;

    @Override
    public PageResult<Bed> getBedPage(PageQuery pageQuery, String keyword, Long wardId, Integer status) {
        Page<Bed> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = (Page<Bed>) bedMapper.selectBedPage(page, keyword, wardId, status);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignBed(Long bedId, Long inpatientId) {
        Bed bed = getById(bedId);
        if (bed == null) {
            throw new RuntimeException("床位不存在");
        }
        if (bed.getStatus() != 0) {
            throw new RuntimeException("床位已被占用");
        }

        Inpatient inpatient = inpatientService.getById(inpatientId);
        if (inpatient == null) {
            throw new RuntimeException("住院记录不存在");
        }

        bed.setStatus(1);
        boolean bedUpdated = updateById(bed);

        inpatient.setBedId(bedId);
        inpatient.setBedNo(bed.getBedNo());
        inpatient.setWardId(bed.getWardId());
        inpatient.setWardName(bed.getWardName());
        boolean inpatientUpdated = inpatientService.updateById(inpatient);

        return bedUpdated && inpatientUpdated;
    }
}
