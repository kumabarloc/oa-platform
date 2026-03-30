package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单权限 Mapper
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据用户ID查询菜单权限
     */
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询菜单权限
     */
    List<SysMenu> selectMenusByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询所有菜单（树形）
     */
    List<SysMenu> selectMenuTree();

    /**
     * 查询菜单详情
     */
    SysMenu selectMenuById(@Param("id") Long id);
}