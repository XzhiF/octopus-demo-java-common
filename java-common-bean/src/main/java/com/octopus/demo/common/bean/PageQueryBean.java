package com.octopus.demo.common.bean;

import java.util.LinkedHashMap;

/**
 * Pagination query parameters with multi-field sorting support.
 */
public class PageQueryBean {

    private int page = 1;
    private int size = 20;
    private LinkedHashMap<String, String> sort;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public LinkedHashMap<String, String> getSort() {
        return sort;
    }

    public void setSort(LinkedHashMap<String, String> sort) {
        this.sort = sort;
    }
}