package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.dto.RegistrationReportDTO;
import com.hospital.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {

    List<RegistrationReportDTO> selectRegistrationStats(@Param("startDate") String startDate,
                                                        @Param("endDate") String endDate,
                                                        @Param("department") String department);
}
