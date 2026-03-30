package com.oa.common.core.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private Integer code;

    /** 消息 */
    private String message;

    /** 数据 */
    private T data;

    /** 时间戳 */
    private Long timestamp;

    public R() {
        this.timestamp = System.currentTimeMillis();
    }

    public R(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public R(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> R<T> ok() {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage());
    }

    public static <T> R<T> ok(T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> R<T> ok(String message, T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    public static <T> R<T> fail() {
        return new R<>(ResultCode.FAIL.getCode(), ResultCode.FAIL.getMessage());
    }

    public static <T> R<T> fail(String message) {
        return new R<>(ResultCode.FAIL.getCode(), message);
    }

    public static <T> R<T> fail(Integer code, String message) {
        return new R<>(code, message);
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        return new R<>(resultCode.getCode(), resultCode.getMessage());
    }

    public static <T> R<T> fail(ResultCode resultCode, String message) {
        return new R<>(resultCode.getCode(), message);
    }
}