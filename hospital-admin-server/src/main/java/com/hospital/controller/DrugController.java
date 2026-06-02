package com.hospital.controller;

import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.dto.DrugStockDTO;
import com.hospital.entity.Drug;
import com.hospital.entity.DrugStockLog;
import com.hospital.entity.SysUser;
import com.hospital.service.DrugService;
import com.hospital.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pharmacy/drug")
public class DrugController {

    @Autowired
    private DrugService drugService;

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('pharmacy:drug:query')")
    public Result<PageResult<Drug>> page(PageQuery pageQuery,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String category,
                                         @RequestParam(required = false) Integer status) {
        return Result.success(drugService.getDrugPage(pageQuery, keyword, category, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('pharmacy:drug:query')")
    public Result<Drug> getById(@PathVariable Long id) {
        return Result.success(drugService.getDrugDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('pharmacy:drug:add')")
    public Result<?> save(@RequestBody Drug drug) {
        boolean success = drugService.saveDrug(drug);
        return success ? Result.success("新增成功") : Result.error("新增失败");
    }

    @PutMapping
    @PreAuthorize("hasAuthority('pharmacy:drug:edit')")
    public Result<?> update(@RequestBody Drug drug) {
        boolean success = drugService.updateDrug(drug);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('pharmacy:drug:delete')")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = drugService.deleteDrug(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PostMapping("/stockIn")
    @PreAuthorize("hasAuthority('pharmacy:stock:in')")
    public Result<?> stockIn(@RequestBody DrugStockDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser currentUser = sysUserService.getByUsername(username);
        boolean success = drugService.stockIn(dto, currentUser.getId(), currentUser.getUsername());
        return success ? Result.success("入库成功") : Result.error("入库失败");
    }

    @PostMapping("/stockOut")
    @PreAuthorize("hasAuthority('pharmacy:stock:out')")
    public Result<?> stockOut(@RequestBody DrugStockDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser currentUser = sysUserService.getByUsername(username);
        boolean success = drugService.stockOut(dto, currentUser.getId(), currentUser.getUsername());
        return success ? Result.success("出库成功") : Result.error("出库失败");
    }

    @GetMapping("/stockLog")
    @PreAuthorize("hasAuthority('pharmacy:stock:query')")
    public Result<PageResult<DrugStockLog>> stockLog(PageQuery pageQuery,
                                                     @RequestParam(required = false) Long drugId,
                                                     @RequestParam(required = false) Integer type,
                                                     @RequestParam(required = false) String startTime,
                                                     @RequestParam(required = false) String endTime) {
        return Result.success(drugService.getStockLogPage(pageQuery, drugId, type, startTime, endTime));
    }

    @GetMapping("/warning")
    @PreAuthorize("hasAuthority('pharmacy:stock:query')")
    public Result<List<Drug>> warning() {
        return Result.success(drugService.getWarningDrugs());
    }

    @GetMapping("/expired")
    @PreAuthorize("hasAuthority('pharmacy:stock:query')")
    public Result<List<Drug>> expired() {
        return Result.success(drugService.getExpiredDrugs());
    }
}
