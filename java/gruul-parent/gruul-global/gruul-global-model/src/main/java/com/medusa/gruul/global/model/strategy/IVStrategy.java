package com.medusa.gruul.global.model.strategy;

/**
 * 没有返回值类型的策接口
 *
 * @author 张治保
 * @since 2024/12/12
 */
@FunctionalInterface
public interface IVStrategy<T, P> extends IStrategy<T, P, Void> {

    void exec(T type, P param);

    @Override
    default Void execute(T type, P param) {
        exec(type, param);
        return VOID;
    }
}
