package com.octopus.demo.common.bean;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PageQueryBeanTest {

    @Test
    void defaultValues_areCorrect() {
        PageQueryBean bean = new PageQueryBean();
        assertEquals(1, bean.getPage());
        assertEquals(20, bean.getSize());
        assertTrue(bean.getSort().isEmpty());
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

        Map<String, String> result = bean.getSort();
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

    @Test
    void setPage_rejectsZeroAndNegative() {
        PageQueryBean bean = new PageQueryBean();
        assertThrows(IllegalArgumentException.class, () -> bean.setPage(0));
        assertThrows(IllegalArgumentException.class, () -> bean.setPage(-1));
    }

    @Test
    void setSize_rejectsZeroNegativeAndTooLarge() {
        PageQueryBean bean = new PageQueryBean();
        assertThrows(IllegalArgumentException.class, () -> bean.setSize(0));
        assertThrows(IllegalArgumentException.class, () -> bean.setSize(-1));
        assertThrows(IllegalArgumentException.class, () -> bean.setSize(101));
    }

    @Test
    void getSort_returnsUnmodifiableMap() {
        PageQueryBean bean = new PageQueryBean();
        LinkedHashMap<String, String> sort = new LinkedHashMap<>();
        sort.put("name", "ASC");
        bean.setSort(sort);

        Map<String, String> result = bean.getSort();
        assertThrows(UnsupportedOperationException.class, () -> result.put("extra", "ASC"));
    }

    @Test
    void setSort_makesDefensiveCopy() {
        PageQueryBean bean = new PageQueryBean();
        LinkedHashMap<String, String> sort = new LinkedHashMap<>();
        sort.put("name", "ASC");
        bean.setSort(sort);

        sort.put("extra", "DESC");
        assertEquals(1, bean.getSort().size());
    }
}