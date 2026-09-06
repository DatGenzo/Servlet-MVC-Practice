package com.thanhdat.servletmvc.models;

import java.util.List;

public class PageResult<T> {

    private final List<T> items;
    private final int page;
    private final int pageSize;
    private final long totalItems;
    private final int totalPages;

    public PageResult(
            List<T> items,
            int page,
            int pageSize,
            long totalItems,
            int totalPages
    ) {
        this.items = List.copyOf(items);
        this.page = page;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }

    public List<T> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isHasPrevious() {
        return page > 1;
    }

    public boolean isHasNext() {
        return page < totalPages;
    }

    public int getPreviousPage() {
        return Math.max(1, page - 1);
    }

    public int getNextPage() {
        return Math.min(totalPages, page + 1);
    }
}
