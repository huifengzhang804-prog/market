package com.medusa.gruul.global.model.exception.collector;


/**
 * 当前行的错误信息收集器 ThreadLocal 保存
 *
 * @author 张治保
 * date 2021/12/15
 */
class CurrentErrorHolder {

    /**
     * 当前行的错误信息收集器
     */
    private static final ThreadLocal<CurrentErrors> LOCAL = new ThreadLocal<>();

    /**
     * 设置当前行的错误信息收集器
     *
     * @param currentErrorCollector 错误信息收集器
     */
    static void set(CurrentErrors currentErrorCollector) {
        LOCAL.set(currentErrorCollector);
    }

    /**
     * 获取当前行的错误信息收集器
     *
     * @return 错误信息收集器
     */
    static CurrentErrors get() {
        return LOCAL.get();
    }


    /**
     * 清除当前行的错误信息收集器
     */
    static void clear() {
        LOCAL.remove();
    }
}
