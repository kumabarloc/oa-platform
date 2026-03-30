package com.oa.framework.security;

import com.oa.common.core.security.JwtUtils;
import com.oa.common.core.security.LoginUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.jwtUtils = jwtUtils;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            try {
                // 从 Redis 获取登录用户信息
                String redisKey = "login:user:" + jwtUtils.getUserId(token);
                Object cachedUser = redisTemplate.opsForValue().get(redisKey);

                if (cachedUser != null) {
                    LoginUser loginUser = objectMapper.convertValue(cachedUser, LoginUser.class);
                    setAuthentication(loginUser);
                    com.oa.common.core.security.SecurityUtils.setLoginUser(loginUser);
                } else {
                    // Token 有效但 Redis 无数据，可能是重启后的情况
                    // 从 Token 中提取基本信息，标记需要刷新
                    log.warn("Token valid but Redis cache missing for userId: {}", jwtUtils.getUserId(token));
                }
            } catch (Exception e) {
                log.error("Failed to load login user from Redis", e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void setAuthentication(LoginUser loginUser) {
        List<SimpleGrantedAuthority> authorities = Arrays.stream(loginUser.getRoleKeys())
                .map(roleKey -> new SimpleGrantedAuthority("ROLE_" + roleKey))
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}