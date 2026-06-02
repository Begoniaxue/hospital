package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.dto.OutpatientReportDTO;
import com.hospital.entity.OutpatientRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OutpatientRecordMapper extends BaseMapper<OutpatientRecord> {

    List<OutpatientReportDTO> selectOutpatientStats(@Param("startDate") String startDate,
                                                    @Param("endDate") String endDate,
                                                    @Param("department") String department,
                                                    @Param("doctorId") Long doctorId);
}
