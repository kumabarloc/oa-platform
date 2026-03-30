package com.oa.system.domain.vo;

import lombok.Data;
import java.util.List;

/**
 * 部门树VO
 */
@Data
public class DeptTreeVo {
    private Long id;
    private Long parentId;
    private String deptName;
    private String deptCode;
    private String deptType;
    private Integer sortOrder;
    private Long leader;
    private String phone;
    private String email;
    private Integer status;
    private String parentName;
    private List<DeptTreeVo> children;
}