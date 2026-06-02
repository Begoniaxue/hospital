package com.hospital.service;

import com.hospital.dto.*;

import java.util.List;

public interface ReportService {

    List<RegistrationReportDTO> getRegistrationReport(String startDate, String endDate, String department);

    List<OutpatientReportDTO> getOutpatientReport(String startDate, String endDate, String department, Long doctorId);

    List<DoctorWorkloadDTO> getDoctorWorkloadReport(String startDate, String endDate, String department);

    List<RevenueReportDTO> getRevenueReport(String startDate, String endDate, String dimension, String department, Long doctorId);

    List<DrugStockReportDTO> getDrugStockReport(String startDate, String endDate);

    List<PatientTrendDTO> getPatientTrendReport(String startDate, String endDate);
}
