package com.oa.system.domain.vo;

import lombok.Data;

/**
 * 用户详情VO
 */
@Data
public class UserDetailVo {
    private Long id;
    private String userName;
    private String nickName;
    private String empNo;
    private Long deptId;
    private Long postId;
    private String userType;
    private String email;
    private String phone;
    private String sex;
    private Integer status;
    private String loginIp;
    private String loginDate;
    private Long[] roleIds;
}