package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.oa.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /** 用户ID */
    private Long id;

    /** 归属部门ID */
    private Long deptId;

    /** 岗位ID */
    private Long postId;

    /** 工号 */
    private String empNo;

    /** 用户名称 */
    private String userName;

    /** 昵称 */
    private String nickName;

    /** 用户类型（internal编制内/contract编制外） */
    private String userType;

    /** 邮箱 */
    private String email;

    /** 手机号码 */
    private String phone;

    /** 用户性别（0未知 1男 2女） */
    private String sex;

    /** 头像地址 */
    private String avatar;

    /** 密码 */
    private String password;

    /** 用户状态（0正常 1停用） */
    private Integer status;

    /** 最后登录IP */
    private String loginIp;

    /** 最后登录时间 */
    private LocalDateTime loginDate;

    /**
     * 以下字段由 MyBatis-Plus 管理
     * createBy, createTime, updateBy, updateTime, delFlag, version
     */
}