package com.octopus.demo.common.util;

import com.octopus.demo.common.bean.PageResultBean;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link PageUtil#paginate}, covering the KD2 decision points:
 * normal page / first page / out-of-range page with honest total / size clamp
 * (lower and upper) / page&lt;1 clamp / totalPage ceil / empty source / exact-full page.
 */
class PageUtilTest {

    private static List<String> data(int n) {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            list.add("item" + i);
        }
        return list;
    }

    // 正常页（中间页）按 [offset, offset+size) 切片
    @Test
    void paginate_middlePage_returnsCorrectSlice() {
        PageResultBean<String> r = PageUtil.paginate(data(12), 2, 5);
        assertEquals(12, r.getCount());
        assertEquals(3, r.getTotalPage());
        assertEquals(List.of("item6", "item7", "item8", "item9", "item10"), r.getList());
    }

    // 首页 offset 为 0
    @Test
    void paginate_firstPage_startsAtOffsetZero() {
        PageResultBean<String> r = PageUtil.paginate(data(12), 1, 5);
        assertEquals(List.of("item1", "item2", "item3", "item4", "item5"), r.getList());
    }

    // 越界页 list 为空，但 total/totalPage 如实
    @Test
    void paginate_pageBeyondEnd_returnsEmptyListWithHonestTotal() {
        PageResultBean<String> r = PageUtil.paginate(data(12), 99, 5);
        assertNotNull(r.getList());
        assertTrue(r.getList().isEmpty());
        assertEquals(12, r.getCount());
        assertEquals(3, r.getTotalPage());
    }

    // size<1 clamp 到 20
    @Test
    void paginate_sizeBelowOne_clampedTo20() {
        PageResultBean<String> r = PageUtil.paginate(data(50), 1, 0);
        assertEquals(20, r.getList().size());
        assertEquals(3, r.getTotalPage());
    }

    // size>100 clamp 到 100
    @Test
    void paginate_sizeAboveMax_clampedTo100() {
        PageResultBean<String> r = PageUtil.paginate(data(150), 1, 500);
        assertEquals(100, r.getList().size());
        assertEquals(2, r.getTotalPage());
    }

    // page<1 clamp 到 1
    @Test
    void paginate_pageBelowOne_clampedToFirstPage() {
        PageResultBean<String> r = PageUtil.paginate(data(12), 0, 5);
        assertEquals(List.of("item1", "item2", "item3", "item4", "item5"), r.getList());
    }

    // totalPage = ceil(total/size)
    @Test
    void paginate_totalPage_usesCeilDivision() {
        PageResultBean<String> r = PageUtil.paginate(data(10), 1, 3);
        assertEquals(4, r.getTotalPage());
    }

    // 空 list：total=0，totalPage=0
    @Test
    void paginate_emptySource_totalZeroAndTotalPageZero() {
        PageResultBean<String> r = PageUtil.paginate(Collections.emptyList(), 1, 10);
        assertEquals(0, r.getCount());
        assertEquals(0, r.getTotalPage());
        assertTrue(r.getList().isEmpty());
    }

    // 恰满页：末页整页返回
    @Test
    void paginate_exactFullLastPage_returnsFullLastPage() {
        PageResultBean<String> r = PageUtil.paginate(data(10), 2, 5);
        assertEquals(2, r.getTotalPage());
        assertEquals(List.of("item6", "item7", "item8", "item9", "item10"), r.getList());
    }

    // null 源按空处理（防御性）
    @Test
    void paginate_nullSource_returnsEmptyResult() {
        PageResultBean<String> r = PageUtil.paginate(null, 1, 10);
        assertEquals(0, r.getCount());
        assertTrue(r.getList().isEmpty());
    }
}
