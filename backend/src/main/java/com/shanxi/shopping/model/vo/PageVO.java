package com.shanxi.shopping.model.vo;

import java.util.List;

/**
 * 分页响应 VO
 */
public class PageVO<T> {
    private List<T> list;
    private long total;
    private int page;
    private int pageSize;
    private int totalPages;

    public PageVO(List<T> list, long total, int page, int pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
    }

    public List<T> getList() { return list; }
    public long getTotal() { return total; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public int getTotalPages() { return totalPages; }
}
