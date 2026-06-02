package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.InpatientArchive;
import com.hospital.service.InpatientArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inpatient/archive")
public class InpatientArchiveController {

    @Autowired
    private InpatientArchiveService inpatientArchiveService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('inpatient:archive:query')")
    public Result<PageResult<InpatientArchive>> page(PageQuery pageQuery,
                                                     @RequestParam(required = false) String keyword,
                                                     @RequestParam(required = false) Integer status) {
        return Result.success(inpatientArchiveService.getArchivePage(pageQuery, keyword, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('inpatient:archive:query')")
    public Result<InpatientArchive> getById(@PathVariable Long id) {
        return Result.success(inpatientArchiveService.getById(id));
    }

    @PostMapping("/archive")
    @PreAuthorize("hasAuthority('inpatient:archive:file')")
    public Result<?> archive(@RequestParam Long inpatientId) {
        boolean success = inpatientArchiveService.fileArchive(inpatientId);
        return success ? Result.success("归档成功") : Result.error("归档失败");
    }

    @PutMapping("/{id}/borrow")
    @PreAuthorize("hasAuthority('inpatient:archive:borrow')")
    public Result<?> borrow(@PathVariable Long id,
                            @RequestParam Long borrowUserId,
                            @RequestParam String borrowUserName,
                            @RequestParam Integer days) {
        boolean success = inpatientArchiveService.borrowArchive(id, borrowUserId, borrowUserName, days);
        return success ? Result.success("借阅成功") : Result.error("借阅失败");
    }

    @PutMapping("/{id}/return")
    @PreAuthorize("hasAuthority('inpatient:archive:borrow')")
    public Result<?> returnArchive(@PathVariable Long id) {
        boolean success = inpatientArchiveService.returnArchive(id);
        return success ? Result.success("归还成功") : Result.error("归还失败");
    }
}
