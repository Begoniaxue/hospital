package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.InpatientRecord;

public interface InpatientRecordService extends IService<InpatientRecord> {

    PageResult<InpatientRecord> getRecordPage(PageQuery pageQuery, String keyword, Long inpatientId);

    boolean saveRecord(InpatientRecord record);

    boolean updateRecord(InpatientRecord record);
}
