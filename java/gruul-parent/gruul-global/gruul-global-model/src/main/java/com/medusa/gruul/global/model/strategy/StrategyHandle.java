package com.medusa.gruul.global.model.strategy;

import java.util.function.Supplier;

/**
 * 策略处理器
 *
 * @author 张治保
 * @since 2024/4/10
 */
public class StrategyHandle<T, P, R> {

    /**
     * 策略提供者
     */
    private Supplier<? extends IStrategy<T, P, R>> supplier;
    /**
     * 策略
     */
    private volatile IStrategy<T, P, R> strategy;


    public StrategyHandle(Supplier<? extends IStrategy<T, P, R>> supplier) {
        this.supplier = supplier;
    }

    /**
     * 获取策略
     * 1. 如果策略为空，通过策略提供者创建策略
     * 2. 返回策略
     *
     * @return 策略
     */
    public IStrategy<T, P, R> getStrategy() {
        if (strategy == null) {
            synchronized (this) {
                if (strategy == null) {
                    strategy = supplier.get();
                    // help GC
                    supplier = null;
                }
            }
        }
        return strategy;
    }
}
