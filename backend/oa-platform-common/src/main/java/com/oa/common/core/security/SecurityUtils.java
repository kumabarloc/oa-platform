package com.oa.common.core.security;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 安全上下文工具类
 */
public class SecurityUtils {

    private static final String LOGIN_USER_KEY = "login_user";

    /**
     * 获取当前登录用户
     */
    public static LoginUser getLoginUser() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        return (LoginUser) request.getAttribute(LOGIN_USER_KEY);
    }

    /**
     * 设置当前登录用户
     */
    public static void setLoginUser(LoginUser loginUser) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            request.setAttribute(LOGIN_USER_KEY, loginUser);
        }
    }

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getUserName() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUserName() : null;
    }

    /**
     * 获取当前部门ID
     */
    public static Long getDeptId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getDeptId() : null;
    }

    /**
     * 判断是否已登录
     */
    public static boolean isLogin() {
        return getLoginUser() != null;
    }

    /**
     * 判断是否超级管理员
     */
    public static boolean isSuperAdmin() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null || loginUser.getRoleKeys() == null) {
            return false;
        }
        for (String roleKey : loginUser.getRoleKeys()) {
            if ("super_admin".equals(roleKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取请求对象
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取响应对象
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getResponse() : null;
    }

    /**
     * 获取客户端IP
     */
    public static String getClientIp() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}