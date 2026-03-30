package com.oa.system.domain.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户创建DTO
 */
@Data
public class UserCreateDto {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度应在2-20个字符之间")
    private String userName;

    @NotBlank(message = "工号不能为空")
    private String empNo;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度应在6-20个字符之间")
    private String password;

    private Long deptId;
    private Long postId;
    private String nickName;
    private String userType;
    private String email;
    private String phone;
    private String sex;
    private Long[] roleIds;
}