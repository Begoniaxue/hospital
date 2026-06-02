package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.InpatientRecord;
import com.hospital.mapper.InpatientRecordMapper;
import com.hospital.service.InpatientRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InpatientRecordServiceImpl extends ServiceImpl<InpatientRecordMapper, InpatientRecord> implements InpatientRecordService {

    @Autowired
    private InpatientRecordMapper inpatientRecordMapper;

    @Override
    public PageResult<InpatientRecord> getRecordPage(PageQuery pageQuery, String keyword, Long inpatientId) {
        Page<InpatientRecord> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = (Page<InpatientRecord>) inpatientRecordMapper.selectRecordPage(page, keyword, inpatientId);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRecord(InpatientRecord record) {
        if (record.getRecordDate() == null) {
            record.setRecordDate(LocalDateTime.now());
        }
        return save(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRecord(InpatientRecord record) {
        return updateById(record);
    }
}
