package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.entity.Inpatient;
import com.hospital.entity.InpatientArchive;
import com.hospital.mapper.InpatientArchiveMapper;
import com.hospital.service.InpatientArchiveService;
import com.hospital.service.InpatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class InpatientArchiveServiceImpl extends ServiceImpl<InpatientArchiveMapper, InpatientArchive> implements InpatientArchiveService {

    @Autowired
    private InpatientArchiveMapper inpatientArchiveMapper;

    @Autowired
    private InpatientService inpatientService;

    @Override
    public PageResult<InpatientArchive> getArchivePage(PageQuery pageQuery, String keyword, Integer status) {
        Page<InpatientArchive> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = (Page<InpatientArchive>) inpatientArchiveMapper.selectArchivePage(page, keyword, status);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean fileArchive(Long inpatientId) {
        Inpatient inpatient = inpatientService.getById(inpatientId);
        if (inpatient == null) {
            throw new RuntimeException("住院记录不存在");
        }

        InpatientArchive archive = new InpatientArchive();
        archive.setArchiveNo(generateArchiveNo());
        archive.setInpatientId(inpatientId);
        archive.setInpatientNo(inpatient.getInpatientNo());
        archive.setPatientId(inpatient.getPatientId());
        archive.setPatientName(inpatient.getPatientName());
        archive.setArchiveStatus(1);
        archive.setArchiveTime(LocalDateTime.now());
        return save(archive);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean borrowArchive(Long id, Long borrowUserId, String borrowUserName, Integer days) {
        InpatientArchive archive = getById(id);
        if (archive == null) {
            throw new RuntimeException("档案不存在");
        }
        if (archive.getArchiveStatus() != 1) {
            throw new RuntimeException("档案状态异常，无法借阅");
        }

        archive.setArchiveStatus(2);
        archive.setBorrowUserId(borrowUserId);
        archive.setBorrowUserName(borrowUserName);
        archive.setBorrowTime(LocalDateTime.now());
        archive.setExpectedReturnTime(LocalDateTime.now().plusDays(days));
        return updateById(archive);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnArchive(Long id) {
        InpatientArchive archive = getById(id);
        if (archive == null) {
            throw new RuntimeException("档案不存在");
        }
        if (archive.getArchiveStatus() != 2) {
            throw new RuntimeException("档案状态异常，无法归还");
        }

        archive.setArchiveStatus(1);
        archive.setActualReturnTime(LocalDateTime.now());
        return updateById(archive);
    }

    private String generateArchiveNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = lambdaQuery()
                .likeRight(InpatientArchive::getArchiveNo, "A" + date)
                .count() + 1;
        return "A" + date + String.format("%04d", count);
    }
}
