package com.oa.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.workflow.entity.SysVehicle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用车申请 Mapper
 */
@Mapper
public interface VehicleMapper extends BaseMapper<SysVehicle> {

    List<SysVehicle> selectVehiclePage(@Param("status") String status, @Param("userId") Long userId);
}
