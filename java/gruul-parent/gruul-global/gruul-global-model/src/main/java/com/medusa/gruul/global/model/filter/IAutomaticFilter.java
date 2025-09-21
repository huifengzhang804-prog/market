package com.medusa.gruul.global.model.filter;

/**
 * 自动 chain的filter
 *
 * @author 张治保
 * date 2022/8/22
 */
@FunctionalInterface
public interface IAutomaticFilter<Context> extends IFilter<Context> {
    /**
     * 处理上下文逻辑抽象
     *
     * @param context 上下文
     */
    void doFilter(FilterContext<Context> context);


    /**
     * 处理上下文逻辑抽象
     *
     * @param context 上下文
     * @param chain   处理链
     */
    @Override
    default void doFilter(FilterContext<Context> context, IFilterChain<Context> chain) {
        this.doFilter(context);
        chain.chain(context);
    }
}
