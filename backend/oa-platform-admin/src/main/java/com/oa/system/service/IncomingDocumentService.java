package com.oa.system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oa.common.core.exception.BusinessException;
import com.oa.common.core.page.PageResult;
import com.oa.common.core.result.ResultCode;
import com.oa.common.core.security.SecurityUtils;
import com.oa.system.entity.SysIncomingDocument;
import com.oa.system.mapper.SysIncomingDocumentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class IncomingDocumentService {

    private final SysIncomingDocumentMapper mapper;

    public IncomingDocumentService(SysIncomingDocumentMapper mapper) {
        this.mapper = mapper;
    }

    public PageResult<Map<String, Object>> getList(int pageNum, int pageSize, String status, String startDate, String endDate) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysIncomingDocument> list = mapper.selectList(null);
        // Filter in memory
        if (status != null && !status.isEmpty()) {
            list.removeIf(d -> !status.equals(d.getStatus()));
        }
        if (startDate != null && !startDate.isEmpty()) {
            LocalDate sd = LocalDate.parse(startDate);
            list.removeIf(d -> d.getReceiveTime() != null && d.getReceiveTime().toLocalDate().isBefore(sd));
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDate ed = LocalDate.parse(endDate);
            list.removeIf(d -> d.getReceiveTime() != null && d.getReceiveTime().toLocalDate().isAfter(ed));
        }
        PageInfo<SysIncomingDocument> pageInfo = new PageInfo<>(list);
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysIncomingDocument d : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", d.getId());
            m.put("docNumber", d.getDocNumber());
            m.put("sourceUnit", d.getSourceUnit());
            m.put("receiveTime", d.getReceiveTime());
            m.put("title", d.getTitle());
            m.put("status", d.getStatus());
            m.put("priority", d.getPriority());
            m.put("currentHandlerId", d.getCurrentHandlerId());
            m.put("createTime", d.getCreateTime());
            result.add(m);
        }
        return PageResult.of(pageInfo.getTotal(), result, (long)pageNum, (long)pageSize);
    }

    public Map<String, Object> getDetail(Long id) {
        SysIncomingDocument d = mapper.selectById(id);
        if (d == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        Map<String, Object> m = new HashMap<>();
        m.put("id", d.getId());
        m.put("docNumber", d.getDocNumber());
        m.put("sourceUnit", d.getSourceUnit());
        m.put("receiveTime", d.getReceiveTime());
        m.put("title", d.getTitle());
        m.put("content", d.getContent());
        m.put("attachUrls", d.getAttachUrls());
        m.put("status", d.getStatus());
        m.put("priority", d.getPriority());
        m.put("currentHandlerId", d.getCurrentHandlerId());
        m.put("draftUserId", d.getDraftUserId());
        m.put("createBy", d.getCreateBy());
        m.put("createDept", d.getCreateDept());
        m.put("createTime", d.getCreateTime());
        m.put("updateTime", d.getUpdateTime());
        return m;
    }

    @Transactional
    public Long create(Map<String, Object> params) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String docNumber = mapper.selectNextDocNumber(today);
        if (docNumber == null) docNumber = "SW-" + today + "-001";
        else {
            String num = docNumber.replace("SW-" + today + "-", "");
            int n = Integer.parseInt(num) + 1;
            docNumber = "SW-" + today + "-" + String.format("%03d", n);
        }

        SysIncomingDocument d = new SysIncomingDocument();
        d.setDocNumber(docNumber);
        d.setSourceUnit((String) params.get("sourceUnit"));
        d.setReceiveTime(LocalDateTime.now());
        d.setTitle((String) params.get("title"));
        d.setContent((String) params.get("content"));
        d.setAttachUrls((String) params.get("attachUrls"));
        d.setStatus("received");
        d.setPriority((String) params.getOrDefault("priority", "normal"));
        d.setDraftUserId(SecurityUtils.getUserId());
        d.setCreateBy(SecurityUtils.getUserId());
        d.setCreateDept(SecurityUtils.getDeptId() != null ? SecurityUtils.getDeptId() : 100L);
        d.setDelFlag("0");
        mapper.insert(d);
        return d.getId();
    }

    @Transactional
    public void update(Long id, Map<String, Object> params) {
        SysIncomingDocument d = mapper.selectById(id);
        if (d == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        if (params.get("sourceUnit") != null) d.setSourceUnit((String) params.get("sourceUnit"));
        if (params.get("title") != null) d.setTitle((String) params.get("title"));
        if (params.get("content") != null) d.setContent((String) params.get("content"));
        if (params.get("attachUrls") != null) d.setAttachUrls((String) params.get("attachUrls"));
        if (params.get("priority") != null) d.setPriority((String) params.get("priority"));
        if (params.get("status") != null) d.setStatus((String) params.get("status"));
        if (params.get("currentHandlerId") != null) d.setCurrentHandlerId(((Number) params.get("currentHandlerId")).longValue());
        mapper.updateById(d);
    }

    @Transactional
    public void delete(Long id) { mapper.deleteById(id); }

    @Transactional
    public void distribute(Long id) {
        SysIncomingDocument d = mapper.selectById(id);
        if (d == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        d.setStatus("distributing");
        mapper.updateById(d);
    }

    @Transactional
    public void archive(Long id) {
        SysIncomingDocument d = mapper.selectById(id);
        if (d == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        d.setStatus("archived");
        mapper.updateById(d);
    }
}
