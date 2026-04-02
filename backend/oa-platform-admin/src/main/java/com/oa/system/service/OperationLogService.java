package com.oa.system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oa.common.core.page.PageResult;
import com.oa.system.entity.SysOperationLog;
import com.oa.system.mapper.SysOperationLogMapper;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class OperationLogService {
    private final SysOperationLogMapper mapper;
    public OperationLogService(SysOperationLogMapper mapper) { this.mapper = mapper; }
    public PageResult<Map<String, Object>> getList(int pageNum, int pageSize, String title, String operName) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysOperationLog> list = mapper.selectList(null);
        if (title != null && !title.isEmpty()) list.removeIf(l -> l.getTitle() == null || !l.getTitle().contains(title));
        if (operName != null && !operName.isEmpty()) list.removeIf(l -> l.getOperName() == null || !l.getOperName().contains(operName));
        PageInfo<SysOperationLog> pageInfo = new PageInfo<>(list);
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysOperationLog l : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", l.getId()); m.put("title", l.getTitle()); m.put("businessType", l.getBusinessType());
            m.put("method", l.getMethod()); m.put("requestMethod", l.getRequestMethod());
            m.put("operName", l.getOperName()); m.put("deptName", l.getDeptName());
            m.put("url", l.getUrl()); m.put("ip", l.getIp()); m.put("location", l.getLocation());
            m.put("status", l.getStatus()); m.put("operTime", l.getOperTime());
            m.put("errorMsg", l.getErrorMsg());
            result.add(m);
        }
        return PageResult.of(pageInfo.getTotal(), result, (long)pageNum, (long)pageSize);
    }
}
