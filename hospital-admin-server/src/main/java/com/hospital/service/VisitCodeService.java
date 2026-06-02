package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.entity.VisitCode;

public interface VisitCodeService extends IService<VisitCode> {

    VisitCode generateVisitCode(Long patientId, String codeType, String useScene);

    VisitCode getLatestByPatientId(Long patientId);

    boolean verifyVisitCode(String codeContent);
}
