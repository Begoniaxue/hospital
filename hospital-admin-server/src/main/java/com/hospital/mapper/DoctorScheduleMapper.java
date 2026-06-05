package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.DoctorSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DoctorScheduleMapper extends BaseMapper<DoctorSchedule> {

    @Select("SELECT * FROM doctor_schedule WHERE doctor_id = #{doctorId} AND schedule_date BETWEEN #{startDate} AND #{endDate} AND is_suspended = 0 AND status = 1 AND deleted = 0 ORDER BY schedule_date ASC, time_slot ASC")
    List<DoctorSchedule> selectByDoctorAndDateRange(@Param("doctorId") Long doctorId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("SELECT * FROM doctor_schedule WHERE department_id = #{departmentId} AND schedule_date = #{date} AND is_suspended = 0 AND status = 1 AND deleted = 0 ORDER BY doctor_id ASC, time_slot ASC")
    List<DoctorSchedule> selectByDepartmentAndDate(@Param("departmentId") Long departmentId, @Param("date") LocalDate date);

    @Select("SELECT * FROM doctor_schedule WHERE schedule_date = #{date} AND is_suspended = 0 AND status = 1 AND deleted = 0 ORDER BY department_id ASC, doctor_id ASC, time_slot ASC")
    List<DoctorSchedule> selectByDate(@Param("date") LocalDate date);

    @Select("SELECT * FROM doctor_schedule WHERE id = #{id} AND is_suspended = 0 AND status = 1 AND deleted = 0 FOR UPDATE")
    DoctorSchedule selectByIdForUpdate(@Param("id") Long id);

    @Update("UPDATE doctor_schedule SET remaining_count = remaining_count - 1 WHERE id = #{id} AND remaining_count > 0 AND is_suspended = 0 AND deleted = 0")
    int decreaseRemainingCount(@Param("id") Long id);

    @Update("UPDATE doctor_schedule SET remaining_count = remaining_count + 1 WHERE id = #{id} AND is_suspended = 0 AND deleted = 0")
    int increaseRemainingCount(@Param("id") Long id);

    @Update("UPDATE doctor_schedule SET doctor_name = #{doctorName} WHERE doctor_id = #{doctorId} AND deleted = 0")
    int updateDoctorNameByDoctorId(@Param("doctorId") Long doctorId, @Param("doctorName") String doctorName);

    @Update("UPDATE doctor_schedule SET department_name = #{departmentName}, department_id = #{departmentId} WHERE doctor_id = #{doctorId} AND deleted = 0")
    int updateDepartmentByDoctorId(@Param("doctorId") Long doctorId, @Param("departmentId") Long departmentId, @Param("departmentName") String departmentName);
}
