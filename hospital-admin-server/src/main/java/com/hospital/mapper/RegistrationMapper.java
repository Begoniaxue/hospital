package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.dto.RegistrationReportDTO;
import com.hospital.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {

    @Select("SELECT * FROM registration WHERE patient_id = #{patientId} AND deleted = 0 ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Registration> selectByPatientId(@Param("patientId") Long patientId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    @Select("SELECT * FROM registration WHERE patient_id = #{patientId} AND status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<Registration> selectByPatientIdAndStatus(@Param("patientId") Long patientId, @Param("status") Integer status);

    @Select("SELECT * FROM registration WHERE schedule_id = #{scheduleId} AND status IN (1, 2) AND deleted = 0 ORDER BY create_time ASC")
    List<Registration> selectByScheduleId(@Param("scheduleId") Long scheduleId);

    @Select("SELECT COUNT(*) FROM registration WHERE schedule_id = #{scheduleId} AND status IN (1, 2) AND deleted = 0")
    Integer countByScheduleId(@Param("scheduleId") Long scheduleId);

    @Update("UPDATE registration SET status = 3, cancel_time = NOW(), cancel_reason = #{reason} WHERE id = #{id} AND status IN (1, 2)")
    int cancelRegistration(@Param("id") Long id, @Param("reason") String reason);

    @Select("SELECT * FROM registration WHERE visit_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 ORDER BY create_time DESC")
    List<Registration> selectByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("SELECT DATE_FORMAT(visit_date, '%Y-%m-%d') as date, " +
            "COUNT(*) as count, " +
            "department, " +
            "registration_type as registrationType, " +
            "SUM(registration_fee) as totalFee " +
            "FROM registration " +
            "WHERE visit_date BETWEEN #{startDate} AND #{endDate} " +
            "AND (#{department} IS NULL OR department = #{department}) " +
            "AND deleted = 0 " +
            "GROUP BY DATE_FORMAT(visit_date, '%Y-%m-%d'), department, registration_type " +
            "ORDER BY date DESC")
    List<RegistrationReportDTO> selectRegistrationStats(@Param("startDate") String startDate, 
                                                        @Param("endDate") String endDate, 
                                                        @Param("department") String department);
}
