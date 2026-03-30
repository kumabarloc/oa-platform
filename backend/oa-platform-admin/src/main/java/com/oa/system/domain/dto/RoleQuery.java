package com.oa.system.domain.dto;

import lombok.Data;

/**
 * 角色查询DTO
 */
@Data
public class RoleQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String roleName;
    private String roleKey;
    private Integer status;
}