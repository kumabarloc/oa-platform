package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门 Mapper
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 查询所有部门（树形）
     */
    List<SysDept> selectDeptTree();

    /**
     * 查询子部门列表
     */
    List<SysDept> selectChildrenDeptById(@Param("parentId") Long parentId);

    /**
     * 查询部门下用户数量
     */
    int countUserByDeptId(@Param("deptId") Long deptId);
}