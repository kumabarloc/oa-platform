package com.oa.system.domain.vo;

import lombok.Data;

/**
 * 角色列表VO
 */
@Data
public class RoleListVo {
    private Long id;
    private String roleName;
    private String roleKey;
    private Integer roleSort;
    private Integer status;
}