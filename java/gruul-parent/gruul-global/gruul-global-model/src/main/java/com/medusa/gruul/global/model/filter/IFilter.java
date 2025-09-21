package com.medusa.gruul.global.model.filter;


/**
 * 处理节点 处理流程  责任链模式抽象
 *
 * @param <Context> 上下文
 * @author 张治保
 * date 2022/8/15
 */
@FunctionalInterface
public interface IFilter<Context> {

    /**
     * 处理上下文逻辑抽象
     *
     * @param context 上下文
     * @param chain   处理链
     */
    void doFilter(FilterContext<Context> context, IFilterChain<Context> chain);
}
