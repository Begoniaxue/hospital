package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.RefundRequest;
import org.apache.ibatis.annotations.Param;

public interface RefundRequestMapper extends BaseMapper<RefundRequest> {

    IPage<RefundRequest> selectRefundPage(Page<RefundRequest> page,
                                          @Param("keyword") String keyword,
                                          @Param("status") Integer status,
                                          @Param("startTime") String startTime,
                                          @Param("endTime") String endTime);
}
