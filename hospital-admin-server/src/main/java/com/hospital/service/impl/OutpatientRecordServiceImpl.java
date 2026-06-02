package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.entity.OutpatientRecord;
import com.hospital.mapper.OutpatientRecordMapper;
import com.hospital.service.OutpatientRecordService;
import org.springframework.stereotype.Service;

@Service
public class OutpatientRecordServiceImpl extends ServiceImpl<OutpatientRecordMapper, OutpatientRecord> implements OutpatientRecordService {
}
