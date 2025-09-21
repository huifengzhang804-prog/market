package com.medusa.gruul.global.model.filter;

import io.vavr.control.Option;

/**
 * 责任链
 *
 * @author 张治保
 * date 2022/8/15
 */
public interface IFilterPipeline<Context> {

    /**
     * 构造pipeline
     *
     * @param dataOption 可选上下文
     * @param <Context>  上下文类型
     * @return FilterPipeline
     */
    static <Context> IFilterPipeline<Context> build(Option<Context> dataOption) {
        FilterContext<Context> filterContext = new FilterContext<>();
        filterContext.setData(dataOption.getOrNull());
        filterContext.setBreakIt(Boolean.FALSE);
        return new FilterPipeline<>(filterContext);
    }

    /**
     * 增加filter处理器
     *
     * @param filter 处理器 任意位置
     * @return self
     */
    IFilterPipeline<Context> addFilter(IFilter<Context> filter);


    /**
     * 增加filter处理器
     *
     * @param order  顺序 从小到大 排序 挨个执行
     * @param filter 处理器
     * @return self
     */
    IFilterPipeline<Context> addFilter(int order, IFilter<Context> filter);

    /**
     * 增加filter处理器
     *
     * @param filter 处理器 任意位置
     * @return self
     */
    IFilterPipeline<Context> addFilter(IAutomaticFilter<Context> filter);


    /**
     * 增加filter处理器
     *
     * @param order  顺序 从小到大 排序 挨个执行
     * @param filter 处理器
     * @return self
     */
    IFilterPipeline<Context> addFilter(int order, IAutomaticFilter<Context> filter);

    /**
     * 增加filter处理器
     *
     * @param filter 处理器 任意位置
     * @return self
     */
    IFilterPipeline<Context> addFilterChain(FilterChain<Context> filter);


    /**
     * 增加filter处理器
     *
     * @param order  顺序 从小到大 排序 挨个执行
     * @param filter 处理器
     * @return self
     */
    IFilterPipeline<Context> addFilterChain(int order, FilterChain<Context> filter);

    /**
     * 开始执行
     */
    void flush();


}
