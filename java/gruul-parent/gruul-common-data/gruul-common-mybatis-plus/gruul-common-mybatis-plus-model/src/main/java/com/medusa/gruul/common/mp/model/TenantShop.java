package com.medusa.gruul.common.mp.model;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.function.*;

/**
 * 店铺多租户
 *
 * @author 张治保
 * date 2022/4/16
 */
public class TenantShop {

    private static final ThreadLocal<Boolean> DISABLE = new TransmittableThreadLocal<>();

    public static boolean isDisable() {
        Boolean disable = DISABLE.get();
        if (disable == null) {
            return false;
        }
        return disable;
    }


    /**
     * 禁用店铺id多租户并执行计算
     *
     * @param runnable 计算逻辑
     */
    public static void disable(Runnable runnable) {
        Boolean preDisable = DISABLE.get();
        DISABLE.set(Boolean.TRUE);
        try {
            runnable.run();
        } finally {
            DISABLE.set(preDisable);
        }
    }

    /**
     * 禁用店铺id多租户 并传参进行计算
     *
     * @param param    参数
     * @param consumer 计算逻辑
     * @param <T>      参数类型
     */
    public static <T> void disable(T param, Consumer<T> consumer) {
        Boolean preDisable = DISABLE.get();
        DISABLE.set(Boolean.TRUE);
        try {
            consumer.accept(param);
        } finally {
            DISABLE.set(preDisable);
        }
    }

    /**
     * 两个参数的
     */
    public static <T1, T2> void disable(T1 param1, T2 param2, BiConsumer<T1, T2> consumer) {
        Boolean preDisable = DISABLE.get();
        DISABLE.set(Boolean.TRUE);
        try {
            consumer.accept(param1, param2);
        } finally {
            DISABLE.set(preDisable);
        }
    }

    /**
     * 禁用店铺id多租户 并计算产出结果
     *
     * @param supplier 计算逻辑
     * @param <R>      结果类型
     */
    public static <R> R disable(Supplier<R> supplier) {
        Boolean preDisable = DISABLE.get();
        DISABLE.set(Boolean.TRUE);
        try {
            return supplier.get();
        } finally {
            DISABLE.set(preDisable);
        }
    }

    /**
     * 禁用店铺id多租户 根据传参计算产出结果
     *
     * @param param    参数
     * @param function 计算逻辑
     * @param <T>      参数类型
     * @param <R>      返回值类型
     * @return 返回结果
     */
    public static <T, R> R disable(T param, Function<T, R> function) {
        Boolean preDisable = DISABLE.get();
        DISABLE.set(Boolean.TRUE);
        try {
            return function.apply(param);
        } finally {
            DISABLE.set(preDisable);
        }
    }

    /**
     * 两个参数的
     */
    public static <T1, T2, R> R disable(T1 param1, T2 param2, BiFunction<T1, T2, R> function) {
        Boolean preDisable = DISABLE.get();
        DISABLE.set(Boolean.TRUE);
        try {
            return function.apply(param1, param2);
        } finally {
            DISABLE.set(preDisable);
        }
    }

}
