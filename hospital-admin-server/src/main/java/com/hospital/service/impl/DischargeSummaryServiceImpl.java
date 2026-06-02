package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.entity.DischargeSummary;
import com.hospital.mapper.DischargeSummaryMapper;
import com.hospital.service.DischargeSummaryService;
import org.springframework.stereotype.Service;

@Service
public class DischargeSummaryServiceImpl extends ServiceImpl<DischargeSummaryMapper, DischargeSummary> implements DischargeSummaryService {
}
