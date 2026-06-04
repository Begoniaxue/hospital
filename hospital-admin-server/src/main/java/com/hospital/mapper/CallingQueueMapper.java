package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.CallingQueue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CallingQueueMapper extends BaseMapper<CallingQueue> {

    @Select("SELECT * FROM calling_queue WHERE doctor_id = #{doctorId} AND visit_date = #{date} ORDER BY queue_number ASC")
    List<CallingQueue> selectByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    @Select("SELECT MAX(queue_number) FROM calling_queue WHERE doctor_id = #{doctorId} AND visit_date = #{date} AND time_slot = #{timeSlot}")
    Integer getMaxQueueNumber(@Param("doctorId") Long doctorId, @Param("date") LocalDate date, @Param("timeSlot") String timeSlot);

    @Select("SELECT * FROM calling_queue WHERE registration_id = #{registrationId}")
    CallingQueue selectByRegistrationId(@Param("registrationId") Long registrationId);

    @Update("UPDATE calling_queue SET current_number = #{currentNumber}, queue_status = 1 WHERE doctor_id = #{doctorId} AND visit_date = #{date}")
    int updateCurrentNumber(@Param("doctorId") Long doctorId, @Param("date") LocalDate date, @Param("currentNumber") Integer currentNumber);
}
