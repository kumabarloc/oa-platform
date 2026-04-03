package com.oa.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.workflow.entity.SysVehicleHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用车申请审批历史 Mapper
 */
@Mapper
public interface VehicleHistoryMapper extends BaseMapper<SysVehicleHistory> {

    List<SysVehicleHistory> selectHistoryByVehicleId(@Param("vehicleId") Long vehicleId);
}
