package com.oa.system.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.oa.common.core.result.R;
import com.oa.system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.UUID;

/**
 * 认证控制器
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RedisTemplate<String, Object> redisTemplate;

    public AuthController(AuthService authService, RedisTemplate<String, Object> redisTemplate) {
        this.authService = authService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取验证码
     */
    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response) throws IOException {
        // 生成图片验证码
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 50);
        String captchaId = UUID.randomUUID().toString();
        String captchaCode = captcha.getCode();

        // 存入 Redis，5分钟有效期
        redisTemplate.opsForValue().set("captcha:" + captchaId, captchaCode, 300);

        response.setContentType("image/png");
        response.setHeader("Captcha-Id", captchaId);
        captcha.write(response.getOutputStream());
    }

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<?> login(@RequestBody @Valid LoginRequest request,
                      @RequestHeader(value = "Captcha-Id", required = false) String captchaId,
                      @RequestHeader(value = "Captcha-Code", required = false) String captchaCode) {
        return R.ok(authService.login(request.getUsername(), request.getPassword(), captchaId, captchaCode));
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public R<?> getLoginInfo() {
        return R.ok(authService.getLoginInfo());
    }

    /**
     * 退出登录
     */
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public R<?> logout() {
        authService.logout();
        return R.ok();
    }

    /**
     * 刷新 Token
     */
    @Operation(summary = "刷新 Token")
    @PostMapping("/refresh")
    public R<?> refreshToken() {
        return R.ok(authService.refreshToken());
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}