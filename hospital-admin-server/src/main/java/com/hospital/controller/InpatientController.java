package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.Inpatient;
import com.hospital.service.InpatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inpatient/admission")
public class InpatientController {

    @Autowired
    private InpatientService inpatientService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('inpatient:admission:query')")
    public Result<PageResult<Inpatient>> page(PageQuery pageQuery,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Integer status) {
        return Result.success(inpatientService.getInpatientPage(pageQuery, keyword, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('inpatient:admission:query')")
    public Result<Inpatient> getById(@PathVariable Long id) {
        return Result.success(inpatientService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('inpatient:admission:add')")
    public Result<?> save(@RequestBody Inpatient inpatient) {
        boolean success = inpatientService.saveInpatient(inpatient);
        return success ? Result.success("新增成功") : Result.error("新增失败");
    }

    @PutMapping
    @PreAuthorize("hasAuthority('inpatient:admission:edit')")
    public Result<?> update(@RequestBody Inpatient inpatient) {
        boolean success = inpatientService.updateInpatient(inpatient);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('inpatient:admission:delete')")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = inpatientService.deleteInpatient(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PutMapping("/{id}/discharge")
    @PreAuthorize("hasAuthority('inpatient:admission:edit')")
    public Result<?> discharge(@PathVariable Long id) {
        Inpatient inpatient = inpatientService.getById(id);
        if (inpatient == null) {
            return Result.error("住院记录不存在");
        }
        inpatient.setStatus(2);
        boolean success = inpatientService.updateById(inpatient);
        return success ? Result.success("办理出院成功") : Result.error("办理出院失败");
    }
}
