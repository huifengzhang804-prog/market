package com.medusa.gruul.global.model.strategy;

/**
 * 策略接口
 *
 * @author 张治保
 * @since 2024/4/10
 */
@FunctionalInterface
public interface IStrategy<T, P, R> {

    /**
     * return void helper
     */
    Void VOID = null;

    /**
     * 执行策略
     *
     * @param type  策略类型
     * @param param 参数
     * @return R 返回值
     */
    R execute(T type, P param);

}
