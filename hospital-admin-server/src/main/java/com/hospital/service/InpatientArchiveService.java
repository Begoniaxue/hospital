package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.InpatientArchive;

public interface InpatientArchiveService extends IService<InpatientArchive> {

    PageResult<InpatientArchive> getArchivePage(PageQuery pageQuery, String keyword, Integer status);

    boolean fileArchive(Long inpatientId);

    boolean borrowArchive(Long id, Long borrowUserId, String borrowUserName, Integer days);

    boolean returnArchive(Long id);
}
