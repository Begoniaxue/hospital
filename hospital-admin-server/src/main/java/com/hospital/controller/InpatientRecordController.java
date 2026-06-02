package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.InpatientRecord;
import com.hospital.service.InpatientRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inpatient/record")
public class InpatientRecordController {

    @Autowired
    private InpatientRecordService inpatientRecordService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('inpatient:record:query')")
    public Result<PageResult<InpatientRecord>> page(PageQuery pageQuery,
                                                    @RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) Long inpatientId) {
        return Result.success(inpatientRecordService.getRecordPage(pageQuery, keyword, inpatientId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('inpatient:record:query')")
    public Result<InpatientRecord> getById(@PathVariable Long id) {
        return Result.success(inpatientRecordService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('inpatient:record:add')")
    public Result<?> save(@RequestBody InpatientRecord record) {
        boolean success = inpatientRecordService.saveRecord(record);
        return success ? Result.success("新增病程记录成功") : Result.error("新增病程记录失败");
    }

    @PutMapping
    @PreAuthorize("hasAuthority('inpatient:record:edit')")
    public Result<?> update(@RequestBody InpatientRecord record) {
        boolean success = inpatientRecordService.updateRecord(record);
        return success ? Result.success("修改病程记录成功") : Result.error("修改病程记录失败");
    }
}
