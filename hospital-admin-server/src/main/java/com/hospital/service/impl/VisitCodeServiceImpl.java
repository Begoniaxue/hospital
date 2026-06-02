package com.hospital.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.entity.VisitCode;
import com.hospital.mapper.VisitCodeMapper;
import com.hospital.service.VisitCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class VisitCodeServiceImpl extends ServiceImpl<VisitCodeMapper, VisitCode> implements VisitCodeService {

    @Autowired
    private VisitCodeMapper visitCodeMapper;

    @Override
    public VisitCode generateVisitCode(Long patientId, String codeType, String useScene) {
        VisitCode visitCode = new VisitCode();
        visitCode.setPatientId(patientId);
        visitCode.setCodeType(codeType);
        visitCode.setUseScene(useScene);
        visitCode.setStatus(1);

        String codeContent = "HOSPITAL" + IdUtil.getSnowflakeNextIdStr();
        visitCode.setCodeContent(codeContent);

        String qrData = patientId + "|" + codeContent + "|" + System.currentTimeMillis();
        visitCode.setQrCodeUrl("data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(qrData.getBytes()));

        visitCode.setExpireTime(LocalDateTime.now().plusMinutes(30));

        save(visitCode);
        return visitCode;
    }

    @Override
    public VisitCode getLatestByPatientId(Long patientId) {
        VisitCode code = visitCodeMapper.selectLatestByPatientId(patientId);
        if (code != null && code.getExpireTime().isAfter(LocalDateTime.now())) {
            return code;
        }
        return generateVisitCode(patientId, "PATIENT", "GENERAL");
    }

    @Override
    public boolean verifyVisitCode(String codeContent) {
        VisitCode code = lambdaQuery().eq(VisitCode::getCodeContent, codeContent).one();
        if (code != null && code.getStatus() == 1 && code.getExpireTime().isAfter(LocalDateTime.now())) {
            code.setUseRecord("已核验 " + LocalDateTime.now());
            updateById(code);
            return true;
        }
        return false;
    }
}
