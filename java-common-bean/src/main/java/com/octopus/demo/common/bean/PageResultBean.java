package com.octopus.demo.common.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pagination result wrapper carrying total count and current page data.
 */
public class PageResultBean<T> {

    private long count;
    private List<T> list = Collections.emptyList();

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list == null ? Collections.emptyList() : new ArrayList<>(list);
    }
}