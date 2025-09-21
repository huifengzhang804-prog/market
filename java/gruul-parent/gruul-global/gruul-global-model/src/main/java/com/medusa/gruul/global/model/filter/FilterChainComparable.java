package com.medusa.gruul.global.model.filter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/8/16
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public class FilterChainComparable<Context> implements Comparable<FilterChainComparable<Context>> {
    private final int order;
    @Getter
    private final FilterChain<Context> filterChain;

    @Override
    public int compareTo(FilterChainComparable<Context> next) {
        return order > next.order ? -1 : 1;
    }
}
