package com.medusa.gruul.common.addon.supporter.invoker;

import java.lang.reflect.Method;

/**
 * 插件驱动执行器
 */
interface IAddonSupporterMethodInvoker {
    /**
     * 执行方法
     *
     * @param proxy  代理对象
     * @param method 执行方法
     * @param args   入参
     * @return 执行结果
     * @throws Throwable 异常
     */
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}