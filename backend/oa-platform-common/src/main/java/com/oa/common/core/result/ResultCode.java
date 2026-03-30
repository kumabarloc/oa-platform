package com.oa.common.core.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    
    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请登录"),
    FORBIDDEN(403, "拒绝访问，没有权限"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    
    // 业务错误 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),
    PASSWORD_ERROR(1003, "密码错误"),
    PASSWORD_EXPIRED(1004, "密码已过期"),
    ACCOUNT_LOCKED(1005, "账户已被锁定"),
    TOKEN_EXPIRED(1006, "Token已过期"),
    TOKEN_INVALID(1007, "Token无效"),
    PERMISSION_DENIED(1008, "没有访问权限"),
    
    // 验证码错误
    CAPTCHA_ERROR(1101, "验证码错误"),
    CAPTCHA_EXPIRED(1102, "验证码已过期"),
    
    // 业务操作错误 2xxx
    DATA_NOT_FOUND(2001, "数据不存在"),
    DATA_CONFLICT(2002, "数据冲突"),
    DATA_CONSTRAINT(2003, "数据约束违反"),
    
    // 流程错误 3xxx
    PROCESS_NOT_FOUND(3001, "流程不存在"),
    PROCESS_INSTANCE_NOT_FOUND(3002, "流程实例不存在"),
    TASK_NOT_FOUND(3003, "任务不存在"),
    TASK_ASSIGNED(3004, "任务已被分配"),
    
    // 系统错误 5xxx
    SYSTEM_ERROR(5001, "系统内部错误"),
    SERVICE_UNAVAILABLE(5002, "服务暂不可用"),
    DATABASE_ERROR(5003, "数据库错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}