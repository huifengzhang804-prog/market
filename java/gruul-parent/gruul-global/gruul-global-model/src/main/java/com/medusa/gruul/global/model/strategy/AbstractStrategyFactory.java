package com.medusa.gruul.global.model.strategy;

import cn.hutool.core.map.MapUtil;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 策略处理器工厂
 *
 * @param <T> 策略类型
 * @param <P> 参数
 * @param <R> 结果
 * @author 张治保
 * @since 2024/4/10
 */
@RequiredArgsConstructor
public abstract class AbstractStrategyFactory<T, P, R> {

    /**
     * 策略映射
     */
    private final Map<T, StrategyHandle<T, P, R>> strategyMap = MapUtil.newHashMap();

    protected Map<T, StrategyHandle<T, P, R>> strategyMap() {
        if (MapUtil.isEmpty(strategyMap)) {
            getStrategyMap()
                    .forEach(
                            (type, supplier) -> strategyMap.put(
                                    type,
                                    new StrategyHandle<>(supplier)
                            )
                    );
        }
        return strategyMap;
    }

    /**
     * 注册策略映射
     *
     * @param type             策略类型
     * @param strategySupplier 策略提供者
     */
    public void register(T type, Supplier<? extends IStrategy<T, P, R>> strategySupplier) {
        strategyMap().put(
                type,
                new StrategyHandle<>(strategySupplier)
        );
    }

    public abstract Map<T, Supplier<? extends IStrategy<T, P, R>>> getStrategyMap();


    /**
     * 获取策略
     *
     * @param type 策略类型
     * @return 策略
     */
    public IStrategy<T, P, R> getStrategy(T type) {
        StrategyHandle<T, P, R> strategyHandle = strategyMap().get(type);
        if (strategyHandle == null) {
            throw new IllegalArgumentException("未找到对应的策略");
        }
        return strategyHandle.getStrategy();
    }

    /**
     * 执行策略
     *
     * @param type  策略类型
     * @param param 参数
     * @return 结果
     */
    public R execute(T type, P param) {
        return getStrategy(type).execute(type, param);
    }

    /**
     * 执行策略
     *
     * @param type 策略类型
     * @return 结果
     */
    public R execute(T type) {
        return execute(type, null);
    }


    /**
     * 执行策略
     *
     * @param type  策略类型
     * @param param 参数
     */
    public void exec(T type, P param) {
        execute(type, param);
    }

    /**
     * 执行策略
     *
     * @param type 策略类型
     */
    public void exec(T type) {
        exec(type, null);
    }

}
