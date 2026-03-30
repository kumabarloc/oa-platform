package com.oa.system.service;

import com.oa.common.core.exception.BusinessException;
import com.oa.common.core.result.ResultCode;
import com.oa.common.core.security.SecurityUtils;
import com.oa.system.domain.vo.MenuDetailVo;
import com.oa.system.domain.vo.MenuTreeVo;
import com.oa.system.entity.SysMenu;
import com.oa.system.mapper.SysMenuMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务
 */
@Service
public class MenuService {

    private final SysMenuMapper menuMapper;

    public MenuService(SysMenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    /**
     * 获取菜单树
     */
    public List<MenuTreeVo> getMenuTree() {
        List<SysMenu> menuList = menuMapper.selectMenuTree();
        return buildMenuTree(menuList, 0L);
    }

    /**
     * 获取菜单详情
     */
    public MenuDetailVo getMenuDetail(Long id) {
        SysMenu menu = menuMapper.selectMenuById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return toMenuDetailVo(menu);
    }

    /**
     * 获取当前用户菜单
     */
    public List<MenuTreeVo> getUserMenus() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return new ArrayList<>();
        }
        List<SysMenu> menuList = menuMapper.selectMenusByUserId(userId);
        return buildMenuTree(menuList, 0L);
    }

    /**
     * 获取角色菜单
     */
    public List<MenuTreeVo> getRoleMenus(Long roleId) {
        List<SysMenu> menuList = menuMapper.selectMenusByRoleId(roleId);
        return buildMenuTree(menuList, 0L);
    }

    /**
     * 递归构建菜单树
     */
    private List<MenuTreeVo> buildMenuTree(List<SysMenu> menuList, Long parentId) {
        return menuList.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {
                    MenuTreeVo vo = new MenuTreeVo();
                    vo.setId(menu.getId());
                    vo.setMenuName(menu.getMenuName());
                    vo.setParentId(menu.getParentId());
                    vo.setOrderNum(menu.getOrderNum());
                    vo.setPath(menu.getPath());
                    vo.setComponent(menu.getComponent());
                    vo.setQuery(menu.getQuery());
                    vo.setIsFrame(menu.getIsFrame());
                    vo.setIsCache(menu.getIsCache());
                    vo.setMenuType(menu.getMenuType());
                    vo.setVisible(menu.getVisible());
                    vo.setStatus(menu.getStatus());
                    vo.setPerms(menu.getPerms());
                    vo.setIcon(menu.getIcon());
                    // 递归子菜单
                    List<MenuTreeVo> children = buildMenuTree(menuList, menu.getId());
                    vo.setChildren(children.isEmpty() ? null : children);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private MenuDetailVo toMenuDetailVo(SysMenu menu) {
        MenuDetailVo vo = new MenuDetailVo();
        vo.setId(menu.getId());
        vo.setMenuName(menu.getMenuName());
        vo.setParentId(menu.getParentId());
        vo.setOrderNum(menu.getOrderNum());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setQuery(menu.getQuery());
        vo.setIsFrame(menu.getIsFrame());
        vo.setIsCache(menu.getIsCache());
        vo.setMenuType(menu.getMenuType());
        vo.setVisible(menu.getVisible());
        vo.setStatus(menu.getStatus());
        vo.setPerms(menu.getPerms());
        vo.setIcon(menu.getIcon());
        return vo;
    }
}