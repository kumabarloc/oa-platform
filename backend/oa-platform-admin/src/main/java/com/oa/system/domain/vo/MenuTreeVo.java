package com.oa.system.domain.vo;

import lombok.Data;
import java.util.List;

/**
 * 菜单树VO
 */
@Data
public class MenuTreeVo {
    private Long id;
    private String menuName;
    private Long parentId;
    private Integer orderNum;
    private String path;
    private String component;
    private String query;
    private Integer isFrame;
    private Integer isCache;
    private String menuType;
    private Integer visible;
    private Integer status;
    private String perms;
    private String icon;
    private List<MenuTreeVo> children;
}