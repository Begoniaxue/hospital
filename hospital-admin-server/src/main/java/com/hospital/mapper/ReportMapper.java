package com.hospital.mapper;

import com.hospital.dto.DoctorWorkloadDTO;
import com.hospital.dto.DrugStockReportDTO;
import com.hospital.dto.PatientTrendDTO;
import com.hospital.dto.RevenueReportDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportMapper {

    List<DoctorWorkloadDTO> selectDoctorWorkload(@Param("startDate") String startDate,
                                                 @Param("endDate") String endDate,
                                                 @Param("department") String department);

    List<RevenueReportDTO> selectRevenueStats(@Param("startDate") String startDate,
                                              @Param("endDate") String endDate,
                                              @Param("dimension") String dimension,
                                              @Param("department") String department,
                                              @Param("doctorId") Long doctorId);

    List<DrugStockReportDTO> selectDrugStockStats(@Param("startDate") String startDate,
                                                  @Param("endDate") String endDate);

    List<PatientTrendDTO> selectPatientTrend(@Param("startDate") String startDate,
                                             @Param("endDate") String endDate);
}
