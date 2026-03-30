package com.oa.system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oa.common.core.exception.BusinessException;
import com.oa.common.core.page.PageResult;
import com.oa.common.core.result.ResultCode;
import com.oa.system.domain.dto.DeptCreateDto;
import com.oa.system.domain.dto.DeptUpdateDto;
import com.oa.system.domain.vo.DeptDetailVo;
import com.oa.system.domain.vo.DeptTreeVo;
import com.oa.system.entity.SysDept;
import com.oa.system.mapper.SysDeptMapper;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务
 */
@Service
public class DeptService {

    private final SysDeptMapper deptMapper;

    public DeptService(SysDeptMapper deptMapper) {
        this.deptMapper = deptMapper;
    }

    /**
     * 获取部门树
     */
    public List<DeptTreeVo> getDeptTree() {
        List<SysDept> deptList = deptMapper.selectDeptTree();
        return buildDeptTree(deptList, 0L);
    }

    /**
     * 获取部门详情
     */
    public DeptDetailVo getDeptDetail(Long id) {
        SysDept dept = deptMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return toDeptDetailVo(dept);
    }

    /**
     * 创建部门
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createDept(DeptCreateDto dto) {
        SysDept dept = new SysDept();
        dept.setParentId(dto.getParentId());
        // 处理 ancestors
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            SysDept parent = deptMapper.selectById(dto.getParentId());
            if (parent != null) {
                dept.setAncestors(parent.getAncestors() + "," + dto.getParentId());
            }
        } else {
            dept.setAncestors("0");
        }
        dept.setDeptName(dto.getDeptName());
        dept.setDeptCode(dto.getDeptCode());
        dept.setDeptType(dto.getDeptType());
        dept.setSortOrder(dto.getSortOrder());
        dept.setLeader(dto.getLeader());
        dept.setPhone(dto.getPhone());
        dept.setEmail(dto.getEmail());
        dept.setStatus(0);
        deptMapper.insert(dept);
        return dept.getId();
    }

    /**
     * 更新部门
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(Long id, DeptUpdateDto dto) {
        SysDept dept = deptMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 如果修改了父部门，需要更新子部门的 ancestors
        if (!dto.getParentId().equals(dept.getParentId())) {
            String newAncestors;
            if (dto.getParentId() != null && dto.getParentId() > 0) {
                SysDept newParent = deptMapper.selectById(dto.getParentId());
                newAncestors = newParent.getAncestors() + "," + dto.getParentId();
            } else {
                newAncestors = "0";
            }
            // 更新所有子部门的 ancestors
            updateChildrenAncestors(id, newAncestors + "," + id);
        }

        dept.setParentId(dto.getParentId());
        dept.setDeptName(dto.getDeptName());
        dept.setDeptCode(dto.getDeptCode());
        dept.setDeptType(dto.getDeptType());
        dept.setSortOrder(dto.getSortOrder());
        dept.setLeader(dto.getLeader());
        dept.setPhone(dto.getPhone());
        dept.setEmail(dto.getEmail());
        dept.setStatus(dto.getStatus());
        deptMapper.updateById(dept);
    }

    /**
     * 删除部门
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDept(Long id) {
        // 检查是否有子部门
        List<SysDept> children = deptMapper.selectChildrenDeptById(id);
        if (!children.isEmpty()) {
            throw new BusinessException(ResultCode.DATA_CONSTRAINT, "请先删除子部门");
        }
        // 检查是否有用户
        int userCount = deptMapper.countUserByDeptId(id);
        if (userCount > 0) {
            throw new BusinessException(ResultCode.DATA_CONSTRAINT, "该部门下有用户，无法删除");
        }
        deptMapper.deleteById(id);
    }

    /**
     * 递归构建部门树
     */
    private List<DeptTreeVo> buildDeptTree(List<SysDept> deptList, Long parentId) {
        return deptList.stream()
                .filter(dept -> dept.getParentId().equals(parentId))
                .map(dept -> {
                    DeptTreeVo vo = new DeptTreeVo();
                    vo.setId(dept.getId());
                    vo.setParentId(dept.getParentId());
                    vo.setDeptName(dept.getDeptName());
                    vo.setDeptCode(dept.getDeptCode());
                    vo.setDeptType(dept.getDeptType());
                    vo.setSortOrder(dept.getSortOrder());
                    vo.setLeader(dept.getLeader());
                    vo.setPhone(dept.getPhone());
                    vo.setEmail(dept.getEmail());
                    vo.setStatus(dept.getStatus());
                    vo.setParentName(dept.getParentName());
                    // 递归子部门
                    List<DeptTreeVo> children = buildDeptTree(deptList, dept.getId());
                    vo.setChildren(children.isEmpty() ? null : children);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 更新子部门的 ancestors
     */
    private void updateChildrenAncestors(Long parentId, String newAncestors) {
        List<SysDept> children = deptMapper.selectChildrenDeptById(parentId);
        for (SysDept child : children) {
            child.setAncestors(newAncestors);
            deptMapper.updateById(child);
            updateChildrenAncestors(child.getId(), newAncestors);
        }
    }

    private DeptDetailVo toDeptDetailVo(SysDept dept) {
        DeptDetailVo vo = new DeptDetailVo();
        vo.setId(dept.getId());
        vo.setParentId(dept.getParentId());
        vo.setAncestors(dept.getAncestors());
        vo.setDeptName(dept.getDeptName());
        vo.setDeptCode(dept.getDeptCode());
        vo.setDeptType(dept.getDeptType());
        vo.setSortOrder(dept.getSortOrder());
        vo.setLeader(dept.getLeader());
        vo.setPhone(dept.getPhone());
        vo.setEmail(dept.getEmail());
        vo.setStatus(dept.getStatus());
        vo.setParentName(dept.getParentName());
        return vo;
    }
}