package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.domain.vo.UserListVo;
import com.oa.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    SysUser selectByUserName(@Param("userName") String userName);
    SysUser selectByEmpNo(@Param("empNo") String empNo);
    List<UserListVo> selectUserList(@Param("userName") String userName,
                                    @Param("phone") String phone,
                                    @Param("status") Integer status);
}
