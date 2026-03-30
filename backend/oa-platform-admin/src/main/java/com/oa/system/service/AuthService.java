package com.oa.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oa.common.core.exception.BusinessException;
import com.oa.common.core.result.ResultCode;
import com.oa.common.core.security.JwtUtils;
import com.oa.common.core.security.LoginUser;
import com.oa.common.core.security.SecurityUtils;
import com.oa.system.entity.SysUser;
import com.oa.system.mapper.SysUserMapper;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务
 */
@Service
public class AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public AuthService(SysUserMapper userMapper,
                      PasswordEncoder passwordEncoder,
                      JwtUtils jwtUtils,
                      RedisTemplate<String, Object> redisTemplate,
                      ObjectMapper objectMapper) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 用户登录
     */
    public LoginVo login(String username, String password, String captchaId, String captchaCode) {
        // 验证验证码
        if (captchaId != null && captchaCode != null) {
            Object cachedCode = redisTemplate.opsForValue().get("captcha:" + captchaId);
            if (cachedCode == null || !cachedCode.toString().equalsIgnoreCase(captchaCode)) {
                throw new BusinessException(ResultCode.CAPTCHA_ERROR);
            }
            redisTemplate.delete("captcha:" + captchaId);
        }

        // 查找用户
        SysUser user = userMapper.selectByUserName(username);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 验证状态
        if (user.getStatus() != 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 构建登录用户信息
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUserName(user.getUserName());
        loginUser.setNickName(user.getNickName());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setUserType(user.getUserType());
        loginUser.setLoginTime(LocalDateTime.now());

        // 当前线程内也设好（供本次请求后续使用）
        SecurityUtils.setLoginUser(loginUser);
        // 生成 Token
        String token = jwtUtils.generateToken(loginUser);

        // 缓存登录用户信息到 Redis
        String redisKey = "login:user:" + user.getId();
        redisTemplate.opsForValue().set(redisKey, loginUser, 2, TimeUnit.HOURS);

        LoginVo vo = new LoginVo();
        vo.setToken(token);
        vo.setUserInfo(loginUser);
        return vo;
    }

    /**
     * 获取当前登录用户信息
     */
    public LoginUser getLoginInfo() {
        return SecurityUtils.getLoginUser();
    }

    /**
     * 退出登录
     */
    public void logout() {
        Long userId = SecurityUtils.getUserId();
        if (userId != null) {
            redisTemplate.delete("login:user:" + userId);
        }
    }

    /**
     * 刷新 Token
     */
    public String refreshToken() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return jwtUtils.refreshToken(jwtUtils.generateToken(loginUser));
    }

    @Data
    public static class LoginVo {
        private String token;
        private LoginUser userInfo;
    }
}