package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.Inpatient;

public interface InpatientService extends IService<Inpatient> {

    PageResult<Inpatient> getInpatientPage(PageQuery pageQuery, String keyword, Integer status);

    boolean saveInpatient(Inpatient inpatient);

    boolean updateInpatient(Inpatient inpatient);

    boolean deleteInpatient(Long id);
}
