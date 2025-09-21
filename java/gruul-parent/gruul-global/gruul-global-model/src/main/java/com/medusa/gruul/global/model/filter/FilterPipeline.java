package com.medusa.gruul.global.model.filter;


import java.util.TreeSet;

/**
 * @author 张治保
 * date 2022/8/15
 */
public class FilterPipeline<Context> implements IFilterPipeline<Context> {

    FilterPipeline(FilterContext<Context> filterContext) {
        this.filterContext = filterContext;
    }

    private final FilterContext<Context> filterContext;
    private final TreeSet<FilterChainComparable<Context>> sortedSet = new TreeSet<>();

    @Override
    public IFilterPipeline<Context> addFilter(IFilter<Context> filter) {
        return this.addFilterChain(new FilterChain<>(filter));
    }

    @Override
    public IFilterPipeline<Context> addFilter(int order, IFilter<Context> filter) {
        return this.addFilterChain(order, new FilterChain<>(filter));
    }

    @Override
    public IFilterPipeline<Context> addFilter(IAutomaticFilter<Context> filter) {
        return this.addFilter((IFilter<Context>) filter);
    }

    @Override
    public IFilterPipeline<Context> addFilter(int order, IAutomaticFilter<Context> filter) {
        return this.addFilter(order, (IFilter<Context>) filter);
    }

    @Override
    public IFilterPipeline<Context> addFilterChain(FilterChain<Context> chain) {
        return this.addFilterChain(0, chain);
    }

    @Override
    public IFilterPipeline<Context> addFilterChain(int order, FilterChain<Context> chain) {
        sortedSet.add(
                new FilterChainComparable<>(order, chain)
        );
        FilterChain<Context> preChain = null;
        for (FilterChainComparable<Context> current : sortedSet) {
            FilterChain<Context> currentFilterChain = current.getFilterChain();
            if (preChain == null) {
                preChain = currentFilterChain;
                continue;
            }
            preChain.setNext(currentFilterChain);
            preChain = currentFilterChain;
        }
        return this;
    }

    @Override
    public void flush() {
        sortedSet.first().getFilterChain().handle(filterContext);
    }
}
