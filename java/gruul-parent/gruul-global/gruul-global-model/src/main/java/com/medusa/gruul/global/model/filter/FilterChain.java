package com.medusa.gruul.global.model.filter;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author 张治保
 * date 2022/8/15
 */
@RequiredArgsConstructor
public class FilterChain<Context> implements IFilterChain<Context> {

    /**
     * 当前节点的处理
     */
    private final IFilter<Context> filter;

    /**
     * 下一个节点
     */
    @Setter
    private IFilterChain<Context> next;

    @Override
    public void handle(FilterContext<Context> context) {
        if (filter == null) {
            return;
        }
        filter.doFilter(context, this);
    }

    @Override
    public void chain(FilterContext<Context> context) {
        if (next == null || context.isBreakIt()) {
            return;
        }
        next.handle(context);
    }
}
