package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.Inpatient;
import org.apache.ibatis.annotations.Param;

public interface InpatientMapper extends BaseMapper<Inpatient> {

    IPage<Inpatient> selectInpatientPage(Page<Inpatient> page,
                                          @Param("keyword") String keyword,
                                          @Param("status") Integer status);
}
