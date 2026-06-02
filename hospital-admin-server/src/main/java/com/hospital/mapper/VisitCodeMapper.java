package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.VisitCode;

public interface VisitCodeMapper extends BaseMapper<VisitCode> {

    VisitCode selectLatestByPatientId(Long patientId);
}
