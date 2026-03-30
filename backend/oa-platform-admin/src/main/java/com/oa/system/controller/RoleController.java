package com.oa.system.controller;

import com.oa.common.core.result.R;
import com.oa.system.domain.dto.RoleCreateDto;
import com.oa.system.domain.dto.RoleUpdateDto;
import com.oa.system.domain.vo.RoleDetailVo;
import com.oa.system.domain.vo.RoleListVo;
import com.oa.system.domain.dto.RoleQuery;
import com.oa.system.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/system/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "获取角色列表")
    @GetMapping("/list")
    public R<?> getRoleList(RoleQuery query) {
        return R.ok(roleService.getRoleList(query));
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public R<?> getRoleDetail(@PathVariable Long id) {
        return R.ok(roleService.getRoleDetail(id));
    }

    @Operation(summary = "获取所有角色（下拉框用）")
    @GetMapping("/options")
    public R<?> getRoleOptions() {
        return R.ok(roleService.getRoleOptions());
    }

    @Operation(summary = "创建角色")
    @PostMapping
    public R<?> createRole(@RequestBody @Validated RoleCreateDto dto) {
        return R.ok(roleService.createRole(dto));
    }

    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    public R<?> updateRole(@PathVariable Long id, @RequestBody @Validated RoleUpdateDto dto) {
        roleService.updateRole(id, dto);
        return R.ok();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public R<?> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return R.ok();
    }

    @Operation(summary = "授权菜单")
    @PutMapping("/{id}/menu")
    public R<?> grantMenu(@PathVariable Long id, @RequestBody Long[] menuIds) {
        roleService.grantMenu(id, menuIds);
        return R.ok();
    }

    @Operation(summary = "获取角色菜单ID列表")
    @GetMapping("/{id}/menu")
    public R<?> getRoleMenus(@PathVariable Long id) {
        return R.ok(roleService.getRoleMenus(id));
    }
}