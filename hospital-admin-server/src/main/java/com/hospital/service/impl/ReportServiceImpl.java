package com.hospital.service.impl;

import com.hospital.dto.*;
import com.hospital.mapper.OutpatientRecordMapper;
import com.hospital.mapper.RegistrationMapper;
import com.hospital.mapper.ReportMapper;
import com.hospital.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private RegistrationMapper registrationMapper;

    @Autowired
    private OutpatientRecordMapper outpatientRecordMapper;

    @Override
    public List<RegistrationReportDTO> getRegistrationReport(String startDate, String endDate, String department) {
        return registrationMapper.selectRegistrationStats(startDate, endDate, department);
    }

    @Override
    public List<OutpatientReportDTO> getOutpatientReport(String startDate, String endDate, String department, Long doctorId) {
        return outpatientRecordMapper.selectOutpatientStats(startDate, endDate, department, doctorId);
    }

    @Override
    public List<DoctorWorkloadDTO> getDoctorWorkloadReport(String startDate, String endDate, String department) {
        return reportMapper.selectDoctorWorkload(startDate, endDate, department);
    }

    @Override
    public List<RevenueReportDTO> getRevenueReport(String startDate, String endDate, String dimension, String department, Long doctorId) {
        return reportMapper.selectRevenueStats(startDate, endDate, dimension, department, doctorId);
    }

    @Override
    public List<DrugStockReportDTO> getDrugStockReport(String startDate, String endDate) {
        return reportMapper.selectDrugStockStats(startDate, endDate);
    }

    @Override
    public List<PatientTrendDTO> getPatientTrendReport(String startDate, String endDate) {
        return reportMapper.selectPatientTrend(startDate, endDate);
    }
}
