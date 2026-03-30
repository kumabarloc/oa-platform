package com.oa.system.domain.vo;

import lombok.Data;

/**
 * 用户列表VO
 */
@Data
public class UserListVo {
    private Long id;
    private String userName;
    private String nickName;
    private String empNo;
    private Long deptId;
    private Integer status;
}