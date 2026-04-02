package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.SysIncomingDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;

@Mapper
public interface SysIncomingDocumentMapper extends BaseMapper<SysIncomingDocument> {
    String selectNextDocNumber(@Param("date") String date);
}
