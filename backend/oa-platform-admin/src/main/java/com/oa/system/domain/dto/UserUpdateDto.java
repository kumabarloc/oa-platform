package com.oa.system.domain.dto;

import lombok.Data;

/**
 * 用户更新DTO
 */
@Data
public class UserUpdateDto {
    private Long deptId;
    private Long postId;
    private String empNo;
    private String nickName;
    private String userType;
    private String email;
    private String phone;
    private String sex;
    private Integer status;
    private Long[] roleIds;
}