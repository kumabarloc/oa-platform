package com.oa.system.controller;

import com.oa.common.core.result.R;
import com.oa.system.domain.vo.MenuTreeVo;
import com.oa.system.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/api/system/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public R<?> getMenuTree() {
        return R.ok(menuService.getMenuTree());
    }

    @Operation(summary = "获取菜单详情")
    @GetMapping("/{id}")
    public R<?> getMenuDetail(@PathVariable Long id) {
        return R.ok(menuService.getMenuDetail(id));
    }

    @Operation(summary = "获取当前用户菜单")
    @GetMapping("/user")
    public R<?> getUserMenus() {
        return R.ok(menuService.getUserMenus());
    }

    @Operation(summary = "获取角色菜单")
    @GetMapping("/role/{roleId}")
    public R<?> getRoleMenus(@PathVariable Long roleId) {
        return R.ok(menuService.getRoleMenus(roleId));
    }
}