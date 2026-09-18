package com.octopus.demo.common.util;

import com.octopus.demo.common.bean.PageResultBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In-memory pagination helper shared across services (spec KD1/KD2).
 *
 * <p>Semantics: {@code page} is 1-based; illegal inputs are clamped
 * ({@code page < 1} to 1, {@code size < 1} to 20, {@code size > 100} to 100);
 * {@code count} is always {@code all.size()}; out-of-range pages return an empty
 * list while count/totalPage stay honest; {@code totalPage = ceil(total / size)}.
 *
 * <p>Zero third-party dependencies — JDK only.
 */
public final class PageUtil {

    private static final long DEFAULT_SIZE = 20;
    private static final long MAX_SIZE = 100;

    private PageUtil() {
    }

    /**
     * Slices {@code all} into a 1-based page of at most {@code size} elements.
     *
     * @param all  full data set; {@code null} is treated as empty
     * @param page 1-based page index; values below 1 are clamped to 1
     * @param size page size; clamped to {@code [1, }{@value #MAX_SIZE}{@code ]},
     *             values below 1 fall back to {@value #DEFAULT_SIZE}
     * @return a {@link PageResultBean} with honest count/totalPage and a defensive-copy
     *         slice of the page (mutating it never writes back to {@code all})
     */
    public static <T> PageResultBean<T> paginate(List<T> all, long page, long size) {
        long effectivePage = page < 1 ? 1 : page;
        long effectiveSize = size < 1 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);

        List<T> source = all == null ? Collections.emptyList() : all;
        long total = source.size();
        long totalPage = (total + effectiveSize - 1) / effectiveSize;

        PageResultBean<T> result = new PageResultBean<>();
        result.setCount(total);
        result.setTotalPage(totalPage);

        long zeroBasedPage = effectivePage - 1;
        if (total > 0 && zeroBasedPage < totalPage) {
            int from = (int) (zeroBasedPage * effectiveSize);
            int to = (int) Math.min((long) from + effectiveSize, total);
            result.setList(new ArrayList<>(source.subList(from, to)));
        } else {
            result.setList(Collections.emptyList());
        }
        return result;
    }
}
