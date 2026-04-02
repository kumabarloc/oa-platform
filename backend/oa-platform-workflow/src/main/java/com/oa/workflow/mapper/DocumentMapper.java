package com.oa.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.workflow.entity.SysDocument;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公文 Mapper
 */
@Mapper
public interface DocumentMapper extends BaseMapper<SysDocument> {
}
