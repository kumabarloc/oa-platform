package com.oa.system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oa.common.core.page.PageResult;
import com.oa.system.entity.SysLoginLog;
import com.oa.system.mapper.SysLoginLogMapper;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LoginLogService {
    private final SysLoginLogMapper mapper;
    public LoginLogService(SysLoginLogMapper mapper) { this.mapper = mapper; }
    public PageResult<Map<String, Object>> getList(int pageNum, int pageSize, String username, String status) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysLoginLog> list = mapper.selectList(null);
        if (username != null && !username.isEmpty()) list.removeIf(l -> l.getUsername() == null || !l.getUsername().contains(username));
        if (status != null && !status.isEmpty()) list.removeIf(l -> !status.equals(l.getStatus()));
        PageInfo<SysLoginLog> pageInfo = new PageInfo<>(list);
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysLoginLog l : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", l.getId()); m.put("username", l.getUsername()); m.put("ip", l.getIp());
            m.put("location", l.getLocation()); m.put("browser", l.getBrowser()); m.put("os", l.getOs());
            m.put("status", l.getStatus()); m.put("msg", l.getMsg()); m.put("loginTime", l.getLoginTime());
            result.add(m);
        }
        return PageResult.of(pageInfo.getTotal(), result, (long)pageNum, (long)pageSize);
    }
}
