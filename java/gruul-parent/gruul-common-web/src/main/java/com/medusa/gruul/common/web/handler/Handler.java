package com.medusa.gruul.common.web.handler;

import com.medusa.gruul.global.model.strategy.AbstractStrategyFactory;

/**
 * 处理器超类
 *
 * @author 张治保
 * date 2021/11/29
 * @deprecated 请使用 {@link AbstractStrategyFactory} 替代 全部调整完毕移除这个类
 */
@Deprecated(since = "2025/05/27")
public interface Handler<T> {
    /**
     * 处理参数
     *
     * @param params 参数
     * @return 返回内容 <T>返回数据类型
     */
    T handle(Object... params);

    /**
     * 校验参数
     *
     * @param params  处理参数
     * @param classes 处理参数类型
     * @return 是否穿了错误参数  true是 false 否
     */
    default boolean hasErrorParam(Object[] params, Class<?>... classes) {
        if (classes == null || classes.length == 0) {
            return false;
        }
        if (params == null || params.length == 0 || params.length != classes.length) {
            return true;
        }
        for (int i = 0; i < classes.length; i++) {
            Class<?> aClass = classes[i];
            if (aClass == null || aClass.isAssignableFrom(params[i].getClass())) {
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * 强转参数
     *
     * @param value 被强转的对象,
     * @param type  强转后的类型
     * @return 强转后的值
     */
    default <E> E cast(Object value, Class<E> type) {
        return type.cast(value);
    }

}
