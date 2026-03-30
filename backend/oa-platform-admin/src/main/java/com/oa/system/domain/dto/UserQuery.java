package com.oa.system.domain.dto;

import lombok.Data;

/**
 * 用户查询DTO
 */
@Data
public class UserQuery {
    /** 页码 */
    private Integer pageNum = 1;
    /** 每页数量 */
    private Integer pageSize = 10;
    /** 用户名 */
    private String userName;
    /** 手机号 */
    private String phone;
    /** 部门ID */
    private Long deptId;
    /** 用户状态 */
    private Integer status;
}