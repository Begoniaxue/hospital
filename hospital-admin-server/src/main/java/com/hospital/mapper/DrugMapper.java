package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.Drug;
import org.apache.ibatis.annotations.Param;

public interface DrugMapper extends BaseMapper<Drug> {

    IPage<Drug> selectDrugPage(Page<Drug> page,
                               @Param("keyword") String keyword,
                               @Param("category") String category,
                               @Param("status") Integer status);
}
