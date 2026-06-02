package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.Bed;
import com.hospital.service.BedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inpatient/bed")
public class BedController {

    @Autowired
    private BedService bedService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('inpatient:bed:query')")
    public Result<PageResult<Bed>> page(PageQuery pageQuery,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Long wardId,
                                        @RequestParam(required = false) Integer status) {
        return Result.success(bedService.getBedPage(pageQuery, keyword, wardId, status));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('inpatient:bed:query')")
    public Result<List<Bed>> list() {
        return Result.success(bedService.list());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('inpatient:bed:query')")
    public Result<Bed> getById(@PathVariable Long id) {
        return Result.success(bedService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('inpatient:bed:add')")
    public Result<?> save(@RequestBody Bed bed) {
        boolean success = bedService.save(bed);
        return success ? Result.success("新增成功") : Result.error("新增失败");
    }

    @PutMapping
    @PreAuthorize("hasAuthority('inpatient:bed:edit')")
    public Result<?> update(@RequestBody Bed bed) {
        boolean success = bedService.updateById(bed);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('inpatient:bed:edit')")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = bedService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('inpatient:bed:assign')")
    public Result<?> assign(@PathVariable Long id, @RequestParam Long inpatientId) {
        boolean success = bedService.assignBed(id, inpatientId);
        return success ? Result.success("分配床位成功") : Result.error("分配床位失败");
    }

    @PutMapping("/{id}/release")
    @PreAuthorize("hasAuthority('inpatient:bed:assign')")
    public Result<?> release(@PathVariable Long id) {
        Bed bed = bedService.getById(id);
        if (bed == null) {
            return Result.error("床位不存在");
        }
        bed.setStatus(0);
        boolean success = bedService.updateById(bed);
        return success ? Result.success("释放床位成功") : Result.error("释放床位失败");
    }
}
