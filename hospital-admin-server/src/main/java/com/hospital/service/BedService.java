package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.Bed;

public interface BedService extends IService<Bed> {

    PageResult<Bed> getBedPage(PageQuery pageQuery, String keyword, Long wardId, Integer status);

    boolean assignBed(Long bedId, Long inpatientId);
}
