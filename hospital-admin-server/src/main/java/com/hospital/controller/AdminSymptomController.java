package com.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.PageQuery;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.entity.Symptom;
import com.hospital.service.SymptomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/symptom")
public class AdminSymptomController {

    @Autowired
    private SymptomService symptomService;

    @GetMapping("/page")
    public Result<PageResult<Symptom>> page(PageQuery query,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) Integer status) {
        QueryWrapper<Symptom> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("symptom_name", keyword).or().like("symptom_code", keyword);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByAsc("sort", "id");
        Page<Symptom> page = symptomService.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return Result.success(PageResult.of(page));
    }

    @GetMapping("/list")
    public Result<List<Symptom>> list(@RequestParam(required = false) Integer status) {
        QueryWrapper<Symptom> wrapper = new QueryWrapper<>();
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByAsc("sort", "id");
        List<Symptom> list = symptomService.list(wrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Symptom> getById(@PathVariable Long id) {
        Symptom symptom = symptomService.getById(id);
        return Result.success(symptom);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Symptom symptom) {
        symptomService.save(symptom);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Symptom symptom) {
        symptomService.updateById(symptom);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        symptomService.removeById(id);
        return Result.success();
    }

    @PostMapping("/batch-delete")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        symptomService.removeByIds(ids);
        return Result.success();
    }
}
