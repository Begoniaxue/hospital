package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.dto.RegistrationCancelDTO;
import com.hospital.dto.RegistrationCreateDTO;
import com.hospital.dto.RegistrationPayDTO;
import com.hospital.dto.RegistrationVO;
import com.hospital.entity.Registration;

import java.time.LocalDate;
import java.util.List;

public interface RegistrationService extends IService<Registration> {

    RegistrationVO create(RegistrationCreateDTO dto, Long operatorId, String operatorName);

    boolean cancel(RegistrationCancelDTO dto, Long operatorId, String operatorName);

    RegistrationVO pay(RegistrationPayDTO dto, Long operatorId, String operatorName);

    List<RegistrationVO> listByPatientId(Long patientId, Integer status, Integer offset, Integer limit);

    RegistrationVO getDetail(Long id);

    RegistrationVO checkin(Long id);

    RegistrationVO finish(Long id);

    RegistrationVO refund(Long id, String reason);

    RegistrationVO getQueueInfo(Long id);

    PageResult<RegistrationVO> adminPage(PageQuery query, String registrationNo, String patientName, Long departmentId, Long doctorId, LocalDate startDate, LocalDate endDate, Integer status);

    String generateRegistrationNo();
}
