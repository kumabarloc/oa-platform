package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色 Mapper
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据角色ID查询菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除角色菜单关联
     */
    int deleteMenuByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色菜单关联
     */
    int insertMenuRole(@Param("roleId") Long roleId, @Param("menuIds") Long[] menuIds);
}