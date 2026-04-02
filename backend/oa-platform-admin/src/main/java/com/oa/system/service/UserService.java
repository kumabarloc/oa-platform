package com.oa.system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oa.common.core.exception.BusinessException;
import com.oa.common.core.page.PageResult;
import com.oa.common.core.result.ResultCode;
import com.oa.common.core.security.SecurityUtils;
import com.oa.system.domain.dto.UserCreateDto;
import com.oa.system.domain.dto.UserQuery;
import com.oa.system.domain.dto.UserUpdateDto;
import com.oa.system.domain.vo.UserDetailVo;
import com.oa.system.domain.vo.UserListVo;
import com.oa.system.entity.SysUser;
import com.oa.system.entity.SysUserRole;
import com.oa.system.mapper.SysUserMapper;
import com.oa.system.mapper.SysUserRoleMapper;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用户服务
 */
@Service
public class UserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(SysUserMapper userMapper, SysUserRoleMapper userRoleMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 获取当前登录用户信息
     */
    public UserDetailVo getCurrentUserInfo() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return toUserDetailVo(user);
    }

    /**
     * 获取用户分页列表
     */
    public PageResult<UserListVo> getUserList(UserQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<SysUser> list = userMapper.selectList(null); // TODO: 添加查询条件
        PageInfo<SysUser> pageInfo = new PageInfo<>(list);
        
        List<UserListVo> voList = new ArrayList<>();
        for (SysUser user : list) {
            voList.add(toUserListVo(user));
        }
        return PageResult.of((long)pageInfo.getTotal(), voList, (long)pageInfo.getPageNum(), (long)pageInfo.getPageSize());
    }

    /**
     * 获取用户详情
     */
    public UserDetailVo getUserDetail(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return toUserDetailVo(user);
    }

    /**
     * 创建用户
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserCreateDto dto) {
        // 检查用户名是否重复
        SysUser existingUser = userMapper.selectByUserName(dto.getUserName());
        if (existingUser != null) {
            throw new BusinessException(ResultCode.DATA_CONFLICT, "用户名已存在");
        }

        SysUser user = new SysUser();
        user.setDeptId(dto.getDeptId());
        user.setPostId(dto.getPostId());
        user.setEmpNo(dto.getEmpNo());
        user.setUserName(dto.getUserName());
        user.setNickName(dto.getNickName());
        user.setUserType(dto.getUserType());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setSex(dto.getSex());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(0);
        userMapper.insert(user);

        // 分配角色
        if (dto.getRoleIds() != null && dto.getRoleIds().length > 0) {
            for (Long roleId : dto.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }

        return user.getId();
    }

    /**
     * 更新用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long id, UserUpdateDto dto) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        user.setDeptId(dto.getDeptId());
        user.setPostId(dto.getPostId());
        user.setEmpNo(dto.getEmpNo());
        user.setNickName(dto.getNickName());
        user.setUserType(dto.getUserType());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setSex(dto.getSex());
        user.setStatus(dto.getStatus());
        userMapper.updateById(user);

        // 更新角色
        userRoleMapper.deleteByUserId(id);
        if (dto.getRoleIds() != null && dto.getRoleIds().length > 0) {
            for (Long roleId : dto.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(id);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
    }

    /**
     * 删除用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
        userRoleMapper.deleteByUserId(id);
    }

    /**
     * 重置用户密码（管理员操作）
     */
    public void resetPassword(Long id, String password) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        user.setPassword(passwordEncoder.encode(password));
        userMapper.updateById(user);
    }

    /**
     * 修改当前用户密码
     */
    public void changePassword(String oldPassword, String newPassword) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        // 校验旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.FAIL, "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    private UserListVo toUserListVo(SysUser user) {
        UserListVo vo = new UserListVo();
        vo.setId(user.getId());
        vo.setUserName(user.getUserName());
        vo.setNickName(user.getNickName());
        vo.setEmpNo(user.getEmpNo());
        vo.setDeptId(user.getDeptId());
        vo.setStatus(user.getStatus());
        return vo;
    }

    private UserDetailVo toUserDetailVo(SysUser user) {
        UserDetailVo vo = new UserDetailVo();
        vo.setId(user.getId());
        vo.setDeptId(user.getDeptId());
        vo.setPostId(user.getPostId());
        vo.setEmpNo(user.getEmpNo());
        vo.setUserName(user.getUserName());
        vo.setNickName(user.getNickName());
        vo.setUserType(user.getUserType());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setSex(user.getSex());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        // 获取角色列表
        vo.setRoleIds(userRoleMapper.selectRoleIdsByUserId(user.getId()).toArray(new Long[0]));
        return vo;
    }
}
