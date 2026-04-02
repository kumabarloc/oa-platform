package com.oa.system.controller;

import com.oa.common.core.result.R;
import com.oa.common.core.security.SecurityUtils;
import com.oa.system.domain.vo.UserDetailVo;
import com.oa.system.domain.dto.UserQuery;
import com.oa.system.domain.dto.UserCreateDto;
import com.oa.system.domain.dto.UserUpdateDto;
import com.oa.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/system/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public R<?> getCurrentUserInfo() {
        return R.ok(userService.getCurrentUserInfo());
    }

    @Operation(summary = "获取用户列表")
    @GetMapping("/list")
    public R<?> getUserList(UserQuery query) {
        return R.ok(userService.getUserList(query));
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public R<?> getUserDetail(@PathVariable Long id) {
        return R.ok(userService.getUserDetail(id));
    }

    @Operation(summary = "创建用户")
    @PostMapping
    public R<?> createUser(@RequestBody @Validated UserCreateDto dto) {
        return R.ok(userService.createUser(dto));
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public R<?> updateUser(@PathVariable Long id, @RequestBody @Validated UserUpdateDto dto) {
        userService.updateUser(id, dto);
        return R.ok();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public R<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return R.ok();
    }

    @Operation(summary = "重置用户密码")
    @PutMapping("/{id}/password")
    public R<?> resetPassword(@PathVariable Long id, @RequestBody PasswordRequest request) {
        userService.resetPassword(id, request.getPassword());
        return R.ok();
    }

    @Operation(summary = "修改当前用户密码")
    @PutMapping("/password")
    public R<?> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request.getOldPassword(), request.getNewPassword());
        return R.ok();
    }

    @Data
    public static class PasswordRequest {
        private String password;
    }

    @Data
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }
}
