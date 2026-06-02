package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.InpatientArchive;
import org.apache.ibatis.annotations.Param;

public interface InpatientArchiveMapper extends BaseMapper<InpatientArchive> {

    IPage<InpatientArchive> selectArchivePage(Page<InpatientArchive> page,
                                               @Param("keyword") String keyword,
                                               @Param("status") Integer status);
}
