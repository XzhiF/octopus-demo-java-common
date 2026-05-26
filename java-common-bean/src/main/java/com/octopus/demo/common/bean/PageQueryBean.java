package com.octopus.demo.common.bean;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Pagination query parameters with multi-field sorting support.
 */
public class PageQueryBean {

    private static final int MAX_SIZE = 100;

    private int page = 1;
    private int size = 20;
    private LinkedHashMap<String, String> sort;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        if (page < 1) {
            throw new IllegalArgumentException("page must be >= 1");
        }
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size < 1 || size > MAX_SIZE) {
            throw new IllegalArgumentException("size must be between 1 and " + MAX_SIZE);
        }
        this.size = size;
    }

    public Map<String, String> getSort() {
        if (sort == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(sort);
    }

    public void setSort(Map<String, String> sort) {
        if (sort == null) {
            this.sort = null;
        } else {
            this.sort = new LinkedHashMap<>(sort);
        }
    }
}