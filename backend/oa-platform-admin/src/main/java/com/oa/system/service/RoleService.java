package com.oa.system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oa.common.core.exception.BusinessException;
import com.oa.common.core.page.PageResult;
import com.oa.common.core.result.ResultCode;
import com.oa.system.domain.dto.RoleCreateDto;
import com.oa.system.domain.dto.RoleQuery;
import com.oa.system.domain.dto.RoleUpdateDto;
import com.oa.system.domain.vo.RoleDetailVo;
import com.oa.system.domain.vo.RoleListVo;
import com.oa.system.entity.SysRole;
import com.oa.system.mapper.SysMenuMapper;
import com.oa.system.mapper.SysRoleMapper;
import com.oa.system.mapper.SysUserRoleMapper;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务
 */
@Service
public class RoleService {

    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysMenuMapper menuMapper;

    public RoleService(SysRoleMapper roleMapper, SysUserRoleMapper userRoleMapper, SysMenuMapper menuMapper) {
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.menuMapper = menuMapper;
    }

    /**
     * 获取角色分页列表
     */
    public PageResult<RoleListVo> getRoleList(RoleQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<SysRole> list = roleMapper.selectList(null); // TODO: 添加查询条件
        PageInfo<SysRole> pageInfo = new PageInfo<>(list);
        
        List<RoleListVo> voList = new ArrayList<>();
        for (SysRole role : list) {
            voList.add(toRoleListVo(role));
        }
        return PageResult.of((long)pageInfo.getTotal(), voList, (long)pageInfo.getPageNum(), (long)pageInfo.getPageSize());
    }

    /**
     * 获取角色详情
     */
    public RoleDetailVo getRoleDetail(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        RoleDetailVo vo = toRoleDetailVo(role);
        // 获取角色菜单
        vo.setMenuIds(roleMapper.selectMenuIdsByRoleId(id).toArray(new Long[0]));
        return vo;
    }

    /**
     * 获取角色选项列表
     */
    public List<RoleListVo> getRoleOptions() {
        List<SysRole> list = roleMapper.selectList(null);
        List<RoleListVo> voList = new ArrayList<>();
        for (SysRole role : list) {
            RoleListVo vo = new RoleListVo();
            vo.setId(role.getId());
            vo.setRoleName(role.getRoleName());
            vo.setRoleKey(role.getRoleKey());
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 创建角色
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleCreateDto dto) {
        SysRole role = new SysRole();
        role.setRoleName(dto.getRoleName());
        role.setRoleKey(dto.getRoleKey());
        role.setRoleSort(dto.getRoleSort());
        role.setDataScope(dto.getDataScope());
        role.setMenuCheckStrictly(dto.getMenuCheckStrictly());
        role.setDeptCheckStrictly(dto.getDeptCheckStrictly());
        role.setStatus(0);
        roleMapper.insert(role);
        return role.getId();
    }

    /**
     * 更新角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long id, RoleUpdateDto dto) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        role.setRoleName(dto.getRoleName());
        role.setRoleKey(dto.getRoleKey());
        role.setRoleSort(dto.getRoleSort());
        role.setDataScope(dto.getDataScope());
        role.setMenuCheckStrictly(dto.getMenuCheckStrictly());
        role.setDeptCheckStrictly(dto.getDeptCheckStrictly());
        role.setStatus(dto.getStatus());
        roleMapper.updateById(role);
    }

    /**
     * 删除角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        // 检查是否有用户关联
        List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(id);
        if (!userIds.isEmpty()) {
            throw new BusinessException(ResultCode.DATA_CONSTRAINT, "该角色下有用户，无法删除");
        }
        roleMapper.deleteById(id);
    }

    /**
     * 授权菜单
     */
    @Transactional(rollbackFor = Exception.class)
    public void grantMenu(Long roleId, Long[] menuIds) {
        roleMapper.deleteMenuByRoleId(roleId);
        if (menuIds != null && menuIds.length > 0) {
            roleMapper.insertMenuRole(roleId, menuIds);
        }
    }

    /**
     * 获取角色菜单ID列表
     */
    public List<Long> getRoleMenus(Long roleId) {
        return roleMapper.selectMenuIdsByRoleId(roleId);
    }

    private RoleListVo toRoleListVo(SysRole role) {
        RoleListVo vo = new RoleListVo();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleKey(role.getRoleKey());
        vo.setRoleSort(role.getRoleSort());
        vo.setStatus(role.getStatus());
        return vo;
    }

    private RoleDetailVo toRoleDetailVo(SysRole role) {
        RoleDetailVo vo = new RoleDetailVo();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleKey(role.getRoleKey());
        vo.setRoleSort(role.getRoleSort());
        vo.setDataScope(role.getDataScope());
        vo.setMenuCheckStrictly(role.getMenuCheckStrictly());
        vo.setDeptCheckStrictly(role.getDeptCheckStrictly());
        vo.setStatus(role.getStatus());
        return vo;
    }
}