package com.oa.system.domain.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 登录用户信息VO
 */
@Data
public class LoginUserVo {
    private Long userId;
    private String userName;
    private String nickName;
    private String empNo;
    private Long deptId;
    private String deptName;
    private Long[] roleIds;
    private String[] roleNames;
    private String avatar;
    private LocalDateTime loginTime;
}