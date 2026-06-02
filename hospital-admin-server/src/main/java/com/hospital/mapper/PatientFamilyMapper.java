package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.PatientFamily;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PatientFamilyMapper extends BaseMapper<PatientFamily> {

    List<PatientFamily> selectByWechatUserId(@Param("wechatUserId") Long wechatUserId);
}
