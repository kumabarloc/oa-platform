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
    private String deptName;
    private Long postId;
    private String postName;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
}
