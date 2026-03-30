package com.oa.common.core.page;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装
 */
@Data
@Accessors(chain = true)
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private Long total;

    /** 数据列表 */
    private List<T> list;

    /** 当前页码 */
    private Long pageNum;

    /** 每页记录数 */
    private Long pageSize;

    /** 总页数 */
    private Long pages;

    public PageResult() {
    }

    public PageResult(Long total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public PageResult(Long total, List<T> list, Long pageNum, Long pageSize) {
        this.total = total;
        this.list = list;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        if (pageSize != null && pageSize > 0) {
            this.pages = (total + pageSize - 1) / pageSize;
        }
    }

    public static <T> PageResult<T> of(Long total, List<T> list) {
        return new PageResult<>(total, list);
    }

    public static <T> PageResult<T> of(Long total, List<T> list, Long pageNum, Long pageSize) {
        return new PageResult<>(total, list, pageNum, pageSize);
    }
}