package com.medusa.gruul.global.model.filter;


/**
 * 处理节点抽象 责任链模式
 *
 * @param <Context> 上下文
 * @author 张治保
 * date 2022/8/15
 */
public interface IFilterChain<Context> {


    /**
     * 处理流程
     *
     * @param context 上下文
     */
    void handle(FilterContext<Context> context);


    /**
     * 执行任务交给下一个链
     *
     * @param context 上下文
     */
    void chain(FilterContext<Context> context);

}
