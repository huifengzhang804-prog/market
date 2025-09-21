package com.medusa.gruul.global.model.helper;

/**
 * @author 张治保
 * @since 2023/11/14
 */
public interface Classes {

    /**
     * 判断类是否存在
     *
     * @param className 全类名
     * @return 是否存在
     */
    static boolean exists(String className) {
        return load(className) != null;
    }

    /**
     * 加载类
     *
     * @param className 全类名
     * @param <T>       类型
     * @return 类
     */
    @SuppressWarnings("unchecked")
    static <T> Class<T> load(String className) {
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
