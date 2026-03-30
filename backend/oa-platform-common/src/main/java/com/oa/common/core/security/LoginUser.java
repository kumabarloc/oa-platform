package com.oa.common.core.security;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录用户信息
 */
@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String userName;

    /** 昵称 */
    private String nickName;

    /** 工号 */
    private String empNo;

    /** 部门ID */
    private Long deptId;

    /** 部门名称 */
    private String deptName;

    /** 岗位ID */
    private Long postId;

    /** 岗位名称 */
    private String postName;

    /** 用户类型 */
    private String userType;

    /** 头像 */
    private String avatar;

    /** 角色ID列表 */
    private Long[] roleIds;

    /** 角色标识列表 */
    private String[] roleKeys;

    /** 登录时间 */
    private LocalDateTime loginTime;

    /** 登录IP */
    private String loginIp;

    /** 登录地址 */
    private String loginAddress;

    /** Token */
    private String token;
}