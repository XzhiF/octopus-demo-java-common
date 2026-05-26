package com.octopus.demo.common.bean;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class PageQueryBeanTest {

    @Test
    void defaultValues_areCorrect() {
        PageQueryBean bean = new PageQueryBean();
        assertEquals(1, bean.getPage());
        assertEquals(20, bean.getSize());
        assertNull(bean.getSort());
    }

    @Test
    void setters_updateValues() {
        PageQueryBean bean = new PageQueryBean();
        bean.setPage(3);
        bean.setSize(50);
        assertEquals(3, bean.getPage());
        assertEquals(50, bean.getSize());
    }

    @Test
    void sort_preservesInsertionOrder() {
        PageQueryBean bean = new PageQueryBean();
        LinkedHashMap<String, String> sort = new LinkedHashMap<>();
        sort.put("name", "ASC");
        sort.put("age", "DESC");
        bean.setSort(sort);

        LinkedHashMap<String, String> result = bean.getSort();
        assertEquals(2, result.size());

        var entries = result.entrySet().iterator();
        assertEquals("name", entries.next().getKey());
        assertEquals("age", entries.next().getKey());
    }

    @Test
    void sort_entries_haveCorrectDirection() {
        PageQueryBean bean = new PageQueryBean();
        LinkedHashMap<String, String> sort = new LinkedHashMap<>();
        sort.put("createTime", "DESC");
        bean.setSort(sort);

        assertEquals("DESC", bean.getSort().get("createTime"));
    }
}