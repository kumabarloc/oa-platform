package com.oa.system.controller;

import com.oa.common.core.result.R;
import com.oa.system.domain.vo.DeptTreeVo;
import com.oa.system.domain.dto.DeptCreateDto;
import com.oa.system.domain.dto.DeptUpdateDto;
import com.oa.system.service.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/api/system/dept")
public class DeptController {

    private final DeptService deptService;

    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    @Operation(summary = "获取部门树")
    @GetMapping("/tree")
    public R<?> getDeptTree() {
        return R.ok(deptService.getDeptTree());
    }

    @Operation(summary = "获取部门详情")
    @GetMapping("/{id}")
    public R<?> getDeptDetail(@PathVariable Long id) {
        return R.ok(deptService.getDeptDetail(id));
    }

    @Operation(summary = "创建部门")
    @PostMapping
    public R<?> createDept(@RequestBody @Validated DeptCreateDto dto) {
        return R.ok(deptService.createDept(dto));
    }

    @Operation(summary = "更新部门")
    @PutMapping("/{id}")
    public R<?> updateDept(@PathVariable Long id, @RequestBody @Validated DeptUpdateDto dto) {
        deptService.updateDept(id, dto);
        return R.ok();
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    public R<?> deleteDept(@PathVariable Long id) {
        deptService.deleteDept(id);
        return R.ok();
    }
}