package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_post")
public class SysPost extends com.oa.common.core.domain.BaseEntity {

    /** 岗位ID */
    private Long id;

    /** 岗位编码 */
    private String postCode;

    /** 岗位名称 */
    private String postName;

    /** 岗位级别 */
    private String postRank;

    /** 显示顺序 */
    private Integer sortOrder;

    /** 岗位状态（0正常 1停用） */
    private Integer status;
}