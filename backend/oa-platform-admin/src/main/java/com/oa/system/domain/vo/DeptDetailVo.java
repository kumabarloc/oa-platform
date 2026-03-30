package com.oa.system.domain.vo;

import lombok.Data;

/**
 * 部门详情VO
 */
@Data
public class DeptDetailVo {
    private Long id;
    private Long parentId;
    private String ancestors;
    private String deptName;
    private String deptCode;
    private String deptType;
    private Integer sortOrder;
    private Long leader;
    private String phone;
    private String email;
    private Integer status;
    private String parentName;
}