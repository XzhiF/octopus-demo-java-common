package com.octopus.demo.common.bean;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageResultBeanTest {

    @Test
    void constructor_andGetter_workCorrectly() {
        List<String> items = new ArrayList<>();
        items.add("a");
        items.add("b");

        PageResultBean<String> bean = new PageResultBean<>();
        bean.setCount(100);
        bean.setList(items);

        assertEquals(100, bean.getCount());
        assertEquals(2, bean.getList().size());
        assertEquals("a", bean.getList().get(0));
        assertEquals("b", bean.getList().get(1));
    }

    @Test
    void emptyResult_hasZeroCountAndEmptyList() {
        PageResultBean<String> bean = new PageResultBean<>();
        bean.setCount(0);
        bean.setList(new ArrayList<>());

        assertEquals(0, bean.getCount());
        assertTrue(bean.getList().isEmpty());
    }

    @Test
    void count_supportsLargeValues() {
        PageResultBean<String> bean = new PageResultBean<>();
        bean.setCount(Long.MAX_VALUE);

        assertEquals(Long.MAX_VALUE, bean.getCount());
    }

    @Test
    void defaultList_isEmptyNotNull() {
        PageResultBean<String> bean = new PageResultBean<>();
        assertNotNull(bean.getList());
        assertTrue(bean.getList().isEmpty());
    }

    @Test
    void setList_withNull_defaultsToEmptyList() {
        PageResultBean<String> bean = new PageResultBean<>();
        bean.setList(null);

        assertNotNull(bean.getList());
        assertTrue(bean.getList().isEmpty());
    }

    @Test
    void setList_makesDefensiveCopy() {
        List<String> original = new ArrayList<>();
        original.add("a");

        PageResultBean<String> bean = new PageResultBean<>();
        bean.setList(original);

        original.add("b");
        assertEquals(1, bean.getList().size());
    }
}