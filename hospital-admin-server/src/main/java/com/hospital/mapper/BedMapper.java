package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.Bed;
import org.apache.ibatis.annotations.Param;

public interface BedMapper extends BaseMapper<Bed> {

    IPage<Bed> selectBedPage(Page<Bed> page,
                             @Param("keyword") String keyword,
                             @Param("wardId") Long wardId,
                             @Param("status") Integer status);
}
